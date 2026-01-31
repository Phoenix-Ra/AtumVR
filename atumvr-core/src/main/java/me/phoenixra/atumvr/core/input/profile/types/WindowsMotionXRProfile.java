package me.phoenixra.atumvr.core.input.profile.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.action.data.VRActionData;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataButton;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataVec2;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.types.multi.BoolButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatButtonMultiAction;
import me.phoenixra.atumvr.core.input.action.types.multi.Vec2MultiAction;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.*;

import static me.phoenixra.atumvr.api.input.profile.types.WindowsMotionProfile.*;
import static me.phoenixra.atumvr.core.input.action.XRAction.LEFT_HAND_PATH;
import static me.phoenixra.atumvr.core.input.action.XRAction.RIGHT_HAND_PATH;

@Getter
public class WindowsMotionXRProfile extends XRInteractionProfile {
    private static final VRInteractionProfileType PROFILE = VRInteractionProfileType.WINDOWS_MOTION;


    // Menu button
    private BoolButtonMultiAction menuButton;

    // Grip
    private BoolButtonMultiAction gripButton;

    // Trigger
    private FloatButtonMultiAction triggerValue;

    // Thumb stick
    private Vec2MultiAction thumbStick;
    private BoolButtonMultiAction thumbStickButton;

    // Trackpad
    private Vec2MultiAction trackpad;
    private BoolButtonMultiAction trackpadTouch;
    private BoolButtonMultiAction trackpadButton;


    private Map<VRActionIdentifier, VRActionData> actionMap;
    private Map<VRActionIdentifier, VRActionDataButton> buttonMap;
    private Map<VRActionIdentifier, VRActionDataVec2> vec2Map;


    public WindowsMotionXRProfile(XRProvider vrProvider) {
        super(vrProvider, "windows_motion", "Windows Motion Controller", 0);
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

        // -------- GRIP --------
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
                0.7f,
                0.65f,
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
                                new Vector2f(0, 0)
                        ).putDefaultBindings(PROFILE, "input/thumbstick"),
                        new Vec2MultiAction.SubActionVec2(
                                VEC2_THUMBSTICK_RIGHT,
                                RIGHT_HAND_PATH,
                                new Vector2f(0, 0)
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

        // -------- TRACKPAD --------
        trackpad = new Vec2MultiAction(
                vrProvider, this,
                new VRActionIdentifier("vec2.trackpad"), "Trackpad",
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
                vrProvider, this,
                new VRActionIdentifier("button.trackpad.touch"), "Trackpad Touch",
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
                vrProvider, this,
                new VRActionIdentifier("button.trackpad"), "Trackpad Button",
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
        listButton.addAll(gripButton.getSubActionsAsButton());
        listButton.addAll(triggerValue.getSubActionsAsButton());
        listButton.addAll(thumbStickButton.getSubActionsAsButton());
        listButton.addAll(trackpadButton.getSubActionsAsButton());
        listButton.addAll(trackpadTouch.getSubActionsAsButton());

        buttonMap = new LinkedHashMap<>();
        for(var entry : listButton){
            buttonMap.put(entry.getId(), entry);
        }

        List<VRActionDataVec2> listVec2 = new ArrayList<>();
        listVec2.addAll(trackpad.getSubActionsAsVec2());
        listVec2.addAll(thumbStick.getSubActionsAsVec2());

        vec2Map = new LinkedHashMap<>();
        for(var entry : listVec2){
            vec2Map.put(entry.getId(), entry);
        }

        actionMap = new LinkedHashMap<>();
        actionMap.putAll(buttonMap);
        actionMap.putAll(vec2Map);

        return List.of(
                menuButton,
                gripButton,
                triggerValue,
                thumbStick, thumbStickButton,
                trackpad, trackpadTouch, trackpadButton
        );
    }

    @Override
    public @NotNull VRActionDataButton getTriggerButton(@NotNull ControllerType controllerType) {
        return triggerValue.getHandSubaction(controllerType);
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
    public @NotNull VRInteractionProfileType getType() {
        return PROFILE;
    }
}
