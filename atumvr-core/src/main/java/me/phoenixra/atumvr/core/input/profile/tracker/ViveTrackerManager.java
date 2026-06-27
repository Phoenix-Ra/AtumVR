package me.phoenixra.atumvr.core.input.profile.tracker;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.input.device.AtumVRDevice;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPoseMutable;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import me.phoenixra.atumvr.core.input.device.XRDeviceHMD;
import me.phoenixra.atumvr.core.input.device.XRDeviceViveTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViveTrackerManager {

    @Getter
    private final XRProvider vrProvider;

    @Getter
    @Nullable
    private final ViveTrackerActionSet actionSet;

    @Getter
    private final List<ViveTrackerRole> roles;

    @Getter
    private final Map<ViveTrackerRole, XRDeviceViveTracker> devicesMap = new HashMap<>();

    @Getter
    private final boolean supported;

    @Getter
    @Setter
    private volatile boolean emulated = false;

    @Getter
    @Setter
    @NotNull
    private volatile EmulatedTrackerPreset emulationPreset = EmulatedTrackerPreset.IDLE;

    // ---- per-frame emulation anchor cache (render thread only) ----
    private long anchorFrameTime = Long.MIN_VALUE;
    private final Vector3f anchorPos = new Vector3f();
    private final Quaternionf anchorYaw = new Quaternionf();
    private float animSeconds;
    private final Vector3f scratchOffset = new Vector3f();
    private final Vector3f scratchWorld = new Vector3f();
    private final Vector3f scratchForward = new Vector3f();
    private final Matrix4f scratchMatrix = new Matrix4f();


    public ViveTrackerManager(@NotNull XRProvider vrProvider) {
        this(vrProvider, ViveTrackerRole.DEFAULT_FULL_BODY);
    }

    public ViveTrackerManager(@NotNull XRProvider vrProvider,
                              @NotNull List<ViveTrackerRole> roles) {
        this.vrProvider = vrProvider;
        this.supported = vrProvider.getInputHandler()
                .getSupportedProfileTypes()
                .contains(VRInteractionProfileType.VIVE_TRACKER);

        this.roles = List.copyOf(roles);
        this.actionSet = (supported && !this.roles.isEmpty())
                ? new ViveTrackerActionSet(vrProvider, this.roles)
                : null;
    }


    public List<XRActionSet> getActionSets() {
        return actionSet == null ? List.of() : List.of(actionSet);
    }

    public List<XRDeviceViveTracker> createDevices() {
        devicesMap.clear();
        List<XRDeviceViveTracker> devices = new ArrayList<>(roles.size());
        for (ViveTrackerRole role : roles) {
            PoseMultiAction.SubActionPose sub =
                    actionSet == null ? null : actionSet.getPoseSubAction(role);
            XRDeviceViveTracker device = (sub == null)
                    ? new XRDeviceViveTracker(vrProvider, role, this)
                    : new XRDeviceViveTracker(vrProvider, role, this, sub, actionSet.getTrackerHaptic());
            devices.add(device);
            devicesMap.put(role, device);
        }
        return devices;
    }

    public void computeEmulatedPose(@NotNull ViveTrackerRole role, @NotNull AtumVRPoseMutable out) {
        refreshAnchor();

        emulationPreset.offset(role, animSeconds, scratchOffset);
        anchorYaw.transform(scratchOffset, scratchWorld);
        scratchWorld.add(anchorPos);

        scratchMatrix.translationRotate(
                scratchWorld.x(), scratchWorld.y(), scratchWorld.z(),
                anchorYaw
        );
        out.update(scratchMatrix, anchorYaw, scratchWorld);
    }

    private void refreshAnchor() {
        long frameTime = vrProvider.getXrDisplayTime();
        if (frameTime == anchorFrameTime) {
            return;
        }
        anchorFrameTime = frameTime;
        animSeconds = (float) (frameTime * 1.0e-9);

        AtumVRDevice hmd = vrProvider.getInputHandler().getDevice(XRDeviceHMD.ID);
        if (hmd != null && hmd.isActive()) {
            AtumVRPose hmdPose = hmd.getPose();
            anchorPos.set(hmdPose.position());

            hmdPose.orientation().transform(0f, 0f, -1f, scratchForward);
            float yaw = (float) Math.atan2(-scratchForward.x(), -scratchForward.z());
            anchorYaw.identity().rotateY(yaw);
        } else {
            anchorPos.set(0f, 0f, 0f);
            anchorYaw.identity();
        }
    }
}
