package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.core.enums.ControllerType;
import me.phoenixra.atumvr.core.misc.pose.VRPose;
import me.phoenixra.atumvr.core.misc.pose.VRPoseMutable;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import org.jetbrains.annotations.NotNull;

/**
 * VRDevice for controllers
 */
public class VRDeviceController extends VRDevice {
    public static final String ID_LEFT = "controller_left";
    public static final String ID_RIGHT = "controller_right";

    @Getter
    private final ControllerType type;

    @Getter
    private boolean gripActive;

    private final VRPoseMutable gripPose = new VRPoseMutable();


    private final PoseMultiAction aimAction;
    private final PoseMultiAction gripAction;
    private final HapticPulseAction hapticPulseAction;

    public VRDeviceController(@NotNull VRProvider vrProvider,
                              @NotNull ControllerType controllerType,
                              @NotNull PoseMultiAction aimAction,
                              @NotNull PoseMultiAction gripAction,
                              @NotNull HapticPulseAction hapticPulseAction) {
        super(vrProvider, controllerType==ControllerType.LEFT ? ID_LEFT : ID_RIGHT);
        this.type = controllerType;
        this.aimAction = aimAction;
        this.gripAction = gripAction;
        this.hapticPulseAction = hapticPulseAction;

    }

    @Override
    public void update() {
        var subActionAim = aimAction.getSubActions().get(type.ordinal());
        var subActionGrip = gripAction.getSubActions().get(type.ordinal());

        pose.update(subActionAim.getCurrentState());
        active = subActionAim.isActive();

        gripPose.update(subActionGrip.getCurrentState());
        gripActive = subActionGrip.isActive();



    }


    /**
     * Trigger haptic pulse on controller device,
     * with frequency 160 and amplitude 1
     *
     * @param durationMilliseconds the pulse duration in milliseconds
     */
    public void triggerHapticPulse(float durationMilliseconds){
        triggerHapticPulse(
                160f,
                1f,
                (long) (durationMilliseconds * 1_000_000_000)
        );
    }

    /**
     * Trigger haptic pulse on controller device
     *
     * @param frequency the pulse frequency
     * @param amplitude the pulse amplitude
     * @param durationMilliseconds the pulse duration in milliseconds
     */
    public void triggerHapticPulse(float frequency, float amplitude,
                                    float durationMilliseconds){
        triggerHapticPulse(
                frequency,
                amplitude,
                (long) (durationMilliseconds * 1_000_000)
        );
    }

    /**
     * Trigger haptic pulse on controller device
     *
     * @param frequency the pulse frequency
     * @param amplitude the pulse amplitude
     * @param durationNanoSec the pulse duration in nanoseconds
     */
    public void triggerHapticPulse(float frequency, float amplitude, long durationNanoSec) {
        hapticPulseAction.triggerHapticPulse(type, frequency, amplitude, durationNanoSec);
    }

    /**
     * Get aim pose of the controller.<br>
     * Same as {@link #getPose()}
     *
     * @return VRPose
     */
    public @NotNull VRPose getAimPose() {
        return pose;
    }

    /**
     * Get grip pose of the controller.
     *
     * @return VRPose
     */
    public @NotNull VRPose getGripPose() {
        return gripPose;
    }


    public static String getId(@NotNull ControllerType type){
        return type == ControllerType.LEFT
                ? ID_LEFT
                : ID_RIGHT;
    }
}
