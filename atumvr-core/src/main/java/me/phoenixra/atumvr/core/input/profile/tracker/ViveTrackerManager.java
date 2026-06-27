package me.phoenixra.atumvr.core.input.profile.tracker;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.device.XRDeviceViveTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
public class ViveTrackerManager {

    private final XRProvider vrProvider;

    @Nullable
    private final ViveTrackerActionSet actionSet;


    private final List<ViveTrackerRole> roles;

    private final Map<ViveTrackerRole, XRDeviceViveTracker> devicesMap;

    private final boolean supported;


    public ViveTrackerManager(@NotNull XRProvider vrProvider) {
        this(vrProvider, ViveTrackerRole.DEFAULT_FULL_BODY);
    }

    public ViveTrackerManager(@NotNull XRProvider vrProvider,
                              @NotNull List<ViveTrackerRole> roles) {
        this.vrProvider = vrProvider;
        this.supported = vrProvider.getInputHandler()
                .getSupportedProfileTypes()
                .contains(VRInteractionProfileType.VIVE_TRACKER);

        this.roles = (supported && !roles.isEmpty())
                ? List.copyOf(roles)
                : List.of();

        this.actionSet = this.roles.isEmpty()
                ? null
                : new ViveTrackerActionSet(vrProvider, this.roles);

        this.devicesMap = new HashMap<>();
    }


    public List<XRActionSet> getActionSets() {
        return actionSet == null ? List.of() : List.of(actionSet);
    }

    public List<XRDeviceViveTracker> createDevices() {
        if (actionSet == null) {
            return List.of();
        }
        devicesMap.clear();
        List<XRDeviceViveTracker> devices = new ArrayList<>(roles.size());
        for (ViveTrackerRole role : roles) {
            PoseMultiAction.SubActionPose sub = actionSet.getPoseSubAction(role);
            if (sub == null) {
                continue;
            }
            var device = new XRDeviceViveTracker(
                    vrProvider, role, sub, actionSet.getTrackerHaptic()
            );
            devices.add(device);
            devicesMap.put(role, device);
        }
        return devices;
    }
}
