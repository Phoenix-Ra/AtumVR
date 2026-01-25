package me.phoenixra.atumvr.core.input.profile.types;

import lombok.Getter;
import me.phoenixra.atumvr.core.enums.ControllerType;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.input.action.data.VRActionData;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataButton;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataVec2;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.VRAction;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.types.multi.BoolButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.Vec2MultiAction;
import me.phoenixra.atumvr.core.input.action.types.single.BoolButtonAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.core.input.action.VRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.VRAction.RIGHT_HAND_PATH;

@Getter
public class OculusTouchProfile extends VRInteractionProfile {
    private static final VRInteractionProfileType PROFILE = VRInteractionProfileType.OCULUS_TOUCH;

    // ---------- ACTION IDENTIFIERS ----------

    public static final ActionIdentifier BUTTON_MENU = new ActionIdentifier("button.menu", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_SYSTEM = new ActionIdentifier("button.system", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_X = new ActionIdentifier("button.x", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_A = new ActionIdentifier("button.a", ControllerType.RIGHT);
    public static final ActionIdentifier BUTTON_X_TOUCH = new ActionIdentifier("button.x.touch", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_A_TOUCH = new ActionIdentifier("button.a.touch", ControllerType.RIGHT);


    public static final ActionIdentifier BUTTON_Y = new ActionIdentifier("button.y", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_B = new ActionIdentifier("button.b", ControllerType.RIGHT);
    public static final ActionIdentifier BUTTON_Y_TOUCH = new ActionIdentifier("button.y.touch", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_B_TOUCH = new ActionIdentifier("button.b.touch", ControllerType.RIGHT);


    public static final ActionIdentifier BUTTON_GRIP_LEFT = new ActionIdentifier("button.grip.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_GRIP_RIGHT = new ActionIdentifier("button.grip.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_TRIGGER_LEFT = new ActionIdentifier("button.trigger.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_TRIGGER_RIGHT = new ActionIdentifier("button.trigger.right", ControllerType.RIGHT);
    public static final ActionIdentifier BUTTON_TRIGGER_TOUCH_LEFT = new ActionIdentifier("button.trigger.touch.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_TRIGGER_TOUCH_RIGHT = new ActionIdentifier("button.trigger.touch.right", ControllerType.RIGHT);


    public static final ActionIdentifier BUTTON_THUMBSTICK_LEFT = new ActionIdentifier("button.thumbstick.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_THUMBSTICK_RIGHT = new ActionIdentifier("button.thumbstick.right", ControllerType.RIGHT);
    public static final ActionIdentifier BUTTON_THUMBSTICK_TOUCH_LEFT = new ActionIdentifier("button.thumbstick.touch.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_THUMBSTICK_TOUCH_RIGHT = new ActionIdentifier("button.thumbstick.touch.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_THUMBREST_TOUCH_LEFT = new ActionIdentifier("button.thumbrest.touch.left", ControllerType.LEFT);
    public static final ActionIdentifier BUTTON_THUMBREST_TOUCH_RIGHT = new ActionIdentifier("button.thumbrest.touch.right", ControllerType.RIGHT);


    public static final ActionIdentifier VEC2_THUMBSTICK_LEFT = new ActionIdentifier("vec2.thumbstick.left", ControllerType.LEFT);
    public static final ActionIdentifier VEC2_THUMBSTICK_RIGHT = new ActionIdentifier("vec2.thumbstick.right", ControllerType.RIGHT);

    // ----------------------------------------

    // Single-hand only buttons
    private BoolButtonAction menuButton;
    private BoolButtonAction systemButton;

    // Primary (X & A) / Secondary buttons (Y & B)
    private BoolButtonMultiAction primaryButton;
    private BoolButtonMultiAction primaryButtonTouch;
    private BoolButtonMultiAction secondaryButton;
    private BoolButtonMultiAction secondaryButtonTouch;

    // Squeeze & Trigger
    private FloatButtonMultiAction gripValue;
    private FloatButtonMultiAction triggerValue;
    private BoolButtonMultiAction triggerTouch;

    // Thumb stick
    private Vec2MultiAction thumbStick;
    private BoolButtonMultiAction thumbStickButton;
    private BoolButtonMultiAction thumbStickTouch;

    // Thumb rest touch
    private BoolButtonMultiAction thumbRestTouch;


    private Map<ActionIdentifier, VRActionData> actionMap;
    private Map<ActionIdentifier, VRActionDataButton> buttonMap;
    private Map<ActionIdentifier, VRActionDataVec2> vec2Map;

    public OculusTouchProfile(VRProvider vrProvider) {
        super(vrProvider, "oculus_touch", "Oculus Touch Controller", 0);
    }

    @Override
    protected List<VRAction> loadActions(@NotNull VRProvider vrProvider) {


        // -------- MENU & SYSTEM BUTTONS --------
        menuButton = new BoolButtonAction(
                vrProvider, this,
                BUTTON_MENU, "Menu Button"
        ).putDefaultBindings(PROFILE, LEFT_HAND_PATH+"/input/menu/click");

        systemButton = new BoolButtonAction(
                vrProvider, this,
                BUTTON_SYSTEM, "System Button"
        ).putDefaultBindings(PROFILE, RIGHT_HAND_PATH+"/input/system/click");

        // -------- PRIMARY BUTTONS (X/A) --------
        primaryButton = new BoolButtonMultiAction(
                vrProvider, this,
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
        primaryButtonTouch = new BoolButtonMultiAction(
                vrProvider, this,
                new ActionIdentifier("button.primary.touch"), "Primary Button Touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_X_TOUCH,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/x/touch"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_A_TOUCH,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/a/touch")
                )
        );
        // -------- SECONDARY (Y/B) --------
        secondaryButton = new BoolButtonMultiAction(
                vrProvider, this,
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
        secondaryButtonTouch = new BoolButtonMultiAction(
                vrProvider, this,
                new ActionIdentifier("button.secondary.touch"), "Secondary Button Touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_Y_TOUCH,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/y/touch"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_B_TOUCH,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/b/touch")
                )
        );

        // -------- GRIP --------
        gripValue = new FloatButtonMultiAction(
                vrProvider, this,
                new ActionIdentifier("button.grip"), "Grip Value",
                0.9f,   // press
                0.85f,  // release
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

        // -------- TRIGGER BUTTON --------
        triggerValue = new FloatButtonMultiAction(
                vrProvider, this,
                new ActionIdentifier("button.trigger"), "Trigger Value",
                0.7f,   // press
                0.65f,  // release
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
        triggerTouch = new BoolButtonMultiAction(
                vrProvider, this,
                new ActionIdentifier("button.trigger.touch"), "Trigger Touch",
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
                vrProvider, this,
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
                vrProvider, this,
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
                vrProvider, this,
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

        // -------- THUMB REST --------
        thumbRestTouch = new BoolButtonMultiAction(
                vrProvider, this,
                new ActionIdentifier("button.thumbrest.touch"), "Thumbrest Touch",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_THUMBREST_TOUCH_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/thumbrest/touch"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_THUMBREST_TOUCH_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/thumbrest/touch")
                )
        );

        List<VRActionDataButton> listButton = new ArrayList<>();
        listButton.add(menuButton);
        listButton.add(systemButton);
        listButton.addAll(primaryButton.getSubActionsAsButton());
        listButton.addAll(primaryButtonTouch.getSubActionsAsButton());
        listButton.addAll(secondaryButton.getSubActionsAsButton());
        listButton.addAll(secondaryButtonTouch.getSubActionsAsButton());
        listButton.addAll(gripValue.getSubActionsAsButton());
        listButton.addAll(triggerValue.getSubActionsAsButton());
        listButton.addAll(triggerTouch.getSubActionsAsButton());
        listButton.addAll(thumbStickButton.getSubActionsAsButton());
        listButton.addAll(thumbStickTouch.getSubActionsAsButton());
        listButton.addAll(thumbRestTouch.getSubActionsAsButton());

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
                primaryButton, primaryButtonTouch,
                secondaryButton, secondaryButtonTouch,
                gripValue,
                triggerValue, triggerTouch,
                thumbStick, thumbStickButton, thumbStickTouch,
                thumbRestTouch
        );
    }


    @Override
    public Collection<ActionIdentifier> getActionIds() {
        return Collections.unmodifiableCollection(actionMap.keySet());
    }

    @Override
    public @Nullable VRActionData getAction(@NotNull ActionIdentifier id) {
        return actionMap.get(id);
    }


    @Override
    public Collection<ActionIdentifier> getButtonIds() {
        return Collections.unmodifiableCollection(buttonMap.keySet());
    }

    @Override
    public VRActionDataButton getButton(@NotNull ActionIdentifier id) {
        return buttonMap.get(id);
    }


    @Override
    public Collection<ActionIdentifier> getVec2Ids() {
        return Collections.unmodifiableCollection(vec2Map.keySet());
    }

    @Override
    public VRActionDataVec2 getVec2(@NotNull ActionIdentifier id) {
        return vec2Map.get(id);
    }


    @Override
    public @NotNull VRInteractionProfileType getType() {
        return PROFILE;
    }
}
