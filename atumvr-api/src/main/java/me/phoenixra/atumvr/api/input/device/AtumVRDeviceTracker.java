package me.phoenixra.atumvr.api.input.device;

/**
 * Abstract VRDevice for tracker
 */
public interface AtumVRDeviceTracker extends AtumVRDevice {

    /**
     * Trigger a haptic pulse on the tracker,
     *  with frequency 160 and amplitude 1.
     * <p>
     *     Only has an effect if the tracker
     *     supports haptic pulse
     * </p>
     *
     * @param durationMilliseconds the pulse duration in milliseconds
     */
    default void triggerHapticPulse(float durationMilliseconds){
        triggerHapticPulse(
                160f,
                1f,
                (long) (durationMilliseconds * 1_000_000)
        );
    }

    /**
     * Trigger a haptic pulse on the tracker.
     * <p>
     *     Only has an effect if the tracker
     *     supports haptic pulse
     * </p>
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
     * Trigger a haptic pulse on the tracker.
     * <p>
     *     Only has an effect if the tracker
     *     supports haptic pulse
     * </p>
     * @param frequency the pulse frequency
     * @param amplitude the pulse amplitude
     * @param durationNanoSec the pulse duration in nanoseconds
     */
    void triggerHapticPulse(float frequency, float amplitude,
                            long durationNanoSec);
}
