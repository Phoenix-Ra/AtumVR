package me.phoenixra.atumvr.api.input.body;

import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Represents a view of a part or whole body
 */
public interface AtumVRBodyView {


    AtumVRBodyView EMPTY = new AtumVRBodyView() {
        @Override
        public @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint) {
            return null;
        }

        @Override
        public void collectJoints(@NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        }

        @Override
        public @NotNull Set<AtumVRBodyJoint> getTrackedJoints() {
            return Collections.emptySet();
        }
    };


    default boolean isJointTracked(@NotNull AtumVRBodyJoint joint){
        return getJointPose(joint) != null;
    }


    @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint);


    default void collectJoints(@NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink){
        for(AtumVRBodyJoint joint : AtumVRBodyJoint.values()){
            AtumVRPose pose = getJointPose(joint);
            if(pose != null){
                sink.accept(joint, pose);
            }
        }
    }

    /**
     * Get Currently tracked joints
     *
     * @return the set of tracked joints
     */
    default @NotNull Set<AtumVRBodyJoint> getTrackedJoints(){
        EnumSet<AtumVRBodyJoint> out = EnumSet.noneOf(AtumVRBodyJoint.class);
        collectJoints((joint, pose) -> out.add(joint));
        return out;
    }
}
