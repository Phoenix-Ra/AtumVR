package me.phoenixra.atumvr.core.input.action.profileset.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRActionDataButton;
import me.phoenixra.atumvr.api.input.action.VRActionDataVec2;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.OpenXRAction;
import me.phoenixra.atumvr.core.input.action.profileset.OpenXRProfileSet;
import me.phoenixra.atumvr.core.input.action.types.multi.BoolButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.Vec2MultiAction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.core.input.action.OpenXRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.OpenXRAction.RIGHT_HAND_PATH;

@Getter
public class ViveSet extends OpenXRProfileSet {
    private static final XRInteractionProfile PROFILE = XRInteractionProfile.VIVE;

    public static final String BUTTON_SYSTEM_LEFT = "button.system.left";
    public static final String BUTTON_SYSTEM_RIGHT = "button.system.right";

    public static final String BUTTON_MENU_LEFT = "button.menu.left";
    public static final String BUTTON_MENU_RIGHT = "button.menu.right";

    public static final String BUTTON_GRIP_LEFT = "button.grip.left";
    public static final String BUTTON_GRIP_RIGHT = "button.grip.right";

    public static final String BUTTON_TRIGGER_LEFT = "button.trigger.left";
    public static final String BUTTON_TRIGGER_RIGHT = "button.trigger.right";
    public static final String BUTTON_TRIGGER_CLICK_LEFT = "button.trigger.click.left";
    public static final String BUTTON_TRIGGER_CLICK_RIGHT = "button.trigger.click.right";


    public static final String BUTTON_TRACKPAD_LEFT = "button.trackpad.left";
    public static final String BUTTON_TRACKPAD_RIGHT = "button.trackpad.right";
    public static final String BUTTON_TRACKPAD_TOUCH_LEFT = "button.trackpad.touch.left";
    public static final String BUTTON_TRACKPAD_TOUCH_RIGHT = "button.trackpad.touch.right";


    public static final String VEC2_TRACKPAD_LEFT = "vec2.trackpad.left";
    public static final String VEC2_TRACKPAD_RIGHT = "vec2.trackpad.right";


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


    private Map<String, VRActionDataButton> buttonMap;
    private Map<String, VRActionDataVec2> vec2Map;

    public ViveSet(OpenXRProvider provider) {
        super(provider, "vive", "Vive Controller", 0);
    }

    @Override
    protected List<OpenXRAction> loadActions(OpenXRProvider provider) {


        // -------- SYSTEM & MENU BUTTONS --------
        systemButton = new BoolButtonMultiAction(
                provider,
                this,
                "button.system",
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
                provider,
                this,
                "button.menu",
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
                provider,
                this,
                "button.grip",
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
                provider,
                this,
                "trigger.button",
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
                provider,
                this,
                "trigger.button.click",
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
                provider,
                this,
                "vec2.trackpad",
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
                provider,
                this,
                "button.trackpad.touch",
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
                provider,
                this,
                "button.trackpad",
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

        return List.of(
                systemButton, menuButton,
                gripButton,
                triggerValue, triggerButton,
                trackpad, trackpadTouch, trackpadButton
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
