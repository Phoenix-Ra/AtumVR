package me.phoenixra.atumvr.core.input.profile.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.action.data.VRActionData;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataButton;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataVec2;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.types.multi.BoolButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.Vec2MultiAction;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.api.input.profile.types.ValveIndexProfile.*;
import static me.phoenixra.atumvr.core.input.action.XRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.XRAction.RIGHT_HAND_PATH;

@Getter
public class ValveIndexXRProfile extends XRInteractionProfile {
    private static final VRInteractionProfileType PROFILE = VRInteractionProfileType.VALVE_INDEX;



    // System Buttons
    private BoolButtonMultiAction systemButton;
    private BoolButtonMultiAction systemButtonTouch;

    // Button A
    private BoolButtonMultiAction buttonA;
    private BoolButtonMultiAction buttonTouchA;

    // Button B
    private BoolButtonMultiAction buttonB;
    private BoolButtonMultiAction buttonTouchB;

    // Grip
    private FloatButtonMultiAction gripValue;
    private FloatButtonMultiAction gripForce;

    // Trigger button
    private FloatButtonMultiAction triggerValue;
    private BoolButtonMultiAction triggerButton;
    private BoolButtonMultiAction triggerButtonTouch;

    // Thumb Stick
    private Vec2MultiAction thumbStick;
    private BoolButtonMultiAction thumbStickButton;
    private BoolButtonMultiAction thumbStickButtonTouch;

    // Trackpad
    private Vec2MultiAction trackpad;
    private BoolButtonMultiAction trackpadTouch;
    private FloatButtonMultiAction trackpadForce;


    private Map<VRActionIdentifier, VRActionData> actionMap;
    private Map<VRActionIdentifier, VRActionDataButton> buttonMap;
    private Map<VRActionIdentifier, VRActionDataVec2> vec2Map;

    public ValveIndexXRProfile(XRProvider vrProvider) {
        super(vrProvider, "valve_index", "Valve Index", 0);
    }

    @Override
    protected List<XRAction> loadActions(@NotNull XRProvider vrProvider) {


        // -------- SYSTEM BUTTONS --------
        systemButton = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.system"), "System button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_SYSTEM_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/system/click"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_SYSTEM_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/system/click")
                )
        );

        systemButtonTouch = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.system.touch"), "System button touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_SYSTEM_TOUCH_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/system/touch"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_SYSTEM_TOUCH_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/system/touch")
                )
        );

        // -------- BUTTON PRIMARY --------

        buttonA = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.primary"), "Primary Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_A_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/a/click"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_A_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/a/click")
                )
        );

        buttonTouchA = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.a.touch"), "'A' Button Touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_A_TOUCH_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/a/touch"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_A_TOUCH_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/a/touch")
                )
        );

        // -------- BUTTON SECONDARY --------

        buttonB = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.b"), "'B' Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_B_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/b/click"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_B_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/b/click")
                )
        );

        buttonTouchB = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.b.touch"), "B Button Touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_B_TOUCH_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/b/touch"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_B_TOUCH_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/b/touch")
                )
        );

        // -------- GRIP --------
        gripValue = new FloatButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.grip"),
                "Grip Value",
                0.9f,
                0.85f,
                List.of(
                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_GRIP_LEFT,
                                LEFT_HAND_PATH,
                                0f
                        ).putDefaultBindings(PROFILE, "input/squeeze/value"),

                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_GRIP_RIGHT,
                                RIGHT_HAND_PATH,
                                0f
                        ).putDefaultBindings(PROFILE, "input/squeeze/value")
                )
        );

        gripForce = new FloatButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.grip.force"),
                "Grip Force button",
                0.9f,
                0.85f,
                List.of(
                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_GRIP_FORCE_LEFT,
                                LEFT_HAND_PATH,
                                0f
                        ).putDefaultBindings(PROFILE, "input/squeeze/force"),

                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_GRIP_FORCE_RIGHT,
                                RIGHT_HAND_PATH,
                                0f
                        ).putDefaultBindings(PROFILE, "input/squeeze/force")
                )
        );


        // -------- TRIGGER BUTTON --------
        triggerValue = new FloatButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.trigger"),
                "Trigger Value",
                0.7f,
                0.65f,
                List.of(
                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_TRIGGER_LEFT,
                                LEFT_HAND_PATH,
                                0f
                        ).putDefaultBindings(PROFILE, "input/trigger/value"),

                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_TRIGGER_RIGHT,
                                RIGHT_HAND_PATH,
                                0f
                        ).putDefaultBindings(PROFILE, "input/trigger/value")
                )
        );

        triggerButton = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.trigger.click"), "Trigger Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_TRIGGER_CLICK_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/trigger/click"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_TRIGGER_CLICK_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/trigger/click")
                )
        );

        triggerButtonTouch = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.trigger.touch"), "Trigger Button Touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_TRIGGER_TOUCH_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/trigger/touch"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_TRIGGER_TOUCH_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/trigger/touch")
                )
        );



        // -------- THUMB STICK --------

        thumbStick = new Vec2MultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("vec2.thumbstick"), "Thumbstick",
                List.of(
                        new Vec2MultiAction.SubActionVec2(
                                VEC2_THUMBSTICK_LEFT,
                                LEFT_HAND_PATH,
                                new Vector2f(0,0)
                        ).putDefaultBindings(PROFILE, "input/thumbstick"),

                        new Vec2MultiAction.SubActionVec2(
                                VEC2_THUMBSTICK_RIGHT,
                                RIGHT_HAND_PATH,
                                new Vector2f(0,0)
                        ).putDefaultBindings(PROFILE, "input/thumbstick")
                )
        );

        thumbStickButton = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.thumbstick"), "ThumbStick Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_THUMBSTICK_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/thumbstick/click"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_THUMBSTICK_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/thumbstick/click")
                )
        );

        thumbStickButtonTouch = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.thumbstick.touch"), "ThumbStick Button Touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_THUMBSTICK_TOUCH_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/thumbstick/touch"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_THUMBSTICK_TOUCH_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/thumbstick/touch")
                )
        );

        // -------- TRACKPAD --------

        trackpad = new Vec2MultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("vec2.trackpad"), "Trackpad",
                List.of(
                        new Vec2MultiAction.SubActionVec2(
                                VEC2_TRACKPAD_LEFT,
                                LEFT_HAND_PATH,
                                new Vector2f(0,0)
                        ).putDefaultBindings(PROFILE, "input/trackpad"),

                        new Vec2MultiAction.SubActionVec2(
                                VEC2_TRACKPAD_RIGHT,
                                RIGHT_HAND_PATH,
                                new Vector2f(0,0)
                        ).putDefaultBindings(PROFILE, "input/trackpad")
                )
        );

        trackpadTouch = new BoolButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.trackpad.touch"), "Trackpad Touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_TRACKPAD_TOUCH_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/trackpad/touch"),

                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_TRACKPAD_TOUCH_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/trackpad/touch")
                )
        );

        trackpadForce = new FloatButtonMultiAction(
                vrProvider,
                this,
                new VRActionIdentifier("button.trackpad.force"),
                "Trackpad Force button",
                0.3f,
                0.1f,
                List.of(
                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_TRACKPAD_FORCE_LEFT,
                                LEFT_HAND_PATH,
                                0f
                        ).putDefaultBindings(PROFILE, "input/trackpad/force"),

                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_TRACKPAD_FORCE_RIGHT,
                                RIGHT_HAND_PATH,
                                0f
                        ).putDefaultBindings(PROFILE, "input/trackpad/force")
                )
        );


        List<VRActionDataButton> listButton = new ArrayList<>();
        listButton.addAll(systemButton.getSubActionsAsButton());
        listButton.addAll(systemButtonTouch.getSubActionsAsButton());
        listButton.addAll(buttonA.getSubActionsAsButton());
        listButton.addAll(buttonTouchA.getSubActionsAsButton());
        listButton.addAll(buttonB.getSubActionsAsButton());
        listButton.addAll(buttonTouchB.getSubActionsAsButton());
        listButton.addAll(gripValue.getSubActionsAsButton());
        listButton.addAll(gripForce.getSubActionsAsButton());
        listButton.addAll(triggerValue.getSubActionsAsButton());
        listButton.addAll(triggerButton.getSubActionsAsButton());
        listButton.addAll(triggerButtonTouch.getSubActionsAsButton());
        listButton.addAll(thumbStickButton.getSubActionsAsButton());
        listButton.addAll(thumbStickButtonTouch.getSubActionsAsButton());
        listButton.addAll(trackpadTouch.getSubActionsAsButton());
        listButton.addAll(trackpadForce.getSubActionsAsButton());

        buttonMap = new LinkedHashMap<>();
        for(var entry : listButton){
            buttonMap.put(entry.getId(), entry);
        }

        List<VRActionDataVec2> listVec2 = new ArrayList<>();
        listVec2.addAll(thumbStick.getSubActionsAsVec2());
        listVec2.addAll(trackpad.getSubActionsAsVec2());

        vec2Map = new LinkedHashMap<>();
        for(var entry : listVec2){
            vec2Map.put(entry.getId(), entry);
        }

        actionMap = new LinkedHashMap<>();
        actionMap.putAll(buttonMap);
        actionMap.putAll(vec2Map);

        return List.of(
                systemButton, systemButtonTouch,
                buttonA, buttonTouchA,
                buttonB, buttonTouchB,
                gripValue, gripForce,
                triggerValue, triggerButton, triggerButtonTouch,
                thumbStick, thumbStickButton, thumbStickButtonTouch,
                trackpad, trackpadTouch, trackpadForce
        );
    }

    @Override
    public @NotNull VRActionDataButton getTriggerButton(@NotNull ControllerType controllerType) {
        return triggerValue.getHandSubaction(controllerType);
    }

    @Override
    public Collection<VRActionIdentifier> getActionIds() {
        return Collections.unmodifiableCollection(actionMap.keySet());
    }

    @Override
    public @Nullable VRActionData getAction(@NotNull VRActionIdentifier id) {
        return actionMap.get(id);
    }


    @Override
    public Collection<VRActionIdentifier> getButtonIds() {
        return Collections.unmodifiableCollection(buttonMap.keySet());
    }

    @Override
    public VRActionDataButton getButton(@NotNull VRActionIdentifier id) {
        return buttonMap.get(id);
    }


    @Override
    public Collection<VRActionIdentifier> getVec2Ids() {
        return Collections.unmodifiableCollection(vec2Map.keySet());
    }

    @Override
    public VRActionDataVec2 getVec2(@NotNull VRActionIdentifier id) {
        return vec2Map.get(id);
    }


    @Override
    public @NotNull VRInteractionProfileType getType() {
        return PROFILE;
    }
}
