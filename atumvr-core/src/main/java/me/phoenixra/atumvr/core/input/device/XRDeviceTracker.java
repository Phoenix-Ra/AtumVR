package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceTracker;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
public class XRDeviceTracker extends XRDevice implements AtumVRDeviceTracker {

    private final PoseMultiAction.SubActionPose poseSubAction;

    @Nullable
    private final HapticPulseAction hapticPulseAction;

    public XRDeviceTracker(@NotNull XRProvider vrProvider,
                           @NotNull String deviceId,
                           @NotNull PoseMultiAction.SubActionPose poseSubAction,
                           @Nullable HapticPulseAction hapticPulseAction) {
        super(vrProvider, deviceId);
        this.poseSubAction = poseSubAction;
        this.hapticPulseAction = hapticPulseAction;
    }

    @Override
    public void update() {
        pose.update(poseSubAction.getPose());
        active = poseSubAction.isActive();
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
