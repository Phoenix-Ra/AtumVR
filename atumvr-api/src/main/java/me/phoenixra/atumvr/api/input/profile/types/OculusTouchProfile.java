package me.phoenixra.atumvr.api.input.profile.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;

import java.util.*;

@Getter
public class OculusTouchProfile {

    public static final VRActionIdentifier BUTTON_MENU = new VRActionIdentifier("button.menu", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_SYSTEM = new VRActionIdentifier("button.system", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_X = new VRActionIdentifier("button.x", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_A = new VRActionIdentifier("button.a", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_X_TOUCH = new VRActionIdentifier("button.x.touch", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_A_TOUCH = new VRActionIdentifier("button.a.touch", ControllerType.RIGHT);


    public static final VRActionIdentifier BUTTON_Y = new VRActionIdentifier("button.y", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_B = new VRActionIdentifier("button.b", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_Y_TOUCH = new VRActionIdentifier("button.y.touch", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_B_TOUCH = new VRActionIdentifier("button.b.touch", ControllerType.RIGHT);


    public static final VRActionIdentifier BUTTON_GRIP_LEFT = new VRActionIdentifier("button.grip.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_GRIP_RIGHT = new VRActionIdentifier("button.grip.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_TRIGGER_LEFT = new VRActionIdentifier("button.trigger.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_RIGHT = new VRActionIdentifier("button.trigger.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_TRIGGER_TOUCH_LEFT = new VRActionIdentifier("button.trigger.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_TOUCH_RIGHT = new VRActionIdentifier("button.trigger.touch.right", ControllerType.RIGHT);


    public static final VRActionIdentifier BUTTON_THUMBSTICK_LEFT = new VRActionIdentifier("button.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_RIGHT = new VRActionIdentifier("button.thumbstick.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_TOUCH_LEFT = new VRActionIdentifier("button.thumbstick.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_TOUCH_RIGHT = new VRActionIdentifier("button.thumbstick.touch.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_THUMBREST_TOUCH_LEFT = new VRActionIdentifier("button.thumbrest.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBREST_TOUCH_RIGHT = new VRActionIdentifier("button.thumbrest.touch.right", ControllerType.RIGHT);


    public static final VRActionIdentifier VEC2_THUMBSTICK_LEFT = new VRActionIdentifier("vec2.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier VEC2_THUMBSTICK_RIGHT = new VRActionIdentifier("vec2.thumbstick.right", ControllerType.RIGHT);


    public static final List<VRActionIdentifier> ALL_ACTION_IDS;
    public static final List<VRActionIdentifier> BUTTON_IDS;
    public static final List<VRActionIdentifier> VEC2_IDS;

    static {
        ALL_ACTION_IDS = List.of(
                BUTTON_MENU, BUTTON_SYSTEM,
                BUTTON_X, BUTTON_A, BUTTON_X_TOUCH, BUTTON_A_TOUCH,
                BUTTON_Y, BUTTON_B, BUTTON_Y_TOUCH, BUTTON_B_TOUCH,
                BUTTON_GRIP_LEFT, BUTTON_GRIP_RIGHT,
                BUTTON_TRIGGER_LEFT, BUTTON_TRIGGER_RIGHT,
                BUTTON_TRIGGER_TOUCH_LEFT, BUTTON_TRIGGER_TOUCH_RIGHT,
                BUTTON_THUMBSTICK_LEFT, BUTTON_THUMBSTICK_RIGHT,
                BUTTON_THUMBSTICK_TOUCH_LEFT, BUTTON_THUMBSTICK_TOUCH_RIGHT,
                BUTTON_THUMBREST_TOUCH_LEFT, BUTTON_THUMBREST_TOUCH_RIGHT,
                VEC2_THUMBSTICK_LEFT, VEC2_THUMBSTICK_RIGHT
        );
        BUTTON_IDS = List.of(
                BUTTON_MENU, BUTTON_SYSTEM,
                BUTTON_X, BUTTON_A, BUTTON_X_TOUCH, BUTTON_A_TOUCH,
                BUTTON_Y, BUTTON_B, BUTTON_Y_TOUCH, BUTTON_B_TOUCH,
                BUTTON_GRIP_LEFT, BUTTON_GRIP_RIGHT,
                BUTTON_TRIGGER_LEFT, BUTTON_TRIGGER_RIGHT,
                BUTTON_TRIGGER_TOUCH_LEFT, BUTTON_TRIGGER_TOUCH_RIGHT,
                BUTTON_THUMBSTICK_LEFT, BUTTON_THUMBSTICK_RIGHT,
                BUTTON_THUMBSTICK_TOUCH_LEFT, BUTTON_THUMBSTICK_TOUCH_RIGHT,
                BUTTON_THUMBREST_TOUCH_LEFT, BUTTON_THUMBREST_TOUCH_RIGHT
        );
        VEC2_IDS = List.of(
                VEC2_THUMBSTICK_LEFT, VEC2_THUMBSTICK_RIGHT
        );
    }



}
