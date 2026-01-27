package me.phoenixra.atumvr.core.input.action;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.action.VRAction;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;

/**
 * Abstract base class for VR action.
 */
public abstract class XRAction implements VRAction {

    public static final String LEFT_HAND_PATH = "/user/hand/left";
    public static final String RIGHT_HAND_PATH = "/user/hand/right";

    protected @NotNull XRProvider vrProvider;

    @Getter
    protected XrAction handle;

    @Getter
    protected final @NotNull XRActionSet actionSet;

    @Getter
    protected final @NotNull VRActionIdentifier id;
    @Getter
    protected final @NotNull String localizedName;

    protected final @NotNull XRInputActionType actionType;

    public XRAction(@NotNull XRProvider vrProvider,
                    @NotNull XRActionSet actionSet,
                    @NotNull VRActionIdentifier id,
                    @NotNull String localizedName,
                    @NotNull XRInputActionType actionType) {
        this.vrProvider = vrProvider;
        this.actionSet = actionSet;
        this.id = id;
        this.localizedName = localizedName;
        this.actionType = actionType;
    }

    public abstract void init(@NotNull XRActionSet actionSet);

    public abstract void update();


    public void destroy() {
        if(handle != null) {
            XR10.xrDestroyAction(handle);
        }
    }


}
