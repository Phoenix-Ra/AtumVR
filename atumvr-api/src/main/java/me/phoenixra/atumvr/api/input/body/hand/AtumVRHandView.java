package me.phoenixra.atumvr.api.input.body.hand;

import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Represents a view of a single tracked hand skeleton
 */
public interface AtumVRHandView {

    AtumVRHandView EMPTY = new AtumVRHandView() {
        @Override
        public boolean isTracked() {
            return false;
        }

        @Override
        public @Nullable AtumVRPose getJointPose(@NotNull AtumVRHandJoint joint) {
            return null;
        }

        @Override
        public void collectJoints(@NotNull BiConsumer<AtumVRHandJoint, AtumVRPose> sink) {
        }

        @Override
        public @NotNull Set<AtumVRHandJoint> getTrackedJoints() {
            return Collections.emptySet();
        }
    };


    /**
     * Whether the hand is currently tracked as a whole
     */
    boolean isTracked();


    @Nullable AtumVRPose getJointPose(@NotNull AtumVRHandJoint joint);


    default boolean isJointTracked(@NotNull AtumVRHandJoint joint) {
        return getJointPose(joint) != null;
    }

    /**
     * Joint collision sphere radius in meters,
     * 0 if the provider doesn't report radii
     */
    default float getJointRadius(@NotNull AtumVRHandJoint joint) {
        return 0f;
    }


    default void collectJoints(@NotNull BiConsumer<AtumVRHandJoint, AtumVRPose> sink) {
        for (int i = 0; i < AtumVRHandJoint.COUNT; i++) {
            AtumVRHandJoint joint = AtumVRHandJoint.fromIndex(i);
            AtumVRPose pose = getJointPose(joint);
            if (pose != null) {
                sink.accept(joint, pose);
            }
        }
    }

    /**
     * Get currently tracked joints
     *
     * @return the set of tracked joints
     */
    default @NotNull Set<AtumVRHandJoint> getTrackedJoints() {
        EnumSet<AtumVRHandJoint> out = EnumSet.noneOf(AtumVRHandJoint.class);
        collectJoints((joint, pose) -> out.add(joint));
        return out;
    }

    /**
     * What the runtime derives the skeleton from,
     * UNKNOWN when the runtime doesn't report it
     */
    default @NotNull DataSource getDataSource() {
        return DataSource.UNKNOWN;
    }


    enum DataSource {
        UNKNOWN,
        HAND,
        CONTROLLER
    }
}
