package me.phoenixra.atumvr.core.input.action.types.multi;

import lombok.Getter;
import me.phoenixra.atumvr.core.enums.ControllerType;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataVec2;
import me.phoenixra.atumvr.core.utils.VRUtils;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.VRMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateVector2f;
import org.lwjgl.system.MemoryStack;

import java.util.Collections;
import java.util.List;

public class Vec2MultiAction extends VRMultiAction<Vector2f> {


    @Getter
    private final List<SubActionVec2> subActionsAsVec2;

    public Vec2MultiAction(@NotNull VRProvider vrProvider,
                           @NotNull VRActionSet actionSet,
                           @NotNull ActionIdentifier id,
                           @NotNull String localizedName,
                           @NotNull List<SubActionVec2> subActions) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.VECTOR2F, subActions);
        subActionsAsVec2 = Collections.unmodifiableList(subActions);
    }

    @Override
    protected void onInit(@NotNull VRActionSet actionSet, @NotNull MemoryStack stack) {

    }

    @Override
    public void update() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for(var entry : subActionsAsVec2) {
                var state = XrActionStateVector2f.calloc(stack)
                        .type(actionType.getStateId());
                getInfo.subactionPath(entry.getPathHandle());
                getInfo.action(handle);
                vrProvider.checkXRError(
                        XR10.xrGetActionStateVector2f(
                                vrProvider.getSession().getHandle(),
                                getInfo,
                                state
                        ),
                        "xrGetActionStateFloat"
                );
                entry.update(
                        VRUtils.normalizeXrVector(state.currentState()),
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
    public SubActionVec2 getHandSubaction(@NotNull ControllerType type) {
        return (SubActionVec2) super.getHandSubaction(type);
    }

    @Getter
    public static class SubActionVec2 extends SubAction<Vector2f> implements VRActionDataVec2 {


        public SubActionVec2(@NotNull ActionIdentifier id,
                             @NotNull String path,
                             @NotNull Vector2f initialState) {
            super(id, path, initialState);
        }

        @Override
        public SubActionVec2 putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles,
                                                @Nullable String source) {
            return (SubActionVec2) super.putDefaultBindings(profiles, source);
        }

        @Override
        public SubActionVec2 putDefaultBindings(@NotNull VRInteractionProfileType profile,
                                                @Nullable String source) {
            return (SubActionVec2) super.putDefaultBindings(profile, source);
        }

        @Override
        public @NotNull Vector2f getVec2Data() {
            return currentState;
        }
    }

}
