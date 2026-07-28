package me.phoenixra.atumvr.core.input.profile.tracker;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import me.phoenixra.atumvr.core.input.body.EmulatedBodyPreset;
import me.phoenixra.atumvr.core.input.body.XRBodyEmulator;
import me.phoenixra.atumvr.core.input.device.XRDeviceViveTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ViveTrackerProvider implements XRTrackerProvider {

    @Getter
    private final XRProvider vrProvider;

    @Getter
    @Nullable
    private final ViveTrackerActionSet actionSet;

    @Getter
    private final List<ViveTrackerRole> roles;

    @Getter
    private final Map<ViveTrackerRole, XRDeviceViveTracker> devicesMap = new LinkedHashMap<>();

    private final EnumMap<AtumVRBodyJoint, XRDeviceViveTracker> jointDevices =
            new EnumMap<>(AtumVRBodyJoint.class);


    @Getter
    private final boolean hardwareSupported;

    @Getter
    @Setter
    private volatile boolean emulated = false;

    @Getter
    private final XRBodyEmulator emulator;


    public ViveTrackerProvider(@NotNull XRProvider vrProvider) {
        this(vrProvider, ViveTrackerRole.DEFAULT_FULL_BODY);
    }

    public ViveTrackerProvider(@NotNull XRProvider vrProvider,
                               @NotNull List<ViveTrackerRole> roles) {
        this.vrProvider = vrProvider;
        this.hardwareSupported = vrProvider.getInputHandler()
                .getSupportedProfileTypes()
                .contains(VRInteractionProfileType.VIVE_TRACKER);

        this.roles = List.copyOf(roles);
        this.actionSet = (hardwareSupported && !this.roles.isEmpty())
                ? new ViveTrackerActionSet(vrProvider, this.roles)
                : null;
        this.emulator = new XRBodyEmulator(vrProvider);
    }


    @Override
    public boolean isSupported() {
        return (hardwareSupported || emulated) && !roles.isEmpty();
    }

    @Override
    public @NotNull List<XRActionSet> getActionSets() {
        return actionSet == null ? List.of() : List.of(actionSet);
    }

    @Override
    public void onAttached() {
        devicesMap.clear();
        jointDevices.clear();
        for (ViveTrackerRole role : roles) {
            PoseMultiAction.SubActionPose sub =
                    actionSet == null ? null : actionSet.getPoseSubAction(role);
            XRDeviceViveTracker device = (sub == null)
                    ? new XRDeviceViveTracker(vrProvider, role, this)
                    : new XRDeviceViveTracker(vrProvider, role, this, sub, actionSet.getTrackerHaptic());
            devicesMap.put(role, device);

            if (role.getBodyJoint() != null) {
                jointDevices.putIfAbsent(role.getBodyJoint(), device);
            }
        }
    }

    @Override
    public @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint) {
        XRDeviceViveTracker device = jointDevices.get(joint);
        return device == null ? null : device.getJointPose(joint);
    }


    @Override
    public @NotNull Collection<XRDeviceViveTracker> getDevices() {
        return devicesMap.values();
    }

    @Override
    public void destroy() {
        devicesMap.clear();
        jointDevices.clear();
    }

    /**
     * Get the device of a role
     *
     * @param role the role
     * @return the device, or null before the provider is attached
     */
    public @Nullable XRDeviceViveTracker getDevice(@NotNull ViveTrackerRole role) {
        return devicesMap.get(role);
    }

    public @NotNull EmulatedBodyPreset getEmulationPreset() {
        return emulator.getPreset();
    }

    public void setEmulationPreset(@NotNull EmulatedBodyPreset preset) {
        emulator.setPreset(preset);
    }
}
