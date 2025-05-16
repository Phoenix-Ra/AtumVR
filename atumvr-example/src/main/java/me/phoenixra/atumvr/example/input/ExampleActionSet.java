package me.phoenixra.atumvr.example.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.misc.pose.VRPoseRecord;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.OpenXRAction;
import me.phoenixra.atumvr.core.input.action.OpenXRMultiAction;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;


import me.phoenixra.atumvr.core.input.action.OpenXRActionSet;


import java.util.List;

import static me.phoenixra.atumvr.core.input.action.OpenXRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.OpenXRAction.RIGHT_HAND_PATH;


@Getter
public class ExampleActionSet extends OpenXRActionSet {
    // Pose actions
    private PoseMultiAction aimAction;
    private PoseMultiAction gripAction;

    // Trigger actions
    private FloatMultiAction triggerValueAction;

    private HapticPulseAction hapticPulseAction;


    public ExampleActionSet(OpenXRProvider provider) {
        super(provider, "raw", "Raw Actions", 0);
    }

    @Override
    protected List<OpenXRAction> loadActions(OpenXRProvider provider) {
        List<XRInteractionProfile> profiles = XRInteractionProfile.getSupported(provider);




        // Aim pose
        aimAction = new PoseMultiAction(provider, this, "aim", "Aim", List.of(
                new OpenXRMultiAction.SubAction<>(LEFT_HAND_PATH, VRPoseRecord.EMPTY)
                        .putDefaultBindings(profiles, "input/aim/pose"),
                new OpenXRMultiAction.SubAction<>(RIGHT_HAND_PATH, VRPoseRecord.EMPTY)
                        .putDefaultBindings(profiles, "input/aim/pose")
        ));

        // Grip pose
        gripAction = new PoseMultiAction(provider, this, "grip", "Grip", List.of(
                new OpenXRMultiAction.SubAction<>(LEFT_HAND_PATH, VRPoseRecord.EMPTY)
                        .putDefaultBindings(profiles, "input/grip/pose"),
                new OpenXRMultiAction.SubAction<>(RIGHT_HAND_PATH, VRPoseRecord.EMPTY)
                        .putDefaultBindings(profiles, "input/grip/pose")
        ));

        //haptic
        hapticPulseAction = new HapticPulseAction(
                provider, this, "haptic_pulse",
                "Haptic Pulse"
        ).putDefaultBindings(profiles, "output/haptic");


        // Trigger value
        triggerValueAction = new FloatMultiAction(provider, this, "trigger_value", "Trigger Value", List.of(
                new OpenXRMultiAction.SubAction<>(LEFT_HAND_PATH, 0f)
                        .putDefaultBindings(profiles, "input/trigger/value"),
                new OpenXRMultiAction.SubAction<>(RIGHT_HAND_PATH, 0f)
                        .putDefaultBindings(profiles, "input/trigger/value")
        ));



        return List.of(
                aimAction,
                gripAction,
                hapticPulseAction,
                triggerValueAction
        );
    }
}
