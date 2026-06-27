package me.phoenixra.atumvr.core.input.profile.tracker;

import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;


public enum EmulatedTrackerPreset {

    T_POSE {
        @Override
        public Vector3f offset(@NotNull ViveTrackerRole role, float seconds, @NotNull Vector3f dest) {
            return baseOffset(role, dest);
        }
    },

    IDLE {
        @Override
        public Vector3f offset(@NotNull ViveTrackerRole role, float seconds, @NotNull Vector3f dest) {
            baseOffset(role, dest);
            dest.y += 0.02f * (float) Math.sin(seconds * BOB_SPEED);
            switch (role) {
                case LEFT_WRIST, LEFT_ELBOW -> dest.y += 0.03f * (float) Math.sin(seconds * BOB_SPEED + 0.5f);
                case RIGHT_WRIST, RIGHT_ELBOW -> dest.y += 0.03f * (float) Math.sin(seconds * BOB_SPEED - 0.5f);
                default -> { }
            }
            return dest;
        }
    };

    private static final float BOB_SPEED = 1.5f;

    public abstract Vector3f offset(@NotNull ViveTrackerRole role, float seconds, @NotNull Vector3f dest);

    protected static Vector3f baseOffset(@NotNull ViveTrackerRole role, @NotNull Vector3f dest) {
        return switch (role) {
            case WAIST          -> dest.set(0.00f, -0.70f, 0.00f);
            case CHEST          -> dest.set(0.00f, -0.40f, 0.00f);
            case LEFT_SHOULDER  -> dest.set(-0.20f, -0.25f, 0.00f);
            case RIGHT_SHOULDER -> dest.set( 0.20f, -0.25f, 0.00f);
            case LEFT_ELBOW     -> dest.set(-0.45f, -0.25f, 0.00f);
            case RIGHT_ELBOW    -> dest.set( 0.45f, -0.25f, 0.00f);
            case LEFT_WRIST     -> dest.set(-0.70f, -0.25f, 0.00f);
            case RIGHT_WRIST    -> dest.set( 0.70f, -0.25f, 0.00f);
            case LEFT_KNEE      -> dest.set(-0.12f, -1.05f, 0.00f);
            case RIGHT_KNEE     -> dest.set( 0.12f, -1.05f, 0.00f);
            case LEFT_ANKLE     -> dest.set(-0.12f, -1.45f, 0.00f);
            case RIGHT_ANKLE    -> dest.set( 0.12f, -1.45f, 0.00f);
            case LEFT_FOOT      -> dest.set(-0.12f, -1.55f, 0.12f);
            case RIGHT_FOOT     -> dest.set( 0.12f, -1.55f, 0.12f);
            case HANDHELD_OBJECT -> dest.set(0.00f, -0.30f, -0.40f);
            case CAMERA          -> dest.set(0.00f,  0.10f, -0.50f);
            case KEYBOARD        -> dest.set(0.00f, -0.60f, -0.35f);
        };
    }
}
