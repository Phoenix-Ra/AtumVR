package me.phoenixra.atumvr.core.input.action.types;

import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HapticPulseControllerAction extends HapticPulseAction{

    public HapticPulseControllerAction(XRProvider vrProvider,
                             XRActionSet actionSet,
                             String id,
                             String localizedName) {
        super(vrProvider, actionSet, id, localizedName, List.of(LEFT_HAND_PATH, RIGHT_HAND_PATH));
    }

    public void triggerHapticPulse(ControllerType controllerType,
                                   float frequency, float amplitude,
                                   float durationSeconds) {
        triggerHapticPulse(
                controllerType,
                frequency,
                amplitude,
                (long) (durationSeconds * 1_000_000_000)
        );
    }

    public void triggerHapticPulse(ControllerType controllerType,
                                   float frequency, float amplitude,
                                   long durationNanoSec) {
        triggerHapticPulse(
                controllerType == ControllerType.LEFT ? LEFT_HAND_PATH : RIGHT_HAND_PATH,
                frequency,
                amplitude,
                durationNanoSec
        );
    }

    @Override
    public HapticPulseControllerAction putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles, @Nullable String source) {
        return (HapticPulseControllerAction) super.putDefaultBindings(profiles, source);
    }

    @Override
    public HapticPulseControllerAction putDefaultBindings(@NotNull VRInteractionProfileType profile, @Nullable String source) {
        return (HapticPulseControllerAction) super.putDefaultBindings(profile, source);
    }
}
