package me.phoenixra.atumvr.api.input.haptics.bhaptics;

/**
 * Feedback on a single motor of a device
 *
 * @param index     motor index within the device position, clamped to 0-39
 * @param intensity feedback strength, 0-100 is the regular range, clamped to 0-500
 */
public record BHapticsDotPoint(int index, int intensity) {

    public BHapticsDotPoint {
        index = Math.max(0, Math.min(39, index));
        intensity = Math.max(0, Math.min(500, intensity));
    }
}
