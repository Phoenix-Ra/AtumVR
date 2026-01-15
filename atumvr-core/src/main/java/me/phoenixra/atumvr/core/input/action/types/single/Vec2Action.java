package me.phoenixra.atumvr.core.input.action.types.single;

import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.XRHelper;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRSingleAction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateVector2f;
import org.lwjgl.system.MemoryStack;

import java.util.function.Consumer;

public class Vec2Action extends XRSingleAction<Vector2fc> {


    public Vec2Action(XRProvider provider,
                      XRActionSet actionSet,
                      ActionIdentifier id,
                      String localizedName) {
        super(provider, actionSet, id, localizedName, XRInputActionType.VECTOR2F);
        currentState = new Vector2f();
    }


    @Override
    protected void onInit(XRActionSet actionSet, MemoryStack stack) {

    }

    @Override
    public void update(@Nullable Consumer<String> listener) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var state = XrActionStateVector2f.calloc(stack)
                    .type(actionType.getStateId());
            getInfo.action(handle);
            provider.checkXRError(
                    XR10.xrGetActionStateVector2f(
                            provider.getState().getVrSession().getHandle(),
                            getInfo,
                            state
                    ),
                    "xrGetActionStateFloat"
            );
            this.currentState = XRHelper.normalizeXrVector(state.currentState());
            this.changed = state.changedSinceLastSync();
            this.lastChangeTime = state.lastChangeTime();
            this.active = state.isActive();

            if(listener != null && changed){
                listener.accept(id.getValue());
            }
        }
    }




}
