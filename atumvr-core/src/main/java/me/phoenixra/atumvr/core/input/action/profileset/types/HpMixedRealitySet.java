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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.core.input.action.XRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.XRAction.RIGHT_HAND_PATH;

@Getter
public class HpMixedRealitySet extends XRProfileSet {
    private static final XRInteractionProfile PROFILE = XRInteractionProfile.HP_MIXED_REALITY;


    public static final ActionIdentifier BUTTON_MENU_LEFT = new ActionIdentifier("button.menu.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_MENU_RIGHT = new ActionIdentifier("button.menu.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_X = new ActionIdentifier("button.x", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_A = new ActionIdentifier("button.a", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_Y = new ActionIdentifier("button.y", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_B = new ActionIdentifier("button.b", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_GRIP_LEFT = new ActionIdentifier("button.grip.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_GRIP_RIGHT = new ActionIdentifier("button.grip.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_TRIGGER_LEFT = new ActionIdentifier("button.trigger.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_TRIGGER_RIGHT = new ActionIdentifier("button.trigger.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_THUMBSTICK_LEFT = new ActionIdentifier("button.thumbstick.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_THUMBSTICK_RIGHT = new ActionIdentifier("button.thumbstick.right", ControllerType.RIGHT);


    public static final ActionIdentifier VEC2_THUMBSTICK_LEFT = new ActionIdentifier("vec2.thumbstick.left", ControllerType.LEFT);
    public static final ActionIdentifier VEC2_THUMBSTICK_RIGHT = new ActionIdentifier("vec2.thumbstick.right", ControllerType.RIGHT);


    // Menu button (shared)
    private BoolButtonMultiAction menuButton;

    // Primary / Secondary
    private BoolButtonMultiAction primaryButton; // X & A
    private BoolButtonMultiAction secondaryButton; // Y & B

    // Squeeze
    private FloatButtonMultiAction gripValue;

    // Trigger
    private FloatButtonMultiAction triggerValue;

    // Thumb stick
    private Vec2MultiAction thumbStick;
    private BoolButtonMultiAction thumbStickButton;



    private Map<String, VRActionDataButton> buttonMap;
    private Map<String, VRActionDataVec2> vec2Map;

    public HpMixedRealitySet(XRProvider provider) {
        super(provider, "hp_mixed_reality", "HP Mixed Reality Controller", 0);
    }

    @Override
    protected List<XRAction> loadActions(XRProvider provider) {

        // -------- MENU BUTTON --------
        menuButton = new BoolButtonMultiAction(
                provider, this,
                new ActionIdentifier("button.menu"), "Menu Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_MENU_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/menu/click"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_MENU_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/menu/click")
                )
        );

        // -------- PRIMARY & SECONDARY BUTTONS --------
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

        // -------- GRIP --------
        gripValue = new FloatButtonMultiAction(
                provider, this,
                new ActionIdentifier("button.grip"), "Grip Value",
                0.9f,   // press threshold
                0.85f,  // release threshold
                List.of(
                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_GRIP_LEFT,
                                LEFT_HAND_PATH,  0f
                        ).putDefaultBindings(PROFILE, "input/squeeze/value"),
                        new FloatButtonMultiAction.SubActionFloatButton(
                                BUTTON_GRIP_RIGHT,
                                RIGHT_HAND_PATH, 0f
                        ).putDefaultBindings(PROFILE, "input/squeeze/value")
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

        List<VRActionDataButton> listButton = new ArrayList<>();
        listButton.addAll(menuButton.getSubActionsAsButton());
        listButton.addAll(primaryButton.getSubActionsAsButton());
        listButton.addAll(secondaryButton.getSubActionsAsButton());
        listButton.addAll(gripValue.getSubActionsAsButton());
        listButton.addAll(triggerValue.getSubActionsAsButton());
        listButton.addAll(thumbStickButton.getSubActionsAsButton());

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
                menuButton,
                primaryButton, secondaryButton,
                gripValue,
                triggerValue,
                thumbStick, thumbStickButton
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
