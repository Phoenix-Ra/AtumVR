package me.phoenixra.atumvr.core.input.action;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.core.enums.ControllerType;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.VRInputHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;
import org.lwjgl.openxr.XrActionCreateInfo;
import org.lwjgl.openxr.XrActionStateGetInfo;
import org.lwjgl.system.MemoryStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.system.MemoryStack.stackCallocPointer;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;


/**
 * Abstract base class for VR multi action.
 * <p>
 *     Multi action means attached to both controllers
 * </p>
 * <p>
 *     (For input only, e.g. buttons, joystick, pose...)
 * </p>
 */
public abstract class VRMultiAction<T> extends VRAction {

    protected static final XrActionStateGetInfo getInfo = XrActionStateGetInfo.calloc()
            .type(XR10.XR_TYPE_ACTION_STATE_GET_INFO);


    @Getter
    protected final List<? extends SubAction<T>> subActions;


    public VRMultiAction(@NotNull VRProvider vrProvider,
                         @NotNull VRActionSet actionSet,
                         @NotNull ActionIdentifier id,
                         @NotNull String localizedName,
                         @NotNull XRInputActionType actionType,
                         @NotNull List<? extends SubAction<T>> subActions) {
        super(vrProvider, actionSet, id, localizedName, actionType);
        this.subActions = Collections.unmodifiableList(subActions);
    }

    protected abstract void onInit(@NotNull VRActionSet actionSet,
                                   @NotNull MemoryStack stack);

    public void init(@NotNull VRActionSet actionSet) {
        VRInputHandler inputHandler = vrProvider.getInputHandler();
        try (var stack = stackPush()) {
            var subactionPaths = stack.callocLong(subActions.size());

            int i = 0;
            for (SubAction<T> entry : subActions) {
                entry.setPathHandle(inputHandler.convertStringToXrPath(entry.pathName));
                subactionPaths.put(i, entry.pathHandle);
                i++;
            }

            XrActionCreateInfo actionCreateInfo = XrActionCreateInfo.calloc(stack).set(
                    XR10.XR_TYPE_ACTION_CREATE_INFO,
                    NULL,
                    memUTF8(this.id.getValue()),
                    actionType.getId(),
                    subActions.size(),
                    subactionPaths,
                    memUTF8(localizedName)
            );
            PointerBuffer pp = stackCallocPointer(1);

            vrProvider.checkXRError(
                    XR10.xrCreateAction(
                            actionSet.getHandle(),
                            actionCreateInfo,
                            pp
                    ),
                    "xrCreateAction"
            );
            handle = new XrAction(pp.get(), actionSet.getHandle());

            onInit(actionSet, stack);
        }
    }

    public SubAction<T> getHandSubaction(@NotNull ControllerType type){
        return subActions.get(type.ordinal());
    }

    @Getter
    public static class SubAction<T>{
        protected Map<VRInteractionProfileType, String> defaultBindings = new LinkedHashMap<>();

        @Getter
        private final @NotNull ActionIdentifier id;

        protected String pathName;
        @Setter
        protected long pathHandle;
        protected @NotNull T currentState;
        protected long lastChangeTime = 0;

        protected boolean changed = false;
        protected boolean active = false;

        public SubAction(@NotNull ActionIdentifier id,
                         @NotNull String path,
                         @NotNull T initialState){
            this.id = id;
            this.pathName = path;
            this.currentState = initialState;
        }
        public void update(@NotNull T state,
                           long lastChangeTime,
                           boolean changedSinceLastSync,
                           boolean isActive){
            this.currentState = state;
            this.lastChangeTime = lastChangeTime;
            this.changed = changedSinceLastSync;
            this.active = isActive;
        }

        public SubAction<T> putDefaultBindings(@NotNull VRInteractionProfileType profile,
                                               @Nullable String source){
            defaultBindings.put(profile, pathName+"/"+source);
            return this;
        }
        public SubAction<T> putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles,
                                               @Nullable String source){
            for(VRInteractionProfileType profile : profiles){
                defaultBindings.put(profile, pathName+"/"+source);
            }

            return this;
        }

        @Nullable
        public String getDefaultBindings(@NotNull VRInteractionProfileType profile){
            return defaultBindings.get(profile);
        }
    }
}
