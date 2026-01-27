package me.phoenixra.atumvr.api.input.device;

import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.misc.pose.VRPose;
import org.jetbrains.annotations.NotNull;


/**
 * VRDevice for controllers
 */
public interface VRDeviceController extends VRDevice{
    String ID_LEFT = "controller_left";
    String ID_RIGHT = "controller_right";

    /**
     * Get device id from the controller type
     *
     * @param type the controller type
     * @return the device id
     */
    static String getId(@NotNull ControllerType type){
        return type == ControllerType.LEFT
                ? ID_LEFT
                : ID_RIGHT;
    }

    /**
     * Get controller type
     *
     * @return type
     */
    @NotNull
    ControllerType getType();

    /**
     * Get aim pose of the controller.<br>
     * Same as {@link #getPose()}
     *
     * @return VRPose
     */
    @NotNull VRPose getAimPose();

    /**
     * Get grip pose of the controller.
     *
     * @return VRPose
     */
    @NotNull VRPose getGripPose();

    /**
     * If Grip pose action is active
     *
     * @return true/false
     */
    boolean isGripActive();



    /**
     * Trigger haptic pulse on controller device,
     * with frequency 160 and amplitude 1
     *
     * @param durationMilliseconds the pulse duration in milliseconds
     */
    default void triggerHapticPulse(float durationMilliseconds){
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
    default void triggerHapticPulse(float frequency, float amplitude,
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
    void triggerHapticPulse(float frequency, float amplitude,
                            long durationNanoSec);
}
