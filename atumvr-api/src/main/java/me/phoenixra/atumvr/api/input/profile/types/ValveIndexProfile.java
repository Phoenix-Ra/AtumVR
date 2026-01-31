package me.phoenixra.atumvr.api.input.profile.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import java.util.*;


@Getter
public class ValveIndexProfile  {


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



}
