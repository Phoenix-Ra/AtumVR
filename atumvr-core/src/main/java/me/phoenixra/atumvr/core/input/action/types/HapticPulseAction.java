package me.phoenixra.atumvr.core.input.action.types;

import me.phoenixra.atumconfig.api.tuples.PairRecord;
import me.phoenixra.atumvr.core.enums.ControllerType;
import me.phoenixra.atumvr.core.exceptions.VRException;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.VRInputHandler;
import me.phoenixra.atumvr.core.input.action.VRAction;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class HapticPulseAction extends VRAction {



    protected Map<VRInteractionProfileType, PairRecord<String,String>> defaultBindings = new LinkedHashMap<>();


    public HapticPulseAction(VRProvider vrProvider,
                             VRActionSet actionSet,
                             String id,
                             String localizedName) {
        super(vrProvider,
                actionSet,
                new ActionIdentifier(id),
                localizedName,
                XRInputActionType.HAPTIC
        );

    }


    @Override
    public void init(@NotNull VRActionSet actionSet) {
        VRInputHandler inputHandler = vrProvider.getInputHandler();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer paths = stack.callocLong(2);
            paths.put(0, inputHandler.convertStringToXrPath(LEFT_HAND_PATH));
            paths.put(1, inputHandler.convertStringToXrPath(RIGHT_HAND_PATH));

            XrActionCreateInfo actionCreateInfo = XrActionCreateInfo
                    .calloc(stack)
                    .type(XR_TYPE_ACTION_CREATE_INFO)
                    .next(0)
                    .actionType(actionType.getId())
                    .actionName(stack.UTF8(id.getValue()))
                    .localizedActionName(stack.UTF8(localizedName))
                    .countSubactionPaths(2)
                    .subactionPaths(paths);

            PointerBuffer pAction = stack.callocPointer(1);
            xrCreateAction(actionSet.getHandle(), actionCreateInfo, pAction);
            handle = new XrAction(pAction.get(0), actionSet.getHandle());
        }
    }

    @Override
    public void update() {

    }


    public void triggerHapticPulse(ControllerType controllerType,
                                   float frequency, float amplitude,
                                   float durationSeconds){
        triggerHapticPulse(
                controllerType,
                frequency,
                amplitude,
                (long) (durationSeconds * 1_000_000_000)
        );
    }

    public void triggerHapticPulse(ControllerType controllerType,
                                   float frequency, float amplitude,
                                   long durationNanoSec){
        if(handle == null){
            throw new VRException("Tried to apply haptic pulse before action initialized");
        }
        XrSession session = vrProvider.getSession().getHandle();
        VRInputHandler inputHandler = vrProvider.getInputHandler();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            long subPath = inputHandler.convertStringToXrPath(
                    controllerType == ControllerType.LEFT
                            ? LEFT_HAND_PATH
                            : RIGHT_HAND_PATH
            );

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


            xrApplyHapticFeedback(session, info, XrHapticBaseHeader.create(vib.address()));
        }
    }


    public HapticPulseAction putDefaultBindings(@NotNull VRInteractionProfileType profile,
                                                @Nullable String source){
        defaultBindings.put(profile,
                new PairRecord<>(
                        LEFT_HAND_PATH+"/"+source,
                        RIGHT_HAND_PATH+"/"+source
                )
        );
        return this;
    }

    public HapticPulseAction putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles,
                                                @Nullable String source){
        for(VRInteractionProfileType profile : profiles){
            defaultBindings.put(
                    profile,
                    new PairRecord<>(
                            LEFT_HAND_PATH+"/"+source,
                            RIGHT_HAND_PATH+"/"+source
                    )
            );
        }


        return this;
    }

    @Nullable
    public PairRecord<String,String> getDefaultBindings(VRInteractionProfileType profile){
        return defaultBindings.get(profile);
    }
}
