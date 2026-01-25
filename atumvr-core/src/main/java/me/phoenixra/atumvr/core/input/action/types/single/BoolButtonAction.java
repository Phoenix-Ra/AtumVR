package me.phoenixra.atumvr.core.input.action.types.single;

import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataButton;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.VRSingleAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionStateBoolean;
import org.lwjgl.system.MemoryStack;

import java.util.List;

public class BoolButtonAction extends VRSingleAction<Boolean> implements VRActionDataButton {


    public BoolButtonAction(@NotNull VRProvider vrProvider,
                            @NotNull VRActionSet actionSet,
                            @NotNull ActionIdentifier id,
                            @NotNull String localizedName) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.BOOLEAN);
        currentState = false;
    }

    @Override
    protected void onInit(@NotNull VRActionSet actionSet, @NotNull MemoryStack stack) {

    }
    @Override
    public void update() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var state = XrActionStateBoolean.calloc(stack)
                    .type(actionType.getStateId());
            getInfo.action(handle);
            vrProvider.checkXRError(
                    XR10.xrGetActionStateBoolean(
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
    public BoolButtonAction putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles, @Nullable String source) {
        return (BoolButtonAction) super.putDefaultBindings(profiles, source);
    }

    @Override
    public BoolButtonAction putDefaultBindings(@NotNull VRInteractionProfileType profile, @Nullable String source) {
        return (BoolButtonAction) super.putDefaultBindings(profile, source);
    }
}
