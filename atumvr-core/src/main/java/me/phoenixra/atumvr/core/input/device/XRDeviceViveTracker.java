package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceViveTracker;
import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import me.phoenixra.atumvr.core.input.profile.tracker.ViveTrackerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Getter
public class XRDeviceViveTracker extends XRDeviceTracker implements AtumVRDeviceViveTracker {

    private final ViveTrackerRole role;

    private final ViveTrackerManager manager;

    public XRDeviceViveTracker(@NotNull XRProvider vrProvider,
                               @NotNull ViveTrackerRole role,
                               @NotNull ViveTrackerManager manager,
                               @NotNull PoseMultiAction.SubActionPose poseSubAction,
                               @Nullable HapticPulseAction hapticPulseAction) {
        super(vrProvider, role.getDeviceId(), poseSubAction, hapticPulseAction);
        this.role = role;
        this.manager = manager;
    }

    public XRDeviceViveTracker(@NotNull XRProvider vrProvider,
                               @NotNull ViveTrackerRole role,
                               @NotNull ViveTrackerManager manager) {
        super(vrProvider, role.getDeviceId());
        this.role = role;
        this.manager = manager;
    }

    @Override
    public void update() {
        if (manager.isEmulated()) {
            manager.computeEmulatedPose(role, pose);
            active = true;
            return;
        }
        super.update();
    }
}
