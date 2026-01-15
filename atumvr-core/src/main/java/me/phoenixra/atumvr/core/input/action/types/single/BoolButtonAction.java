package me.phoenixra.atumvr.core.input.action.types.single;

import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.api.input.action.VRActionDataButton;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRSingleAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateBoolean;
import org.lwjgl.system.MemoryStack;

import java.util.List;
import java.util.function.Consumer;

public class BoolButtonAction extends XRSingleAction<Boolean> implements VRActionDataButton {


    public BoolButtonAction(XRProvider provider,
                            XRActionSet actionSet,
                            ActionIdentifier id,
                            String localizedName) {
        super(provider, actionSet, id, localizedName, XRInputActionType.BOOLEAN);
        currentState = false;
    }

    @Override
    protected void onInit(XRActionSet actionSet, MemoryStack stack) {

    }
    @Override
    public void update(@Nullable Consumer<String> listener) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var state = XrActionStateBoolean.calloc(stack)
                    .type(actionType.getStateId());
            getInfo.action(handle);
            provider.checkXRError(
                    XR10.xrGetActionStateBoolean(
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

    @Override
    public boolean isPressed() {
        return this.currentState;
    }

    @Override
    public boolean isButtonChanged() {
        return this.changed;
    }

    @Override
    public long getButtonLastChangeTime() {
        return this.lastChangeTime;
    }


    @Override
    public BoolButtonAction putDefaultBindings(@NotNull List<XRInteractionProfile> profiles, @Nullable String source) {
        return (BoolButtonAction) super.putDefaultBindings(profiles, source);
    }

    @Override
    public BoolButtonAction putDefaultBindings(@NotNull XRInteractionProfile profile, @Nullable String source) {
        return (BoolButtonAction) super.putDefaultBindings(profile, source);
    }
}
