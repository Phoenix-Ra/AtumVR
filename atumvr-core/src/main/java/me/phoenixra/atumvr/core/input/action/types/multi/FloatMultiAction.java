package me.phoenixra.atumvr.core.input.action.types.multi;

import lombok.Getter;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.VRMultiAction;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataFloat;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateFloat;
import org.lwjgl.system.MemoryStack;

import java.util.Collections;
import java.util.List;

public class FloatMultiAction extends VRMultiAction<Float> {


    @Getter
    private final List<SubActionFloat> subActionsAsFloat;

    public FloatMultiAction(@NotNull VRProvider vrProvider,
                            @NotNull VRActionSet actionSet,
                            @NotNull ActionIdentifier id,
                            @NotNull String localizedName,
                            @NotNull List<SubActionFloat> subActions) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.FLOAT, subActions);
        subActionsAsFloat = Collections.unmodifiableList(subActions);
    }


    @Override
    protected void onInit(@NotNull VRActionSet actionSet, @NotNull MemoryStack stack) {

    }

    @Override
    public void update() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for(var entry : subActionsAsFloat) {
                var state = XrActionStateFloat.calloc(stack)
                        .type(actionType.getStateId());
                getInfo.subactionPath(entry.getPathHandle());
                getInfo.action(handle);
                vrProvider.checkXRError(
                        XR10.xrGetActionStateFloat(
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

    public static class SubActionFloat extends SubAction<Float> implements VRActionDataFloat {


        public SubActionFloat(@NotNull ActionIdentifier id,
                              @NotNull String path,
                              @NotNull Float initialState) {
            super(id, path, initialState);

        }

        @Override
        public SubActionFloat putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles, @Nullable String source) {
            return (SubActionFloat) super.putDefaultBindings(profiles, source);
        }

        @Override
        public SubActionFloat putDefaultBindings(@NotNull VRInteractionProfileType profile, @Nullable String source) {
            return (SubActionFloat) super.putDefaultBindings(profile, source);
        }

        @Override
        public float getFloat() {
            return currentState;
        }
    }

}
