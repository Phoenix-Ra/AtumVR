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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.core.input.action.VRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.VRAction.RIGHT_HAND_PATH;

@Getter
public class ViveProfile extends VRInteractionProfile {
    private static final VRInteractionProfileType PROFILE = VRInteractionProfileType.VIVE;

    // ---------- ACTION IDENTIFIERS ----------

    public static final ActionIdentifier BUTTON_SYSTEM_LEFT = new ActionIdentifier("button.system.left", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_SYSTEM_RIGHT = new ActionIdentifier("button.system.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_MENU_LEFT = new ActionIdentifier("button.menu.left", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_MENU_RIGHT = new ActionIdentifier("button.menu.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_GRIP_LEFT = new ActionIdentifier("button.grip.left", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_GRIP_RIGHT = new ActionIdentifier("button.grip.right", ControllerType.RIGHT);

    public static final ActionIdentifier BUTTON_TRIGGER_LEFT = new ActionIdentifier("button.trigger.left", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_TRIGGER_RIGHT = new ActionIdentifier("button.trigger.right", ControllerType.RIGHT);
    public static final ActionIdentifier BUTTON_TRIGGER_CLICK_LEFT = new ActionIdentifier("button.trigger.click.left", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_TRIGGER_CLICK_RIGHT = new ActionIdentifier("button.trigger.click.right", ControllerType.RIGHT);


    public static final ActionIdentifier BUTTON_TRACKPAD_LEFT = new ActionIdentifier("button.trackpad.left", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_TRACKPAD_RIGHT = new ActionIdentifier("button.trackpad.right", ControllerType.RIGHT);
    public static final ActionIdentifier BUTTON_TRACKPAD_TOUCH_LEFT = new ActionIdentifier("button.trackpad.touch.left", ControllerType.LEFT);;
    public static final ActionIdentifier BUTTON_TRACKPAD_TOUCH_RIGHT = new ActionIdentifier("button.trackpad.touch.right", ControllerType.RIGHT);


    public static final ActionIdentifier VEC2_TRACKPAD_LEFT = new ActionIdentifier("vec2.trackpad.left", ControllerType.LEFT);;
    public static final ActionIdentifier VEC2_TRACKPAD_RIGHT = new ActionIdentifier("vec2.trackpad.right", ControllerType.RIGHT);

    // ----------------------------------------

    // System & Menu Buttons
    private BoolButtonMultiAction systemButton;
    private BoolButtonMultiAction menuButton;

    // Grip
    private BoolButtonMultiAction gripButton;

    // Trigger
    private FloatButtonMultiAction triggerValue;
    private BoolButtonMultiAction triggerButton;

    // Trackpad
    private Vec2MultiAction trackpad;
    private BoolButtonMultiAction trackpadTouch;
    private BoolButtonMultiAction trackpadButton;


    private Map<ActionIdentifier, VRActionData> actionMap;
    private Map<ActionIdentifier, VRActionDataButton> buttonMap;
    private Map<ActionIdentifier, VRActionDataVec2> vec2Map;

    public ViveProfile(VRProvider vrProvider) {
        super(vrProvider, "vive", "Vive Controller", 0);
    }

    @Override
    protected List<VRAction> loadActions(@NotNull VRProvider vrProvider) {


        // -------- SYSTEM & MENU BUTTONS --------
        systemButton = new BoolButtonMultiAction(
                vrProvider,
                this,
                new ActionIdentifier("button.system"),
                "System Button",
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

        menuButton = new BoolButtonMultiAction(
                vrProvider,
                this,
                new ActionIdentifier("button.menu"),
                "Menu Button",
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

        // -------- GRIP --------
        gripButton = new BoolButtonMultiAction(
                vrProvider,
                this,
                new ActionIdentifier("button.grip"),
                "Grip Button",
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
                vrProvider,
                this,
                new ActionIdentifier("trigger.button"),
                "Trigger Value",
                0.7f,   // click threshold
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
                vrProvider,
                this,
                new ActionIdentifier("trigger.button.click"),
                "Trigger Button",
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

        // -------- TRACKPAD --------
        trackpad = new Vec2MultiAction(
                vrProvider,
                this,
                new ActionIdentifier("vec2.trackpad"),
                "Trackpad",
                List.of(
                        new Vec2MultiAction.SubActionVec2(
                                VEC2_TRACKPAD_LEFT,
                                LEFT_HAND_PATH,
                                new Vector2f(0, 0)
                        ).putDefaultBindings(PROFILE, "input/trackpad"),
                        new Vec2MultiAction.SubActionVec2(
                                VEC2_TRACKPAD_RIGHT,
                                RIGHT_HAND_PATH,
                                new Vector2f(0, 0)
                        ).putDefaultBindings(PROFILE, "input/trackpad")
                )
        );

        trackpadTouch = new BoolButtonMultiAction(
                vrProvider,
                this,
                new ActionIdentifier("button.trackpad.touch"),
                "Trackpad Touch",
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

        trackpadButton = new BoolButtonMultiAction(
                vrProvider,
                this,
                new ActionIdentifier("button.trackpad"),
                "Trackpad Button",
                List.of(
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_TRACKPAD_LEFT,
                                LEFT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/trackpad/click"),
                        new BoolButtonMultiAction.SubActionBoolButton(
                                BUTTON_TRACKPAD_RIGHT,
                                RIGHT_HAND_PATH,
                                false
                        ).putDefaultBindings(PROFILE, "input/trackpad/click")
                )
        );


        List<VRActionDataButton> listButton = new ArrayList<>();
        listButton.addAll(menuButton.getSubActionsAsButton());
        listButton.addAll(systemButton.getSubActionsAsButton());
        listButton.addAll(gripButton.getSubActionsAsButton());
        listButton.addAll(triggerValue.getSubActionsAsButton());
        listButton.addAll(triggerButton.getSubActionsAsButton());
        listButton.addAll(trackpadButton.getSubActionsAsButton());
        listButton.addAll(trackpadTouch.getSubActionsAsButton());

        buttonMap = new LinkedHashMap<>();
        for(var entry : listButton){
            buttonMap.put(entry.getId(), entry);
        }

        List<VRActionDataVec2> listVec2 = new ArrayList<>(trackpad.getSubActionsAsVec2());

        vec2Map = new LinkedHashMap<>();
        for(var entry : listVec2){
            vec2Map.put(entry.getId(), entry);
        }

        actionMap = new LinkedHashMap<>();
        actionMap.putAll(buttonMap);
        actionMap.putAll(vec2Map);

        return List.of(
                systemButton, menuButton,
                gripButton,
                triggerValue, triggerButton,
                trackpad, trackpadTouch, trackpadButton
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
