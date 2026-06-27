package me.phoenixra.atumvr.core.input.profile.tracker;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPoseRecord;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Getter
public class ViveTrackerActionSet extends XRActionSet {

    private final List<ViveTrackerRole> roles;

    private Map<ViveTrackerRole, PoseMultiAction.SubActionPose> poseByRole;

    private PoseMultiAction trackerPose;
    private HapticPulseAction trackerHaptic;

    public ViveTrackerActionSet(@NotNull XRProvider vrProvider,
                                @NotNull List<ViveTrackerRole> roles) {
        super(vrProvider, "vive_trackers", "Vive Trackers", 0);
        this.roles = List.copyOf(roles);
    }

    @Override
    protected List<XRAction> loadActions(@NotNull XRProvider vrProvider) {
        List<PoseMultiAction.SubActionPose> subActions = new ArrayList<>(roles.size());
        List<String> rolePaths = new ArrayList<>(roles.size());
        poseByRole = new LinkedHashMap<>();

        for (ViveTrackerRole role : roles) {
            PoseMultiAction.SubActionPose sub = new PoseMultiAction.SubActionPose(
                    new VRActionIdentifier("tracker.pose." + role.getKey()),
                    role.getUserPath(),
                    AtumVRPoseRecord.EMPTY
            ).putDefaultBindings(VRInteractionProfileType.VIVE_TRACKER, "input/grip/pose");

            subActions.add(sub);
            rolePaths.add(role.getUserPath());
            poseByRole.put(role, sub);
        }

        trackerPose = new PoseMultiAction(
                vrProvider, this,
                new VRActionIdentifier("tracker.pose"),
                "Tracker Pose",
                subActions
        );

        trackerHaptic = new HapticPulseAction(
                vrProvider, this,
                "tracker.haptic", "Tracker Haptic",
                rolePaths
        ).putDefaultBindings(VRInteractionProfileType.VIVE_TRACKER, "output/haptic");

        return List.of(trackerPose, trackerHaptic);
    }

    @Nullable
    public PoseMultiAction.SubActionPose getPoseSubAction(@NotNull ViveTrackerRole role) {
        return poseByRole == null ? null : poseByRole.get(role);
    }
}
