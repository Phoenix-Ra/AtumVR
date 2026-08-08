package me.phoenixra.atumvr.api.input.treadmill;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

/**
 * Represents a view of locomotion hardware
 * (omnidirectional treadmills, VR shoes, gait pads)
 */
public interface AtumVRTreadmillView {

    AtumVRTreadmillView EMPTY = new AtumVRTreadmillView() {
        private final Vector2f motion = new Vector2f();

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public @NotNull Vector2fc getMotion() {
            return motion;
        }

        @Override
        public float getSpeed() {
            return 0f;
        }

        @Override
        public @Nullable Quaternionfc getBodyOrientation() {
            return null;
        }
    };


    /**
     * If the hardware is connected and delivering data
     */
    boolean isActive();

    /**
     * Gait direction relative to the user's body facing.
     * <p>
     *     x - strafe right, y - forward.
     *     Unit length while moving, zero when idle
     * </p>
     */
    @NotNull Vector2fc getMotion();

    /**
     * Ground speed in meters per second
     */
    float getSpeed();

    /**
     * Body facing in play space, as reported by the hardware direction sensor.
     * <p>
     *     Null when the hardware has no such sensor -
     *     never derived from other devices, the app decides the fallback
     * </p>
     */
    @Nullable Quaternionfc getBodyOrientation();

    /**
     * Yaw of {@link #getBodyOrientation()} in radians, NaN when orientation is null
     */
    default float getBodyYaw() {
        Quaternionfc q = getBodyOrientation();
        if (q == null) {
            return Float.NaN;
        }
        float x = q.x(), y = q.y(), z = q.z(), w = q.w();
        return (float) Math.atan2(2f * (x * z + w * y), 1f - 2f * (x * x + y * y));
    }

    /**
     * Play space velocity combined from motion, speed and body orientation
     *
     * @return dest, or null when body orientation is unknown
     */
    default @Nullable Vector3f getVelocity(@NotNull Vector3f dest) {
        Quaternionfc orientation = getBodyOrientation();
        if (orientation == null) {
            return null;
        }
        Vector2fc motion = getMotion();
        dest.set(motion.x(), 0f, -motion.y()).mul(getSpeed());
        return orientation.transform(dest);
    }

    /**
     * Align the hardware forward reference with the given play space yaw.
     * No-op for hardware that keeps itself calibrated
     */
    default void recenter(float playSpaceYawRadians) {
    }
}
