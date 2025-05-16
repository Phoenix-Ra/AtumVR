package me.phoenixra.atumvr.core.input.action.types.single;

import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.OpenXRSingleAction;
import me.phoenixra.atumvr.core.input.action.OpenXRActionSet;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateFloat;
import org.lwjgl.system.MemoryStack;

public class FloatAction extends OpenXRSingleAction<Float> {


    public FloatAction(OpenXRProvider provider,
                       OpenXRActionSet actionSet,
                       String name,
                       String localizedName) {
        super(provider, actionSet, name, localizedName, XRInputActionType.FLOAT);
        currentState = 0f;
    }

    @Override
    protected void onInit(OpenXRActionSet actionSet, MemoryStack stack) {

    }
    @Override
    public void update() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var state = XrActionStateFloat.calloc(stack)
                    .type(actionType.getStateId());
            getInfo.action(handle);
            provider.checkXRError(
                    XR10.xrGetActionStateFloat(
                            provider.getState().getVrSession().getHandle(),
                            getInfo,
                            state
                    ),
                    "xrGetActionStateFloat"
            );
            this.currentState = state.currentState();
            this.changed = state.changedSinceLastSync();
            this.lastChangeTime = state.lastChangeTime();
            this.active = state.isActive();
        }
    }
}
