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

    private final Vector3f scratchOffset = new Vector3f();
    private final Vector3f scratchWorld = new Vector3f();
    private final Vector3f scratchForward = new Vector3f();
    private final Matrix4f scratchMatrix = new Matrix4f();

    public XRBodyEmulator(@NotNull XRProvider vrProvider) {
        this.vrProvider = vrProvider;
    }


    public void computePose(@Nullable AtumVRBodyJoint joint, @NotNull AtumVRPoseMutable out) {
        refreshAnchor();

        preset.offset(joint, animSeconds, scratchOffset);
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

        AtumVRDevice hmd = vrProvider.getInputHandler().getDevice(AtumVRDeviceHMD.ID);
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
