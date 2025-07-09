package me.phoenixra.atumvr.core.input.action;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRAction;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;

import java.util.function.Consumer;


public abstract class OpenXRAction implements VRAction {
    public static final String LEFT_HAND_PATH = "/user/hand/left";
    public static final String RIGHT_HAND_PATH = "/user/hand/right";

    protected OpenXRProvider provider;

    @Getter
    protected XrAction handle;

    @Getter
    protected final OpenXRActionSet actionSet;

    @Getter
    protected final String id;
    @Getter
    protected final String localizedName;

    protected final XRInputActionType actionType;

    public OpenXRAction(OpenXRProvider provider,
                        OpenXRActionSet actionSet,
                        String id,
                        String localizedName,
                        XRInputActionType actionType) {
        this.provider = provider;
        this.actionSet = actionSet;
        this.id = id;
        this.localizedName = localizedName;
        this.actionType = actionType;
    }

    public abstract void init(OpenXRActionSet actionSet);

    public abstract void update(@Nullable Consumer<String> listener);


    public void destroy() {
        if(handle != null) {
            XR10.xrDestroyAction(handle);
        }
    }


}
