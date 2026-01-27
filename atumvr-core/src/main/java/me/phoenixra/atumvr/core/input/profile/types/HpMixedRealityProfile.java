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
public class HpMixedRealityProfile extends XRInteractionProfile {
    private static final XRInteractionProfileType PROFILE = XRInteractionProfileType.HP_MIXED_REALITY;


    // ---------- ACTION IDENTIFIERS ----------

    public static final VRActionIdentifier BUTTON_MENU_LEFT = new VRActionIdentifier("button.menu.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_MENU_RIGHT = new VRActionIdentifier("button.menu.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_X = new VRActionIdentifier("button.x", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_A = new VRActionIdentifier("button.a", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_Y = new VRActionIdentifier("button.y", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_B = new VRActionIdentifier("button.b", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_GRIP_LEFT = new VRActionIdentifier("button.grip.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_GRIP_RIGHT = new VRActionIdentifier("button.grip.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_TRIGGER_LEFT = new VRActionIdentifier("button.trigger.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_RIGHT = new VRActionIdentifier("button.trigger.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_THUMBSTICK_LEFT = new VRActionIdentifier("button.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_RIGHT = new VRActionIdentifier("button.thumbstick.right", ControllerType.RIGHT);


    public static final VRActionIdentifier VEC2_THUMBSTICK_LEFT = new VRActionIdentifier("vec2.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier VEC2_THUMBSTICK_RIGHT = new VRActionIdentifier("vec2.thumbstick.right", ControllerType.RIGHT);

    // ----------------------------------------

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



    private Map<VRActionIdentifier, VRActionData> actionMap;
    private Map<VRActionIdentifier, VRActionDataButton> buttonMap;
    private Map<VRActionIdentifier, VRActionDataVec2> vec2Map;

    public HpMixedRealityProfile(XRProvider vrProvider) {
        super(vrProvider, "hp_mixed_reality", "HP Mixed Reality Controller", 0);
    }

    @Override
    protected List<XRAction> loadActions(@NotNull XRProvider vrProvider) {

        // -------- MENU BUTTON --------
        menuButton = new BoolButtonMultiAction(
                vrProvider, this,
                new VRActionIdentifier("button.menu"), "Menu Button",
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
                vrProvider, this,
                new VRActionIdentifier("button.primary"), "Primary Button",
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
                vrProvider, this,
                new VRActionIdentifier("button.secondary"), "Secondary Button",
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
                vrProvider, this,
                new VRActionIdentifier("button.grip"), "Grip Value",
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
                vrProvider, this,
                new VRActionIdentifier("button.trigger"), "Trigger Value",
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
                vrProvider, this,
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
                vrProvider, this,
                new VRActionIdentifier("button.thumbstick"), "Thumbstick Button",
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
            buttonMap.put(entry.getId(), entry);
        }

        List<VRActionDataVec2> listVec2 = new ArrayList<>(thumbStick.getSubActionsAsVec2());

        vec2Map = new LinkedHashMap<>();
        for(var entry : listVec2){
            vec2Map.put(entry.getId(), entry);
        }

        actionMap = new LinkedHashMap<>();
        actionMap.putAll(buttonMap);
        actionMap.putAll(vec2Map);

        return List.of(
                menuButton,
                primaryButton, secondaryButton,
                gripValue,
                triggerValue,
                thumbStick, thumbStickButton
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
