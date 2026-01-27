package me.phoenixra.atumvr.core.input.action.types.single;

import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataVec2;
import me.phoenixra.atumvr.core.utils.XRUtils;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRSingleAction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateVector2f;
import org.lwjgl.system.MemoryStack;

public class Vec2Action extends XRSingleAction<Vector2f> implements VRActionDataVec2 {


    public Vec2Action(@NotNull XRProvider vrProvider,
                      @NotNull XRActionSet actionSet,
                      @NotNull VRActionIdentifier id,
                      @NotNull String localizedName) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.VECTOR2F);
        currentState = new Vector2f();
    }


    @Override
    protected void onInit(@NotNull XRActionSet actionSet, @NotNull MemoryStack stack) {

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
            this.currentState = XRUtils.normalizeXrVector(state.currentState());
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
