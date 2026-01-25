package me.phoenixra.atumvr.core.input.action;

import lombok.Getter;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;

import java.util.function.Consumer;

/**
 * Abstract base class for VR action.
 * <p>
 *     For Both input (e.g. buttons, joystick, pose...)
 *     and output (e.g. haptic)
 * </p>
 */
public abstract class VRAction {

    public static final String LEFT_HAND_PATH = "/user/hand/left";
    public static final String RIGHT_HAND_PATH = "/user/hand/right";

    protected @NotNull VRProvider vrProvider;

    @Getter
    protected XrAction handle;

    @Getter
    protected final @NotNull VRActionSet actionSet;

    @Getter
    protected final @NotNull ActionIdentifier id;
    @Getter
    protected final @NotNull String localizedName;

    protected final @NotNull XRInputActionType actionType;

    public VRAction(@NotNull VRProvider vrProvider,
                    @NotNull VRActionSet actionSet,
                    @NotNull ActionIdentifier id,
                    @NotNull String localizedName,
                    @NotNull XRInputActionType actionType) {
        this.vrProvider = vrProvider;
        this.actionSet = actionSet;
        this.id = id;
        this.localizedName = localizedName;
        this.actionType = actionType;
    }

    public abstract void init(@NotNull VRActionSet actionSet);

    public abstract void update();


    public void destroy() {
        if(handle != null) {
            XR10.xrDestroyAction(handle);
        }
    }


}
