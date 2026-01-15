package me.phoenixra.atumvr.core.input.action.types.single;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRSingleAction;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateFloat;
import org.lwjgl.system.MemoryStack;

import java.util.function.Consumer;

@Getter
public class FloatAction extends XRSingleAction<Float> {



    public FloatAction(XRProvider provider,
                       XRActionSet actionSet,
                       ActionIdentifier id,
                       String localizedName) {
        super(provider, actionSet, id, localizedName,  XRInputActionType.FLOAT);
    }





    @Override
    protected void onInit(XRActionSet actionSet, MemoryStack stack) {

    }
    @Override
    public void update(@Nullable Consumer<String> listener) {
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

            if(listener != null && changed){
                listener.accept(id.getValue());
            }
        }
    }
}
