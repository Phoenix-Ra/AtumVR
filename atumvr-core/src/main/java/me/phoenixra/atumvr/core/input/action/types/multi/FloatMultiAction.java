package me.phoenixra.atumvr.core.input.action.types.multi;

import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRMultiAction;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateFloat;
import org.lwjgl.system.MemoryStack;

import java.util.List;
import java.util.function.Consumer;

public class FloatMultiAction extends XRMultiAction<Float> {




    public FloatMultiAction(XRProvider provider,
                            XRActionSet actionSet,
                            ActionIdentifier id,
                            String localizedName,
                            List<SubAction<Float>> subActions) {
        super(provider, actionSet, id, localizedName, XRInputActionType.FLOAT, subActions);
    }


    @Override
    protected void onInit(XRActionSet actionSet, MemoryStack stack) {

    }

    @Override
    public void update(@Nullable Consumer<String> listener) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for(SubAction<Float> entry : subActions) {
                var state = XrActionStateFloat.calloc(stack)
                        .type(actionType.getStateId());
                getInfo.subactionPath(entry.getPathHandle());
                getInfo.action(handle);
                provider.checkXRError(
                        XR10.xrGetActionStateFloat(
                                provider.getState().getVrSession().getHandle(),
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
                if(listener != null
                        && state.changedSinceLastSync()){
                    listener.accept(entry.getId().getValue());
                }
            }
        }
    }

}
