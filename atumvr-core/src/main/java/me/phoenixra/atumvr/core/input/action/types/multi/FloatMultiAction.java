package me.phoenixra.atumvr.core.input.action.types.multi;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRMultiAction;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataFloat;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateFloat;
import org.lwjgl.system.MemoryStack;

import java.util.Collections;
import java.util.List;

public class FloatMultiAction extends XRMultiAction<Float> {


    @Getter
    private final List<SubActionFloat> subActionsAsFloat;

    public FloatMultiAction(@NotNull XRProvider vrProvider,
                            @NotNull XRActionSet actionSet,
                            @NotNull VRActionIdentifier id,
                            @NotNull String localizedName,
                            @NotNull List<SubActionFloat> subActions) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.FLOAT, subActions);
        subActionsAsFloat = Collections.unmodifiableList(subActions);
    }


    @Override
    protected void onInit(@NotNull XRActionSet actionSet, @NotNull MemoryStack stack) {

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


        public SubActionFloat(@NotNull VRActionIdentifier id,
                              @NotNull String path,
                              @NotNull Float initialState) {
            super(id, path, initialState);

        }

        @Override
        public SubActionFloat putDefaultBindings(@NotNull List<XRInteractionProfileType> profiles, @Nullable String source) {
            return (SubActionFloat) super.putDefaultBindings(profiles, source);
        }

        @Override
        public SubActionFloat putDefaultBindings(@NotNull XRInteractionProfileType profile, @Nullable String source) {
            return (SubActionFloat) super.putDefaultBindings(profile, source);
        }

        @Override
        public float getFloat() {
            return currentState;
        }
    }

}
