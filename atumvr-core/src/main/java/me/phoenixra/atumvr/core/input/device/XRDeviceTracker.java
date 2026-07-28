package me.phoenixra.atumvr.core.input.device;

import lombok.AccessLevel;
import lombok.Getter;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceTracker;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.BiConsumer;


@Getter
public class XRDeviceTracker extends XRDevice implements AtumVRDeviceTracker, AtumVRBodyView {

    @Nullable
    private final AtumVRBodyJoint bodyJoint;

    @Getter(AccessLevel.NONE)
    private final Set<AtumVRBodyJoint> joints;

    @Nullable
    private final PoseMultiAction.SubActionPose poseSubAction;

    @Nullable
    private final HapticPulseAction hapticPulseAction;

    public XRDeviceTracker(@NotNull XRProvider vrProvider,
                           @NotNull String deviceId,
                           @Nullable AtumVRBodyJoint bodyJoint,
                           @NotNull PoseMultiAction.SubActionPose poseSubAction,
                           @Nullable HapticPulseAction hapticPulseAction) {
        super(vrProvider, deviceId);
        this.bodyJoint = bodyJoint;
        this.joints = bodyJoint == null ? Set.of() : Set.of(bodyJoint);
        this.poseSubAction = poseSubAction;
        this.hapticPulseAction = hapticPulseAction;
    }

    /**
     * No OpenXR pose action.
     * Subclasses must override update() to drive the pose
     *
     * @param vrProvider the VR provider
     * @param deviceId   the device ID
     * @param bodyJoint  the joint this tracker is attached to, or null
     */
    protected XRDeviceTracker(@NotNull XRProvider vrProvider,
                              @NotNull String deviceId,
                              @Nullable AtumVRBodyJoint bodyJoint) {
        super(vrProvider, deviceId);
        this.bodyJoint = bodyJoint;
        this.joints = bodyJoint == null ? Set.of() : Set.of(bodyJoint);
        this.poseSubAction = null;
        this.hapticPulseAction = null;
    }

    @Override
    public void update() {
        if (poseSubAction == null) {
            active = false;
            return;
        }
        pose.update(poseSubAction.getPose());
        active = poseSubAction.isActive();
    }

    // -------- BODY JOINTS --------

    @Override
    public @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint) {
        return active && joint == bodyJoint ? pose : null;
    }

    @Override
    public void collectJoints(@NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        if (active && bodyJoint != null) {
            sink.accept(bodyJoint, pose);
        }
    }

    @Override
    public @NotNull Set<AtumVRBodyJoint> getTrackedJoints() {
        return active ? joints : Set.of();
    }

    @Override
    public void triggerHapticPulse(float frequency, float amplitude, long durationNanoSec) {
        if (hapticPulseAction == null) {
            return;
        }
        hapticPulseAction.triggerHapticPulse(
                poseSubAction.getPathName(),
                frequency,
                amplitude,
                durationNanoSec
        );
    }
}
