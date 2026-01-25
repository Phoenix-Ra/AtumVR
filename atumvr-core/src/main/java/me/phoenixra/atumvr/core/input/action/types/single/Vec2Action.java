package me.phoenixra.atumvr.core.input.action.types.single;

import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataVec2;
import me.phoenixra.atumvr.core.utils.VRUtils;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.VRSingleAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateVector2f;
import org.lwjgl.system.MemoryStack;

import java.util.function.Consumer;

public class Vec2Action extends VRSingleAction<Vector2f> implements VRActionDataVec2 {


    public Vec2Action(@NotNull VRProvider vrProvider,
                      @NotNull VRActionSet actionSet,
                      @NotNull ActionIdentifier id,
                      @NotNull String localizedName) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.VECTOR2F);
        currentState = new Vector2f();
    }


    @Override
    protected void onInit(@NotNull VRActionSet actionSet, @NotNull MemoryStack stack) {

    }

    @Override
    public void update() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var state = XrActionStateVector2f.calloc(stack)
                    .type(actionType.getStateId());
            getInfo.action(handle);
            vrProvider.checkXRError(
                    XR10.xrGetActionStateVector2f(
                            vrProvider.getSession().getHandle(),
                            getInfo,
                            state
                    ),
                    "xrGetActionStateFloat"
            );
            this.currentState = VRUtils.normalizeXrVector(state.currentState());
            this.changed = state.changedSinceLastSync();
            this.lastChangeTime = state.lastChangeTime();
            this.active = state.isActive();

            if(changed){
                vrProvider.getInputHandler().onActionChanged(
                        this
                );
            }
        }
    }


    @Override
    public @NotNull Vector2f getVec2Data() {
        return currentState;
    }
}
