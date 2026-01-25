package me.phoenixra.atumvr.core.input.action.types.single;

import lombok.Getter;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.VRSingleAction;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataFloat;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateFloat;
import org.lwjgl.system.MemoryStack;


@Getter
public class FloatAction extends VRSingleAction<Float> implements VRActionDataFloat {



    public FloatAction(@NotNull VRProvider vrProvider,
                       @NotNull VRActionSet actionSet,
                       @NotNull ActionIdentifier id,
                       @NotNull String localizedName) {
        super(vrProvider, actionSet, id, localizedName,  XRInputActionType.FLOAT);
    }





    @Override
    protected void onInit(VRActionSet actionSet, MemoryStack stack) {

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
