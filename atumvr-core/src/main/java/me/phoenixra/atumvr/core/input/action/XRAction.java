package me.phoenixra.atumvr.core.input.action;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRAction;
import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;

import java.util.function.Consumer;


public abstract class XRAction implements VRAction {
    public static final String LEFT_HAND_PATH = "/user/hand/left";
    public static final String RIGHT_HAND_PATH = "/user/hand/right";

    protected XRProvider provider;

    @Getter
    protected XrAction handle;

    @Getter
    protected final XRActionSet actionSet;

    @Getter
    protected final ActionIdentifier id;
    @Getter
    protected final String localizedName;

    protected final XRInputActionType actionType;

    public XRAction(XRProvider provider,
                    XRActionSet actionSet,
                    ActionIdentifier id,
                    String localizedName,
                    XRInputActionType actionType) {
        this.provider = provider;
        this.actionSet = actionSet;
        this.id = id;
        this.localizedName = localizedName;
        this.actionType = actionType;
    }

    public abstract void init(XRActionSet actionSet);

    public abstract void update(@Nullable Consumer<String> listener);


    public void destroy() {
        if(handle != null) {
            XR10.xrDestroyAction(handle);
        }
    }


}
