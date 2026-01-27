package me.phoenixra.atumvr.core.input.profile.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.action.data.VRActionData;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataButton;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataVec2;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.types.multi.BoolButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.Vec2MultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.core.input.action.XRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.XRAction.RIGHT_HAND_PATH;

@Getter
public class ValveIndexProfile extends XRInteractionProfile {
    private static final XRInteractionProfileType PROFILE = XRInteractionProfileType.VALVE_INDEX;

    // ---------- ACTION IDENTIFIERS ----------

    public static final VRActionIdentifier BUTTON_SYSTEM_LEFT = new VRActionIdentifier("button.system.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_SYSTEM_RIGHT = new VRActionIdentifier("button.system.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_SYSTEM_TOUCH_LEFT = new VRActionIdentifier("button.system.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_SYSTEM_TOUCH_RIGHT = new VRActionIdentifier("button.system.touch.right", ControllerType.RIGHT);


    public static final VRActionIdentifier BUTTON_A_LEFT = new VRActionIdentifier("button.a.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_A_RIGHT = new VRActionIdentifier("button.a.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_A_TOUCH_LEFT = new VRActionIdentifier("button.a.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_A_TOUCH_RIGHT = new VRActionIdentifier("button.a.touch.right", ControllerType.RIGHT);


    public static final VRActionIdentifier BUTTON_B_LEFT = new VRActionIdentifier("button.b.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_B_RIGHT = new VRActionIdentifier("button.b.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_B_TOUCH_LEFT = new VRActionIdentifier("button.b.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_B_TOUCH_RIGHT = new VRActionIdentifier("button.b.touch.right", ControllerType.RIGHT);


    public static final VRActionIdentifier BUTTON_GRIP_LEFT = new VRActionIdentifier("button.grip.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_GRIP_RIGHT = new VRActionIdentifier("button.grip.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_GRIP_FORCE_LEFT = new VRActionIdentifier("button.grip.force.left", ControllerType.LEFT);;
    public static final VRActionIdentifier BUTTON_GRIP_FORCE_RIGHT = new VRActionIdentifier("button.grip.force.right", ControllerType.RIGHT);



    public static final VRActionIdentifier BUTTON_TRIGGER_LEFT = new VRActionIdentifier("button.trigger.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_RIGHT = new VRActionIdentifier("button.trigger.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_TRIGGER_CLICK_LEFT = new VRActionIdentifier("button.trigger.click.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_CLICK_RIGHT = new VRActionIdentifier("button.trigger.click.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_TRIGGER_TOUCH_LEFT = new VRActionIdentifier("button.trigger.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_TOUCH_RIGHT = new VRActionIdentifier("button.trigger.touch.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_THUMBSTICK_LEFT = new VRActionIdentifier("button.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_RIGHT = new VRActionIdentifier("button.thumbstick.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_TOUCH_LEFT = new VRActionIdentifier("button.thumbstick.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_TOUCH_RIGHT = new VRActionIdentifier("button.thumbstick.touch.right", ControllerType.RIGHT);


    public static final VRActionIdentifier BUTTON_TRACKPAD_TOUCH_LEFT = new VRActionIdentifier("button.trackpad.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRACKPAD_TOUCH_RIGHT = new VRActionIdentifier("button.trackpad.touch.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_TRACKPAD_FORCE_LEFT = new VRActionIdentifier("button.trackpad.force.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRACKPAD_FORCE_RIGHT = new VRActionIdentifier("button.trackpad.force.right", ControllerType.RIGHT);


    public static final VRActionIdentifier VEC2_THUMBSTICK_LEFT = new VRActionIdentifier("vec2.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier VEC2_THUMBSTICK_RIGHT = new VRActionIdentifier("vec2.thumbstick.right", ControllerType.RIGHT);


    public static final VRActionIdentifier VEC2_TRACKPAD_LEFT = new VRActionIdentifier("vec2.trackpad.left", ControllerType.LEFT);
    public static final VRActionIdentifier VEC2_TRACKPAD_RIGHT = new VRActionIdentifier("vec2.trackpad.right", ControllerType.RIGHT);


    public static final List<VRActionIdentifier> ALL_ACTION_IDS;
    public static final List<VRActionIdentifier> BUTTON_IDS;
    public static final List<VRActionIdentifier> VEC2_IDS;

    static {
        ALL_ACTION_IDS = List.of(
                BUTTON_SYSTEM_LEFT, BUTTON_SYSTEM_RIGHT,
                BUTTON_SYSTEM_TOUCH_LEFT, BUTTON_SYSTEM_TOUCH_RIGHT,
                BUTTON_A_LEFT, BUTTON_A_RIGHT,
                BUTTON_A_TOUCH_LEFT, BUTTON_A_TOUCH_RIGHT,
                BUTTON_B_LEFT, BUTTON_B_RIGHT,
                BUTTON_B_TOUCH_LEFT, BUTTON_B_TOUCH_RIGHT,
                BUTTON_GRIP_LEFT, BUTTON_GRIP_RIGHT,
                BUTTON_GRIP_FORCE_LEFT, BUTTON_GRIP_FORCE_RIGHT,
                BUTTON_TRIGGER_LEFT, BUTTON_TRIGGER_RIGHT,
                BUTTON_TRIGGER_CLICK_LEFT, BUTTON_TRIGGER_CLICK_RIGHT,
                BUTTON_TRIGGER_TOUCH_LEFT, BUTTON_TRIGGER_TOUCH_RIGHT,
                BUTTON_THUMBSTICK_LEFT, BUTTON_THUMBSTICK_RIGHT,
                BUTTON_THUMBSTICK_TOUCH_LEFT, BUTTON_THUMBSTICK_TOUCH_RIGHT,
                BUTTON_TRACKPAD_TOUCH_LEFT, BUTTON_TRACKPAD_TOUCH_RIGHT,
                BUTTON_TRACKPAD_FORCE_LEFT, BUTTON_TRACKPAD_FORCE_RIGHT,
                VEC2_THUMBSTICK_LEFT, VEC2_THUMBSTICK_RIGHT,
                VEC2_TRACKPAD_LEFT, VEC2_TRACKPAD_RIGHT
        );
        BUTTON_IDS = List.of(
                BUTTON_SYSTEM_LEFT, BUTTON_SYSTEM_RIGHT,
                BUTTON_SYSTEM_TOUCH_LEFT, BUTTON_SYSTEM_TOUCH_RIGHT,
                BUTTON_A_LEFT, BUTTON_A_RIGHT,
                BUTTON_A_TOUCH_LEFT, BUTTON_A_TOUCH_RIGHT,
                BUTTON_B_LEFT, BUTTON_B_RIGHT,
                BUTTON_B_TOUCH_LEFT, BUTTON_B_TOUCH_RIGHT,
                BUTTON_GRIP_LEFT, BUTTON_GRIP_RIGHT,
                BUTTON_GRIP_FORCE_LEFT, BUTTON_GRIP_FORCE_RIGHT,
                BUTTON_TRIGGER_LEFT, BUTTON_TRIGGER_RIGHT,
                BUTTON_TRIGGER_CLICK_LEFT, BUTTON_TRIGGER_CLICK_RIGHT,
                BUTTON_TRIGGER_TOUCH_LEFT, BUTTON_TRIGGER_TOUCH_RIGHT,
                BUTTON_THUMBSTICK_LEFT, BUTTON_THUMBSTICK_RIGHT,
                BUTTON_THUMBSTICK_TOUCH_LEFT, BUTTON_THUMBSTICK_TOUCH_RIGHT,
                BUTTON_TRACKPAD_TOUCH_LEFT, BUTTON_TRACKPAD_TOUCH_RIGHT,
                BUTTON_TRACKPAD_FORCE_LEFT, BUTTON_TRACKPAD_FORCE_RIGHT
        );
        VEC2_IDS = List.of(
                VEC2_THUMBSTICK_LEFT, VEC2_THUMBSTICK_RIGHT,
                VEC2_TRACKPAD_LEFT, VEC2_TRACKPAD_RIGHT
        );
    }
    // ----------------------------------------

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

    public ValveIndexProfile(XRProvider vrProvider) {
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
    public @NotNull XRInteractionProfileType getType() {
        return PROFILE;
    }
}
