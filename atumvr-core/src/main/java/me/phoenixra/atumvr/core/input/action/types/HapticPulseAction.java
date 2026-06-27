package me.phoenixra.atumvr.core.input.action.types;

import lombok.Getter;
import me.phoenixra.atumvr.api.exceptions.AtumVRException;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.XRInputHandler;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class HapticPulseAction extends XRAction {

    @Getter
    private final List<String> subActionPaths;

    protected Map<VRInteractionProfileType, List<String>> defaultBindings = new LinkedHashMap<>();



    public HapticPulseAction(XRProvider vrProvider,
                             XRActionSet actionSet,
                             String id,
                             String localizedName,
                             @NotNull List<String> subActionPaths) {
        super(vrProvider,
                actionSet,
                new VRActionIdentifier(id),
                localizedName,
                XRInputActionType.HAPTIC
        );
        this.subActionPaths = List.copyOf(subActionPaths);
    }


    @Override
    public void init(@NotNull XRActionSet actionSet) {
        XRInputHandler inputHandler = vrProvider.getInputHandler();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer paths = stack.callocLong(subActionPaths.size());
            for (int i = 0; i < subActionPaths.size(); i++) {
                paths.put(i, inputHandler.convertStringToXrPath(subActionPaths.get(i)));
            }

            XrActionCreateInfo actionCreateInfo = XrActionCreateInfo
                    .calloc(stack)
                    .type(XR_TYPE_ACTION_CREATE_INFO)
                    .next(0)
                    .actionType(actionType.getId())
                    .actionName(stack.UTF8(id.getValue()))
                    .localizedActionName(stack.UTF8(localizedName))
                    .countSubactionPaths(subActionPaths.size())
                    .subactionPaths(paths);

            PointerBuffer pAction = stack.callocPointer(1);
            vrProvider.checkXRError(
                    XR10.xrCreateAction(
                            actionSet.getHandle(),
                            actionCreateInfo,
                            pAction
                    ),
                    "xrCreateAction"
            );
            handle = new XrAction(pAction.get(0), actionSet.getHandle());
        }
    }

    @Override
    public void update() {

    }



    public void triggerHapticPulse(@NotNull String userPath,
                                   float frequency, float amplitude,
                                   float durationSeconds) {
        triggerHapticPulse(
                userPath,
                frequency,
                amplitude,
                (long) (durationSeconds * 1_000_000_000)
        );
    }

    public void triggerHapticPulse(@NotNull String userPath,
                                   float frequency, float amplitude,
                                   long durationNanoSec) {
        if (handle == null) {
            throw new AtumVRException("Tried to apply haptic pulse before action initialized");
        }
        if (!subActionPaths.contains(userPath)) {
            throw new AtumVRException(
                    "Haptic action '" + id.getValue() + "' has no target user path: " + userPath
            );
        }
        if (vrProvider.isShuttingDown()) {
            return;
        }
        XrSession session = vrProvider.getSession().getHandle();
        XRInputHandler inputHandler = vrProvider.getInputHandler();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            long subPath = inputHandler.convertStringToXrPath(userPath);

            XrHapticActionInfo info = XrHapticActionInfo
                    .calloc(stack)
                    .next(NULL)
                    .type(XR_TYPE_HAPTIC_ACTION_INFO)
                    .action(handle)
                    .subactionPath(subPath);

            XrHapticVibration vib = XrHapticVibration
                    .calloc(stack)
                    .next(NULL)
                    .type(XR_TYPE_HAPTIC_VIBRATION)
                    .duration(durationNanoSec)
                    .frequency(frequency)
                    .amplitude(amplitude);


            xrStopHapticFeedback(session, info);
            xrApplyHapticFeedback(session, info, XrHapticBaseHeader.create(vib.address()));
        }
    }


    public void stop() {
        if (handle == null) {
            return;
        }
        XrSession session = vrProvider.getSession().getHandle();
        if (session == null) {
            return;
        }
        XRInputHandler inputHandler = vrProvider.getInputHandler();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            for (String userPath : subActionPaths) {
                long subPath;
                try {
                    subPath = inputHandler.convertStringToXrPath(userPath);
                } catch (Throwable t) {
                    continue;
                }

                XrHapticActionInfo info = XrHapticActionInfo
                        .calloc(stack)
                        .next(NULL)
                        .type(XR_TYPE_HAPTIC_ACTION_INFO)
                        .action(handle)
                        .subactionPath(subPath);

                try {
                    xrStopHapticFeedback(session, info);
                } catch (Throwable ignored) {
                }
            }
        }
    }


    // -------- DEFAULT BINDINGS --------

    public HapticPulseAction putDefaultBindings(@NotNull VRInteractionProfileType profile,
                                                @Nullable String source) {
        List<String> binds = new ArrayList<>(subActionPaths.size());
        for (String path : subActionPaths) {
            binds.add(path + "/" + source);
        }
        defaultBindings.put(profile, binds);
        return this;
    }

    public HapticPulseAction putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles,
                                                @Nullable String source) {
        for (VRInteractionProfileType profile : profiles) {
            putDefaultBindings(profile, source);
        }
        return this;
    }

    @Nullable
    public List<String> getDefaultBindings(VRInteractionProfileType profile) {
        return defaultBindings.get(profile);
    }
}
