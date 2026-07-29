package me.phoenixra.atumvr.core.input.body;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.input.device.AtumVRDevice;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceHMD;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPoseMutable;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;


public class XRBodyEmulator {

    private final XRProvider vrProvider;

    @Getter
    @Setter
    @NotNull
    private volatile EmulatedBodyPreset preset = EmulatedBodyPreset.IDLE;

    private long anchorFrameTime = Long.MIN_VALUE;
    private final Vector3f anchorPos = new Vector3f();
    private final Quaternionf anchorYaw = new Quaternionf();
    private float animSeconds;

    private final Vector3f cachedOffset = new Vector3f();
    private final Vector3f cachedPosition = new Vector3f();
    private final Vector3f cachedForward = new Vector3f();
    private final Matrix4f cachedMatrix = new Matrix4f();

    public XRBodyEmulator(@NotNull XRProvider vrProvider) {
        this.vrProvider = vrProvider;
    }


    public void computePose(@Nullable AtumVRBodyJoint joint, @NotNull AtumVRPoseMutable out) {
        refreshAnchor();

        preset.offset(joint, animSeconds, cachedOffset);
        anchorYaw.transform(cachedOffset, cachedPosition);
        cachedPosition.add(anchorPos);

        cachedMatrix.translationRotate(
                cachedPosition.x(), cachedPosition.y(), cachedPosition.z(),
                anchorYaw
        );
        out.update(cachedMatrix, anchorYaw, cachedPosition);
    }

    private void refreshAnchor() {
        long frameTime = vrProvider.getXrDisplayTime();
        if (frameTime == anchorFrameTime) {
            return;
        }
        anchorFrameTime = frameTime;
        animSeconds = (float) (frameTime * 1.0e-9);

        AtumVRDevice hmd = vrProvider.getInputHandler().getDevice(AtumVRDeviceHMD.ID);
        if (hmd != null && hmd.isActive()) {
            AtumVRPose hmdPose = hmd.getPose();
            anchorPos.set(hmdPose.position());

            hmdPose.orientation().transform(0f, 0f, -1f, cachedForward);
            float yaw = (float) Math.atan2(-cachedForward.x(), -cachedForward.z());
            anchorYaw.identity().rotateY(yaw);
        } else {
            anchorPos.set(0f, 0f, 0f);
            anchorYaw.identity();
        }
    }
}
