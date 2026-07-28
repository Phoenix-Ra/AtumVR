package me.phoenixra.atumvr.core.input.device;

import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceHMD;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPoseMutable;
import me.phoenixra.atumvr.core.utils.XRUtils;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.openxr.XrPosef;
import org.lwjgl.openxr.XrSpace;
import org.lwjgl.openxr.XrSpaceLocation;
import org.lwjgl.openxr.XrView;
import org.lwjgl.system.MemoryStack;

import java.util.Set;
import java.util.function.BiConsumer;


public class XRDeviceHMD extends XRDevice implements AtumVRDeviceHMD, AtumVRBodyView {
    /**
     * VR device identifier for HMD
     */
    public static final String ID = "hmd";

    private static final Set<AtumVRBodyJoint> JOINTS =
            Set.of(AtumVRBodyJoint.HEAD, AtumVRBodyJoint.NECK);

    private final AtumVRPoseMutable eyeLeftPose = new AtumVRPoseMutable();

    private final AtumVRPoseMutable eyeRightPose = new AtumVRPoseMutable();


    private final Vector3f neckOffset = new Vector3f(0f, -0.12f, 0.08f);

    private final AtumVRPoseMutable neckPose = new AtumVRPoseMutable();
    private final Vector3f neckPosition = new Vector3f();
    private final Quaternionf neckOrientation = new Quaternionf();
    private final Vector3f scratchForward = new Vector3f();
    private final Matrix4f scratchMatrix = new Matrix4f();
    private final Quaternionf scratchOrientation = new Quaternionf();
    private final Vector3f scratchPosition = new Vector3f();

    private final XrSpace space;

    public XRDeviceHMD(XRProvider vrProvider) {
        super(vrProvider, ID);
        space = vrProvider.getSession().getXrViewSpace();
    }



    @Override
    public void update() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrSpaceLocation loc = XRUtils.xrLocationFromSpace(
                    vrProvider,
                    space,
                    stack
            );

            active = loc != null;

            if (active) {
                writePose(loc.pose(), pose);
                updateNeckPose();
            }
        }

        writePose(getXrView(EyeType.LEFT).pose(), eyeLeftPose);
        writePose(getXrView(EyeType.RIGHT).pose(), eyeRightPose);
    }

    private void writePose(@NotNull XrPosef xrPose, @NotNull AtumVRPoseMutable out) {
        out.update(
                XRUtils.normalizeXrPose(xrPose, scratchMatrix),
                XRUtils.normalizeXrQuaternion(xrPose.orientation(), scratchOrientation),
                XRUtils.normalizeXrVector(xrPose.position$(), scratchPosition)
        );
    }

    @Override
    public @NotNull AtumVRPose getEyePose(@NotNull EyeType eyeType) {
        if(eyeType == EyeType.LEFT){
            return eyeLeftPose;
        }else{
            return eyeRightPose;
        }
    }

    // -------- BODY JOINTS --------

    @Override
    public @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint) {
        if (!active) {
            return null;
        }
        return switch (joint) {
            case HEAD -> pose;
            case NECK -> neckPose;
            default -> null;
        };
    }

    @Override
    public void collectJoints(@NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        if (!active) {
            return;
        }
        sink.accept(AtumVRBodyJoint.HEAD, pose);
        sink.accept(AtumVRBodyJoint.NECK, neckPose);
    }

    @Override
    public @NotNull Set<AtumVRBodyJoint> getTrackedJoints() {
        return active ? JOINTS : Set.of();
    }


    public @NotNull AtumVRPose getNeckPose() {
        return neckPose;
    }


    public void setNeckOffset(@NotNull Vector3fc offset) {
        neckOffset.set(offset);
    }

    public @NotNull Vector3fc getNeckOffset() {
        return neckOffset;
    }

    private void updateNeckPose() {
        pose.orientation().transform(neckOffset, neckPosition)
                .add(pose.position());

        pose.orientation().transform(0f, 0f, -1f, scratchForward);
        float yaw = (float) Math.atan2(-scratchForward.x(), -scratchForward.z());
        neckOrientation.identity().rotateY(yaw);

        scratchMatrix.translationRotate(
                neckPosition.x(), neckPosition.y(), neckPosition.z(),
                neckOrientation
        );
        neckPose.update(scratchMatrix, neckOrientation, neckPosition);
    }

    /**
     * Get XR view for specified eye
     * @param eyeType the type of eye (RIGHT, LEFT)
     * @return XrView
     */
    public XrView getXrView(EyeType eyeType){
        return vrProvider.getSession().getSwapChain()
                .getXrViewBuffer().get(eyeType.getIndex());
    }
}
