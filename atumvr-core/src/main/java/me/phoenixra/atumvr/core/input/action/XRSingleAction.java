package me.phoenixra.atumvr.core.input.action;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;
import org.lwjgl.openxr.XrActionCreateInfo;
import org.lwjgl.openxr.XrActionStateGetInfo;
import org.lwjgl.system.MemoryStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.system.MemoryStack.stackCallocPointer;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

@Getter
public abstract class XRSingleAction<T> extends XRAction {

    protected static final XrActionStateGetInfo getInfo = XrActionStateGetInfo.calloc()
            .type(XR10.XR_TYPE_ACTION_STATE_GET_INFO);

    protected Map<XRInteractionProfile, String> defaultBindings = new LinkedHashMap<>();


    protected T currentState;
    protected long lastChangeTime;

    protected boolean changed;
    protected boolean active;

    public XRSingleAction(XRProvider provider,
                          XRActionSet actionSet,
                          ActionIdentifier id, String localizedName,
                          XRInputActionType actionType) {
        super(provider, actionSet, id, localizedName, actionType);
    }

    protected abstract void onInit(XRActionSet actionSet,
                                   MemoryStack stack);

    @Override
    public void init(XRActionSet actionSet) {

        try (var stack = stackPush()) {
            XrActionCreateInfo actionCreateInfo = XrActionCreateInfo.calloc(stack).set(
                    XR10.XR_TYPE_ACTION_CREATE_INFO,
                    NULL,
                    memUTF8(this.id.getValue()),
                    actionType.getId(),
                    0,
                    null,
                    memUTF8(localizedName)
            );
            PointerBuffer pp = stackCallocPointer(1);

            provider.checkXRError(
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


    public XRSingleAction<T> putDefaultBindings(@NotNull XRInteractionProfile profile,
                                                @Nullable String source){
        defaultBindings.put(profile, source);
        return this;
    }
    public XRSingleAction<T> putDefaultBindings(@NotNull List<XRInteractionProfile> profiles,
                                                @Nullable String source){
        for(XRInteractionProfile profile : profiles){
            defaultBindings.put(profile, source);
        }


        return this;
    }

    @Nullable
    public String getDefaultBindings(XRInteractionProfile profile){
        return defaultBindings.get(profile);
    }

}
