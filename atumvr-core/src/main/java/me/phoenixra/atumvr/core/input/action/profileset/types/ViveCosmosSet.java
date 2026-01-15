package me.phoenixra.atumvr.core.input.action.profileset.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.api.input.action.VRActionDataButton;
import me.phoenixra.atumvr.api.input.action.VRActionDataVec2;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.core.input.action.profileset.XRProfileSet;
import me.phoenixra.atumvr.core.input.action.types.multi.BoolButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.Vec2MultiAction;
import me.phoenixra.atumvr.core.input.action.types.single.BoolButtonAction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.core.input.action.XRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.XRAction.RIGHT_HAND_PATH;

@Getter
public class ViveCosmosSet extends XRProfileSet {
    private static final XRInteractionProfile PROFILE = XRInteractionProfile.VIVE_COSMOS;

    public static final ActionIdentifier BUTTON_MENU = new ActionIdentifier("button.menu", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_SYSTEM = new ActionIdentifier("button.system", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_X = new ActionIdentifier("button.x", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_A = new ActionIdentifier("button.a", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_Y = new ActionIdentifier("button.y", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_B = new ActionIdentifier("button.b", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_SHOULDER_LEFT = new ActionIdentifier("button.shoulder.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_SHOULDER_RIGHT = new ActionIdentifier("button.shoulder.right", ControllerType.RIGHT);


    public static final ActionIdentifier BUTTON_GRIP_LEFT = new ActionIdentifier("button.grip.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_GRIP_RIGHT = new ActionIdentifier("button.grip.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_TRIGGER_LEFT = new ActionIdentifier("button.trigger.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_TRIGGER_RIGHT = new ActionIdentifier("button.trigger.right", ControllerType.RIGHT);
    public static final ActionIdentifier BUTTON_TRIGGER_CLICK_LEFT = new ActionIdentifier("button.trigger.click.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_TRIGGER_CLICK_RIGHT = new ActionIdentifier("button.trigger.click.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_THUMBSTICK_LEFT = new ActionIdentifier("button.thumbstick.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_THUMBSTICK_RIGHT = new ActionIdentifier("button.thumbstick.right", ControllerType.RIGHT);
    public static final ActionIdentifier BUTTON_THUMBSTICK_TOUCH_LEFT = new ActionIdentifier("button.thumbstick.touch.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_THUMBSTICK_TOUCH_RIGHT = new ActionIdentifier("button.thumbstick.touch.right", ControllerType.RIGHT);


    public static final ActionIdentifier VEC2_THUMBSTICK_LEFT = new ActionIdentifier("vec2.thumbstick.left", ControllerType.LEFT);
    public static final ActionIdentifier VEC2_THUMBSTICK_RIGHT = new ActionIdentifier("vec2.thumbstick.right", ControllerType.RIGHT);

    // Single-hand only buttons
    private BoolButtonAction menuButton;
    private BoolButtonAction systemButton;

    // Primary/Secondary Buttons
    private BoolButtonMultiAction primaryButton;  //A & X
    private BoolButtonMultiAction secondaryButton;  // B & Y

    // Shoulder & Grip
    private BoolButtonMultiAction shoulderButton;
    private BoolButtonMultiAction gripButton;

    // Trigger
    private FloatButtonMultiAction triggerValue;
    private BoolButtonMultiAction triggerButton;

    // Thumb stick
    private Vec2MultiAction thumbStick;
    private BoolButtonMultiAction thumbStickButton;
    private BoolButtonMultiAction thumbStickTouch;


    private Map<String, VRActionDataButton> buttonMap;
    private Map<String, VRActionDataVec2> vec2Map;

    public ViveCosmosSet(XRProvider provider) {
        super(provider, "vive_cosmos", "Vive Cosmos Controller", 0);
    }

    @Override
    protected List<XRAction> loadActions(XRProvider provider) {


        // -------- SINGLE-HAND BUTTONS --------
        menuButton = new BoolButtonAction(
                provider, this,
                BUTTON_MENU, "Menu Button"
        ).putDefaultBindings(PROFILE, LEFT_HAND_PATH+"/input/menu/click");

        systemButton = new BoolButtonAction(
                provider, this,
                BUTTON_SYSTEM, "System Button"
        ).putDefaultBindings(PROFILE, RIGHT_HAND_PATH+"/input/system/click");

        // -------- PRIMARY & SECONDARY --------
        primaryButton = new BoolButtonMultiAction(
                provider, this,
                new ActionIdentifier("button.primary"), "Primary Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_X,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/x/click"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_A,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/a/click")
                )
        );

        secondaryButton = new BoolButtonMultiAction(
                provider, this,
                new ActionIdentifier("button.secondary"), "Secondary Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_Y,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/y/click"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_B,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/b/click")
                )
        );

        // -------- SHOULDER & GRIP --------
        shoulderButton = new BoolButtonMultiAction(
                provider, this,
                new ActionIdentifier("button.shoulder"), "Shoulder Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_SHOULDER_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/shoulder/click"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_SHOULDER_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/shoulder/click")
                )
        );

        gripButton = new BoolButtonMultiAction(
                provider, this,
                new ActionIdentifier("button.grip"), "Grip Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_GRIP_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/squeeze/click"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_GRIP_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/squeeze/click")
                )
        );

        // -------- TRIGGER BUTTON --------
        triggerValue = new FloatButtonMultiAction(
                provider, this,
                new ActionIdentifier("button.trigger"), "Trigger Value",
                0.7f,   // press threshold
                0.65f,  // release threshold
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
                provider, this,
                new ActionIdentifier("button.trigger.click"), "Trigger Button",
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

        // -------- THUMB STICK --------
        thumbStick = new Vec2MultiAction(
                provider, this,
                new ActionIdentifier("vec2.thumbstick"), "Thumbstick",
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
                provider, this,
                new ActionIdentifier("button.thumbstick"), "Thumbstick Button",
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

        thumbStickTouch = new BoolButtonMultiAction(
                provider, this,
                new ActionIdentifier("button.thumbstick.touch"), "Thumbstick Touch",
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


        List<VRActionDataButton> listButton = new ArrayList<>();
        listButton.add(menuButton);
        listButton.add(systemButton);
        listButton.addAll(primaryButton.getSubActionsAsButton());
        listButton.addAll(secondaryButton.getSubActionsAsButton());
        listButton.addAll(shoulderButton.getSubActionsAsButton());
        listButton.addAll(gripButton.getSubActionsAsButton());
        listButton.addAll(triggerValue.getSubActionsAsButton());
        listButton.addAll(triggerButton.getSubActionsAsButton());
        listButton.addAll(thumbStickButton.getSubActionsAsButton());
        listButton.addAll(thumbStickButton.getSubActionsAsButton());
        listButton.addAll(thumbStickTouch.getSubActionsAsButton());

        buttonMap = new LinkedHashMap<>();
        for(var entry : listButton){
            buttonMap.put(entry.getId().getValue(), entry);
        }

        List<VRActionDataVec2> listVec2 = new ArrayList<>(thumbStick.getSubActionsAsVec2());

        vec2Map = new LinkedHashMap<>();
        for(var entry : listVec2){
            vec2Map.put(entry.getId().getValue(), entry);
        }

        return List.of(
                menuButton, systemButton,
                primaryButton, secondaryButton,
                shoulderButton, gripButton,
                triggerValue, triggerButton,
                thumbStick, thumbStickButton, thumbStickTouch
        );
    }

    @Override
    public Collection<String> getButtonIds() {
        return Collections.unmodifiableCollection(buttonMap.keySet());
    }

    @Override
    public VRActionDataButton getButton(@NotNull String id) {
        return buttonMap.get(id);
    }


    @Override
    public Collection<String> getVec2Ids() {
        return Collections.unmodifiableCollection(vec2Map.keySet());
    }

    @Override
    public VRActionDataVec2 getVec2(@NotNull String id) {
        return vec2Map.get(id);
    }

    @Override
    public @NotNull XRInteractionProfile getType() {
        return PROFILE;
    }
}
