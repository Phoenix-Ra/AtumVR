package me.phoenixra.atumvr.core.input.action;

import lombok.Getter;
import me.phoenixra.atumconfig.api.tuples.PairRecord;
import me.phoenixra.atumvr.api.input.action.VRActionSet;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfileType;
import me.phoenixra.atumvr.core.session.XRInstance;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrActionSet;
import org.lwjgl.openxr.XrActionSetCreateInfo;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackCallocPointer;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

/**
 * Abstract base class for VR action set
 */
public abstract class XRActionSet implements VRActionSet {

    private final XRProvider vrProvider;
    @Getter
    private final String name;
    @Getter
    private final String localizedName;

    @Getter
    private XrActionSet handle;
    @Getter
    private final int priority;

    @Getter
    private List<XRAction> actions = new ArrayList<>();


    public XRActionSet(@NotNull XRProvider vrProvider,
                       @NotNull String name,
                       @NotNull String localizedName,
                       int priority) {
        this.vrProvider = vrProvider;
        this.name = name;
        this.localizedName = localizedName;
        this.priority = priority;
    }

    /**
     * Load actions and return the result
     *
     * @param vrProvider the VR provider
     * @return the list of loaded actions
     */
    protected abstract List<XRAction> loadActions(@NotNull XRProvider vrProvider);


    @Override
    public void init() {
        try (MemoryStack stack = stackPush()) {
            XRInstance vrInstance = vrProvider.getSession().getInstance();
            XrActionSetCreateInfo actionSetCreateInfo = XrActionSetCreateInfo
                    .calloc(stack)
                    .set(
                            XR10.XR_TYPE_ACTION_SET_CREATE_INFO,
                            NULL,
                            memUTF8(this.name),
                            memUTF8(this.localizedName),
                            priority
                    );
            PointerBuffer actionSetPoint = stackCallocPointer(1);
            vrProvider.checkXRError(
                    XR10.xrCreateActionSet(
                            vrInstance.getHandle(),
                            actionSetCreateInfo,
                            actionSetPoint
                    ),
                    "xrCreateActionSet"
            );
            handle = new XrActionSet(actionSetPoint.get(0), vrInstance.getHandle());

            actions = loadActions(vrProvider);
            for (var action : actions) {
                action.init(this);
            }

        }
    }

    @Override
    public void update() {
        for (var action : getActions()) {
            action.update();
        }
    }


    /**
     * Get default bindings, that are suggested during initialization of VR session
     *
     * @param profile the interaction profile
     * @return the list of default bindings
     */
    public List<PairRecord<XRAction, String>> getDefaultBindings(@NotNull XRInteractionProfileType profile){
        List<PairRecord<XRAction, String>> out = new ArrayList<>();
        for(XRAction action : actions){
            if(action instanceof XRSingleAction<?> singleAction){
                var bind = singleAction.getDefaultBindings(profile);
                if(bind == null) {
                    continue;
                }
                out.add(new PairRecord<>(
                        singleAction,
                        bind
                ));
            }else if(action instanceof XRMultiAction<?> multiAction){
                for(XRMultiAction.SubAction<?> subAction : multiAction.getSubActions()){
                    var bind = subAction.getDefaultBindings(profile);
                    if(bind == null) {
                        continue;
                    }
                    out.add(new PairRecord<>(
                            multiAction,
                            bind
                    ));
                }
            }else if(action instanceof HapticPulseAction hapticPulseAction){
                var bind = hapticPulseAction.getDefaultBindings(profile);
                if(bind == null) {
                    continue;
                }
                out.add(new PairRecord<>(
                        action,
                        bind.first()
                ));
                out.add(new PairRecord<>(
                        action,
                        bind.second()
                ));
            }
        }
        return out;
    }

    // -------- DESTROY --------

    @Override
    public final void destroy() {
        if (handle != null) {
            XR10.xrDestroyActionSet(handle);
        }
        for (var action : getActions()) {
            action.destroy();
        }
    }

}
