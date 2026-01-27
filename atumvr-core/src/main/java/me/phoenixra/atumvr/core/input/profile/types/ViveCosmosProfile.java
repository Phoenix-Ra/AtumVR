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
import me.phoenixra.atumvr.core.input.action.types.single.BoolButtonAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.core.input.action.XRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.XRAction.RIGHT_HAND_PATH;

@Getter
public class ViveCosmosProfile extends XRInteractionProfile {
    private static final XRInteractionProfileType PROFILE = XRInteractionProfileType.VIVE_COSMOS;

    // ---------- ACTION IDENTIFIERS ----------

    public static final VRActionIdentifier BUTTON_MENU = new VRActionIdentifier("button.menu", ControllerType.LEFT);;
    public static final VRActionIdentifier BUTTON_SYSTEM = new VRActionIdentifier("button.system", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_X = new VRActionIdentifier("button.x", ControllerType.LEFT);;
    public static final VRActionIdentifier BUTTON_A = new VRActionIdentifier("button.a", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_Y = new VRActionIdentifier("button.y", ControllerType.LEFT);;
    public static final VRActionIdentifier BUTTON_B = new VRActionIdentifier("button.b", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_SHOULDER_LEFT = new VRActionIdentifier("button.shoulder.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_SHOULDER_RIGHT = new VRActionIdentifier("button.shoulder.right", ControllerType.RIGHT);


    public static final VRActionIdentifier BUTTON_GRIP_LEFT = new VRActionIdentifier("button.grip.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_GRIP_RIGHT = new VRActionIdentifier("button.grip.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_TRIGGER_LEFT = new VRActionIdentifier("button.trigger.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_RIGHT = new VRActionIdentifier("button.trigger.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_TRIGGER_CLICK_LEFT = new VRActionIdentifier("button.trigger.click.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_TRIGGER_CLICK_RIGHT = new VRActionIdentifier("button.trigger.click.right", ControllerType.RIGHT);

    public static final VRActionIdentifier BUTTON_THUMBSTICK_LEFT = new VRActionIdentifier("button.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_RIGHT = new VRActionIdentifier("button.thumbstick.right", ControllerType.RIGHT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_TOUCH_LEFT = new VRActionIdentifier("button.thumbstick.touch.left", ControllerType.LEFT);
    public static final VRActionIdentifier BUTTON_THUMBSTICK_TOUCH_RIGHT = new VRActionIdentifier("button.thumbstick.touch.right", ControllerType.RIGHT);


    public static final VRActionIdentifier VEC2_THUMBSTICK_LEFT = new VRActionIdentifier("vec2.thumbstick.left", ControllerType.LEFT);
    public static final VRActionIdentifier VEC2_THUMBSTICK_RIGHT = new VRActionIdentifier("vec2.thumbstick.right", ControllerType.RIGHT);

    // ----------------------------------------

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


    private Map<VRActionIdentifier, VRActionData> actionMap;
    private Map<VRActionIdentifier, VRActionDataButton> buttonMap;
    private Map<VRActionIdentifier, VRActionDataVec2> vec2Map;

    public ViveCosmosProfile(XRProvider vrProvider) {
        super(vrProvider, "vive_cosmos", "Vive Cosmos Controller", 0);
    }

    @Override
    protected List<XRAction> loadActions(@NotNull XRProvider vrProvider) {


        // -------- SINGLE-HAND BUTTONS --------
        menuButton = new BoolButtonAction(
                vrProvider, this,
                BUTTON_MENU, "Menu Button"
        ).putDefaultBindings(PROFILE, LEFT_HAND_PATH+"/input/menu/click");

        systemButton = new BoolButtonAction(
                vrProvider, this,
                BUTTON_SYSTEM, "System Button"
        ).putDefaultBindings(PROFILE, RIGHT_HAND_PATH+"/input/system/click");

        // -------- PRIMARY & SECONDARY --------
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

        // -------- SHOULDER & GRIP --------
        shoulderButton = new BoolButtonMultiAction(
                vrProvider, this,
                new VRActionIdentifier("button.shoulder"), "Shoulder Button",
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
                vrProvider, this,
                new VRActionIdentifier("button.grip"), "Grip Button",
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

        triggerButton = new BoolButtonMultiAction(
                vrProvider, this,
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

        thumbStickTouch = new BoolButtonMultiAction(
                vrProvider, this,
                new VRActionIdentifier("button.thumbstick.touch"), "Thumbstick Touch",
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
                menuButton, systemButton,
                primaryButton, secondaryButton,
                shoulderButton, gripButton,
                triggerValue, triggerButton,
                thumbStick, thumbStickButton, thumbStickTouch
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
