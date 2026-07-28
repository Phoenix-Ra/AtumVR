package me.phoenixra.atumvr.core.input.body;

import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.input.device.AtumVRDevice;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceController;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceHMD;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;


public class XRCommonBodyView implements AtumVRBodyView {

    private final XRProvider vrProvider;

    public XRCommonBodyView(@NotNull XRProvider vrProvider) {
        this.vrProvider = vrProvider;
    }

    @Override
    public @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint) {
        return switch (joint) {
            case HEAD, NECK -> getJointPose(AtumVRDeviceHMD.ID, joint);
            case LEFT_HAND -> getJointPose(AtumVRDeviceController.ID_LEFT, joint);
            case RIGHT_HAND -> getJointPose(AtumVRDeviceController.ID_RIGHT, joint);
            default -> null;
        };
    }

    @Override
    public void collectJoints(@NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        collectJoint(AtumVRBodyJoint.HEAD, sink);
        collectJoint(AtumVRBodyJoint.NECK, sink);
        collectJoint(AtumVRBodyJoint.LEFT_HAND, sink);
        collectJoint(AtumVRBodyJoint.RIGHT_HAND, sink);
    }

    private void collectJoint(@NotNull AtumVRBodyJoint joint,
                              @NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        AtumVRPose pose = getJointPose(joint);
        if (pose != null) {
            sink.accept(joint, pose);
        }
    }

    private @Nullable AtumVRPose getJointPose(@NotNull String deviceId,
                                              @NotNull AtumVRBodyJoint joint) {
        AtumVRDevice device = vrProvider.getInputHandler().getDevice(deviceId);
        return device instanceof AtumVRBodyView view
                ? view.getJointPose(joint)
                : null;
    }
}
