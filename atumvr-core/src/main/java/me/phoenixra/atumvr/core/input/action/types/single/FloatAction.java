package me.phoenixra.atumvr.core.input.action.types.single;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRSingleAction;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataFloat;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateFloat;
import org.lwjgl.system.MemoryStack;


@Getter
public class FloatAction extends XRSingleAction<Float> implements VRActionDataFloat {



    public FloatAction(@NotNull XRProvider vrProvider,
                       @NotNull XRActionSet actionSet,
                       @NotNull ActionIdentifier id,
                       @NotNull String localizedName) {
        super(vrProvider, actionSet, id, localizedName,  XRInputActionType.FLOAT);
    }





    @Override
    protected void onInit(XRActionSet actionSet, MemoryStack stack) {

    }
    @Override
    public void update() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var state = XrActionStateFloat.calloc(stack)
                    .type(actionType.getStateId());
            getInfo.action(handle);
            vrProvider.checkXRError(
                    XR10.xrGetActionStateFloat(
                            vrProvider.getSession().getHandle(),
                            getInfo,
                            state
                    ),
                    "xrGetActionStateFloat"
            );
            this.currentState = state.currentState();
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
    public float getFloat() {
        return currentState;
    }
}
