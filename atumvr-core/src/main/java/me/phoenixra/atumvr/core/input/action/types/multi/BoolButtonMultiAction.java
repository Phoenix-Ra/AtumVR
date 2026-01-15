package me.phoenixra.atumvr.core.input.action.types.multi;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.api.input.action.VRActionDataButton;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateBoolean;
import org.lwjgl.system.MemoryStack;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class BoolButtonMultiAction extends XRMultiAction<Boolean> {

    @Getter
    private final List<SubActionBoolButton> subActionsAsButton;

    public BoolButtonMultiAction(XRProvider provider,
                                 XRActionSet actionSet,
                                 ActionIdentifier id,
                                 String localizedName,
                                 List<SubActionBoolButton> subActions) {
        super(provider, actionSet, id, localizedName, XRInputActionType.BOOLEAN, subActions);
        subActionsAsButton = Collections.unmodifiableList(subActions);
    }

    @Override
    protected void onInit(XRActionSet actionSet, MemoryStack stack) {

    }

    @Override
    public void update(@Nullable Consumer<String> listener) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for(SubAction<Boolean> entry : subActions) {
                var state = XrActionStateBoolean.calloc(stack)
                        .type(actionType.getStateId());
                getInfo.subactionPath(entry.getPathHandle());
                getInfo.action(handle);
                provider.checkXRError(
                        XR10.xrGetActionStateBoolean(
                                provider.getState().getVrSession().getHandle(),
                                getInfo,
                                state
                        ),
                        "xrGetActionStateFloat"
                );
                entry.update(
                        state.currentState(),
                        state.lastChangeTime(),
                        state.changedSinceLastSync(),
                        state.isActive()
                );
                if(listener != null
                        && state.changedSinceLastSync()){
                    listener.accept(entry.getId().getValue());
                }
            }
        }
    }

    @Override
    public SubActionBoolButton getHandSubaction(ControllerType type) {
        return (SubActionBoolButton) super.getHandSubaction(type);
    }


    public static class SubActionBoolButton extends SubAction<Boolean> implements VRActionDataButton {


        public SubActionBoolButton(ActionIdentifier id, String path, Boolean initialState) {
            super(id, path, initialState);

        }

        @Override
        public SubActionBoolButton putDefaultBindings(@NotNull List<XRInteractionProfile> profiles, @Nullable String source) {
            return (SubActionBoolButton) super.putDefaultBindings(profiles, source);
        }

        @Override
        public SubActionBoolButton putDefaultBindings(@NotNull XRInteractionProfile profile, @Nullable String source) {
            return (SubActionBoolButton) super.putDefaultBindings(profile, source);
        }

        @Override
        public boolean isPressed() {
            return currentState;
        }

        @Override
        public boolean isButtonChanged() {
            return changed;
        }

        @Override
        public long getButtonLastChangeTime() {
            return lastChangeTime;
        }
    }
}
