package me.phoenixra.atumvr.core.input.body;

import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Joint layout used by {@link XRBodyEmulator}, offsets are relative to the HMD.
 */
public enum EmulatedBodyPreset {

    T_POSE {
        @Override
        public Vector3f offset(@Nullable AtumVRBodyJoint joint, float seconds, @NotNull Vector3f dest) {
            return baseOffset(joint, dest);
        }
    },

    IDLE {
        @Override
        public Vector3f offset(@Nullable AtumVRBodyJoint joint, float seconds, @NotNull Vector3f dest) {
            baseOffset(joint, dest);
            dest.y += 0.02f * (float) Math.sin(seconds * BOB_SPEED);
            if (joint == null) {
                return dest;
            }
            switch (joint) {
                case LEFT_WRIST, LEFT_ELBOW, LEFT_HAND ->
                        dest.y += 0.03f * (float) Math.sin(seconds * BOB_SPEED + 0.5f);
                case RIGHT_WRIST, RIGHT_ELBOW, RIGHT_HAND ->
                        dest.y += 0.03f * (float) Math.sin(seconds * BOB_SPEED - 0.5f);
                default -> { }
            }
            return dest;
        }
    };

    private static final float BOB_SPEED = 1.5f;

    /**
     * Get the joint offset from the HMD
     *
     * @param joint   the joint, or null for a tracker that is not on the body
     * @param seconds the animation time
     * @param dest    the vector to write into
     * @return dest
     */
    public abstract Vector3f offset(@Nullable AtumVRBodyJoint joint, float seconds, @NotNull Vector3f dest);

    protected static Vector3f baseOffset(@Nullable AtumVRBodyJoint joint, @NotNull Vector3f dest) {
        if (joint == null) {
            // anything not on the body, held in front of the chest
            return dest.set(0.00f, -0.30f, -0.40f);
        }
        return switch (joint) {
            case HEAD           -> dest.set(0.00f,  0.00f, 0.00f);
            case NECK           -> dest.set(0.00f, -0.15f, 0.05f);
            case CHEST          -> dest.set(0.00f, -0.40f, 0.00f);
            case WAIST          -> dest.set(0.00f, -0.70f, 0.00f);
            case LEFT_SHOULDER  -> dest.set(-0.20f, -0.25f, 0.00f);
            case RIGHT_SHOULDER -> dest.set( 0.20f, -0.25f, 0.00f);
            case LEFT_ELBOW     -> dest.set(-0.45f, -0.25f, 0.00f);
            case RIGHT_ELBOW    -> dest.set( 0.45f, -0.25f, 0.00f);
            case LEFT_WRIST     -> dest.set(-0.70f, -0.25f, 0.00f);
            case RIGHT_WRIST    -> dest.set( 0.70f, -0.25f, 0.00f);
            case LEFT_HAND      -> dest.set(-0.80f, -0.25f, 0.00f);
            case RIGHT_HAND     -> dest.set( 0.80f, -0.25f, 0.00f);
            case LEFT_HIP       -> dest.set(-0.12f, -0.75f, 0.00f);
            case RIGHT_HIP      -> dest.set( 0.12f, -0.75f, 0.00f);
            case LEFT_KNEE      -> dest.set(-0.12f, -1.05f, 0.00f);
            case RIGHT_KNEE     -> dest.set( 0.12f, -1.05f, 0.00f);
            case LEFT_ANKLE     -> dest.set(-0.12f, -1.45f, 0.00f);
            case RIGHT_ANKLE    -> dest.set( 0.12f, -1.45f, 0.00f);
            case LEFT_FOOT      -> dest.set(-0.12f, -1.55f, 0.12f);
            case RIGHT_FOOT     -> dest.set( 0.12f, -1.55f, 0.12f);
        };
    }
}
