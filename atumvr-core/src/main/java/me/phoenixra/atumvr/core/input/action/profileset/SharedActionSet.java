package me.phoenixra.atumvr.core.input.action.profileset;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.api.misc.pose.VRPoseRecord;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRMultiAction;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;

import java.util.List;

import static me.phoenixra.atumvr.core.input.action.XRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.XRAction.RIGHT_HAND_PATH;

@Getter
public class SharedActionSet extends XRActionSet {

    public static final ActionIdentifier POSE_HAND_AIM_LEFT = new ActionIdentifier("hand.aim.left", ControllerType.LEFT);

    public static final ActionIdentifier POSE_HAND_AIM_RIGHT = new ActionIdentifier("hand.aim.right", ControllerType.RIGHT);


    public static final ActionIdentifier POSE_HAND_GRIP_LEFT = new ActionIdentifier("hand.grip.left", ControllerType.LEFT);

    public static final ActionIdentifier POSE_HAND_GRIP_RIGHT = new ActionIdentifier("hand.grip.right", ControllerType.RIGHT);

    // Haptics
    private HapticPulseAction hapticPulse;

    // Hand poses
    private PoseMultiAction handPoseAim;
    private PoseMultiAction handPoseGrip;


    public SharedActionSet(XRProvider provider) {
        super(provider, "shared", "Shared set", 0);
    }

    @Override
    protected List<XRAction> loadActions(XRProvider provider) {
        var supportedProfiles = XRInteractionProfile.getSupported(provider);
        // -------- HAPTICS --------
        hapticPulse = new HapticPulseAction(
                provider, this,
                "haptic_pulse", "Haptic Pulse"
        ).putDefaultBindings(supportedProfiles, "output/haptic");

        // -------- HAND POSES --------
        handPoseAim = new PoseMultiAction(
                provider, this,
                new ActionIdentifier("hand_aim"),
                "Hand Aim",
                List.of(
                        new XRMultiAction.SubAction<>(
                                POSE_HAND_AIM_LEFT,
                                LEFT_HAND_PATH,
                                VRPoseRecord.EMPTY
                        ).putDefaultBindings(supportedProfiles, "input/aim/pose"),
                        new XRMultiAction.SubAction<>(
                                POSE_HAND_AIM_RIGHT,
                                RIGHT_HAND_PATH,
                                VRPoseRecord.EMPTY
                        ).putDefaultBindings(supportedProfiles, "input/aim/pose")
                )
        );

        handPoseGrip = new PoseMultiAction(
                provider, this,
                new ActionIdentifier("hand_grip"),
                "Hand Grip",
                List.of(
                        new XRMultiAction.SubAction<>(
                                POSE_HAND_GRIP_LEFT,
                                LEFT_HAND_PATH,
                                VRPoseRecord.EMPTY
                        ).putDefaultBindings(supportedProfiles, "input/grip/pose"),
                        new XRMultiAction.SubAction<>(
                                POSE_HAND_GRIP_RIGHT,
                                RIGHT_HAND_PATH,
                                VRPoseRecord.EMPTY
                        ).putDefaultBindings(supportedProfiles, "input/grip/pose")
                )
        );

        return List.of(hapticPulse, handPoseAim, handPoseGrip);
    }
}
