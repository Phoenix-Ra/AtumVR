package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceViveTracker;
import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
public class XRDeviceViveTracker extends XRDeviceTracker implements AtumVRDeviceViveTracker {

    private final ViveTrackerRole role;

    public XRDeviceViveTracker(@NotNull XRProvider vrProvider,
                               @NotNull ViveTrackerRole role,
                               @NotNull PoseMultiAction.SubActionPose poseSubAction,
                               @Nullable HapticPulseAction hapticPulseAction) {
        super(vrProvider, role.getDeviceId(), poseSubAction, hapticPulseAction);
        this.role = role;
    }
}
