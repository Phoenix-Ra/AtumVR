package me.phoenixra.atumvr.api.input.profile.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;

import java.util.*;


@Getter
public class WindowsMotionProfile {


    public static final VRActionIdentifier BUTTON_MENU_LEFT = new VRActionIdentifier("button.menu.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_MENU_RIGHT = new VRActionIdentifier("button.menu.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_GRIP_LEFT = new VRActionIdentifier("button.grip.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_GRIP_RIGHT = new VRActionIdentifier("button.grip.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_TRIGGER_LEFT = new VRActionIdentifier("button.trigger.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_RIGHT = new VRActionIdentifier("button.trigger.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_THUMBSTICK_LEFT = new VRActionIdentifier("button.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_RIGHT = new VRActionIdentifier("button.thumbstick.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_TRACKPAD_LEFT = new VRActionIdentifier("button.trackpad.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRACKPAD_RIGHT = new VRActionIdentifier("button.trackpad.touch.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_TRACKPAD_TOUCH_LEFT = new VRActionIdentifier("button.trackpad.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRACKPAD_TOUCH_RIGHT = new VRActionIdentifier("button.trackpad.touch.right", ControllerType.RIGHT);


    public static final VRActionIdentifier VEC2_THUMBSTICK_LEFT = new VRActionIdentifier("vec2.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier VEC2_THUMBSTICK_RIGHT = new VRActionIdentifier("vec2.thumbstick.right", ControllerType.RIGHT);

    public static final VRActionIdentifier VEC2_TRACKPAD_LEFT = new VRActionIdentifier("vec2.trackpad.left", ControllerType.LEFT);
    public static final VRActionIdentifier VEC2_TRACKPAD_RIGHT = new VRActionIdentifier("vec2.trackpad.right", ControllerType.RIGHT);


    public static final List<VRActionIdentifier> ALL_ACTION_IDS;
    public static final List<VRActionIdentifier> BUTTON_IDS;
    public static final List<VRActionIdentifier> VEC2_IDS;

    static {
        ALL_ACTION_IDS = List.of(
                BUTTON_MENU_LEFT, BUTTON_MENU_RIGHT,
                BUTTON_GRIP_LEFT, BUTTON_GRIP_RIGHT,
                BUTTON_TRIGGER_LEFT, BUTTON_TRIGGER_RIGHT,
                BUTTON_THUMBSTICK_LEFT, BUTTON_THUMBSTICK_RIGHT,
                BUTTON_TRACKPAD_LEFT, BUTTON_TRACKPAD_RIGHT,
                BUTTON_TRACKPAD_TOUCH_LEFT, BUTTON_TRACKPAD_TOUCH_RIGHT,
                VEC2_THUMBSTICK_LEFT, VEC2_THUMBSTICK_RIGHT,
                VEC2_TRACKPAD_LEFT, VEC2_TRACKPAD_RIGHT
        );
        BUTTON_IDS = List.of(
                BUTTON_MENU_LEFT, BUTTON_MENU_RIGHT,
                BUTTON_GRIP_LEFT, BUTTON_GRIP_RIGHT,
                BUTTON_TRIGGER_LEFT, BUTTON_TRIGGER_RIGHT,
                BUTTON_THUMBSTICK_LEFT, BUTTON_THUMBSTICK_RIGHT,
                BUTTON_TRACKPAD_LEFT, BUTTON_TRACKPAD_RIGHT,
                BUTTON_TRACKPAD_TOUCH_LEFT, BUTTON_TRACKPAD_TOUCH_RIGHT
        );
        VEC2_IDS = List.of(
                VEC2_THUMBSTICK_LEFT, VEC2_THUMBSTICK_RIGHT,
                VEC2_TRACKPAD_LEFT, VEC2_TRACKPAD_RIGHT
        );
    }


}
