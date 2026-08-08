package me.phoenixra.atumvr.api.input.haptics.bhaptics;

/**
 * Feedback at a normalized point on a device surface,
 * the Player interpolates it onto the nearest motors
 *
 * @param x          horizontal position on the device, clamped to 0-1
 * @param y          vertical position on the device, clamped to 0-1
 * @param intensity  feedback strength, 0-100 is the regular range, clamped to 0-500
 * @param motorCount how many nearby motors render the point, clamped to 1-3
 */
public record BHapticsPathPoint(float x, float y, int intensity, int motorCount) {

    public BHapticsPathPoint {
        x = Math.max(0f, Math.min(1f, x));
        y = Math.max(0f, Math.min(1f, y));
        intensity = Math.max(0, Math.min(500, intensity));
        motorCount = Math.max(1, Math.min(3, motorCount));
    }

    public BHapticsPathPoint(float x, float y, int intensity) {
        this(x, y, intensity, 3);
    }
}
