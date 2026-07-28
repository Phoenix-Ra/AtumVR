package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceViveTracker;
import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import me.phoenixra.atumvr.core.input.profile.tracker.ViveTrackerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
public class XRDeviceViveTracker extends XRDeviceTracker implements AtumVRDeviceViveTracker {

    private final ViveTrackerRole role;

    private final ViveTrackerProvider provider;

    public XRDeviceViveTracker(@NotNull XRProvider vrProvider,
                               @NotNull ViveTrackerRole role,
                               @NotNull ViveTrackerProvider provider,
                               @NotNull PoseMultiAction.SubActionPose poseSubAction,
                               @Nullable HapticPulseAction hapticPulseAction) {
        super(vrProvider, role.getDeviceId(), role.getBodyJoint(), poseSubAction, hapticPulseAction);
        this.role = role;
        this.provider = provider;
    }

    public XRDeviceViveTracker(@NotNull XRProvider vrProvider,
                               @NotNull ViveTrackerRole role,
                               @NotNull ViveTrackerProvider provider) {
        super(vrProvider, role.getDeviceId(), role.getBodyJoint());
        this.role = role;
        this.provider = provider;
    }

    @Override
    public void update() {
        if (provider.isEmulated()) {
            provider.getEmulator().computePose(getBodyJoint(), pose);
            active = true;
            return;
        }
        super.update();
    }
}
