package me.phoenixra.atumvr.core.input.action.types.multi;

import lombok.Getter;
import me.phoenixra.atumvr.core.enums.ControllerType;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataButton;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.VRMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateBoolean;
import org.lwjgl.system.MemoryStack;

import java.util.Collections;
import java.util.List;

public class BoolButtonMultiAction extends VRMultiAction<Boolean> {

    @Getter
    private final List<SubActionBoolButton> subActionsAsButton;

    public BoolButtonMultiAction(@NotNull VRProvider vrProvider,
                                 @NotNull VRActionSet actionSet,
                                 @NotNull ActionIdentifier id,
                                 @NotNull String localizedName,
                                 @NotNull List<SubActionBoolButton> subActions) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.BOOLEAN, subActions);
        subActionsAsButton = Collections.unmodifiableList(subActions);
    }

    @Override
    protected void onInit(@NotNull VRActionSet actionSet, @NotNull MemoryStack stack) {

    }

    @Override
    public void update() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for(var entry : subActionsAsButton) {
                var state = XrActionStateBoolean.calloc(stack)
                        .type(actionType.getStateId());
                getInfo.subactionPath(entry.getPathHandle());
                getInfo.action(handle);
                vrProvider.checkXRError(
                        XR10.xrGetActionStateBoolean(
                                vrProvider.getSession().getHandle(),
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
                if(state.changedSinceLastSync()){
                    vrProvider.getInputHandler().onActionChanged(
                            entry
                    );
                }
            }
        }
    }

    @Override
    public SubActionBoolButton getHandSubaction(@NotNull ControllerType type) {
        return (SubActionBoolButton) super.getHandSubaction(type);
    }


    public static class SubActionBoolButton extends SubAction<Boolean> implements VRActionDataButton {


        public SubActionBoolButton(@NotNull ActionIdentifier id,
                                   @NotNull String path,
                                   @NotNull Boolean initialState) {
            super(id, path, initialState);

        }

        @Override
        public SubActionBoolButton putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles, @Nullable String source) {
            return (SubActionBoolButton) super.putDefaultBindings(profiles, source);
        }

        @Override
        public SubActionBoolButton putDefaultBindings(@NotNull VRInteractionProfileType profile, @Nullable String source) {
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
