package me.phoenixra.atumvr.core.input.body.hand;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandJoint;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandView;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPoseMutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;


public class XRHand implements AtumVRHandView {

    @Getter
    private final ControllerType side;

    private final AtumVRPoseMutable[] poses = new AtumVRPoseMutable[AtumVRHandJoint.COUNT];
    private final float[] radii = new float[AtumVRHandJoint.COUNT];

    private final EnumSet<AtumVRHandJoint> trackedJoints = EnumSet.noneOf(AtumVRHandJoint.class);
    private final Set<AtumVRHandJoint> trackedJointsView = Collections.unmodifiableSet(trackedJoints);

    private boolean active;
    private DataSource dataSource = DataSource.UNKNOWN;


    public XRHand(@NotNull ControllerType side) {
        this.side = side;
        for (int i = 0; i < poses.length; i++) {
            poses[i] = new AtumVRPoseMutable();
        }
    }

    // -------- PROVIDER API --------


    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDataSource(@NotNull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void updateJoint(@NotNull AtumVRHandJoint joint,
                            @NotNull Matrix4fc matrix,
                            @NotNull Quaternionfc orientation,
                            @NotNull Vector3fc position,
                            float radius) {
        int index = joint.ordinal();
        poses[index].update(matrix, orientation, position);
        radii[index] = radius;
        trackedJoints.add(joint);
    }

    public void clear() {
        active = false;
        trackedJoints.clear();
        dataSource = DataSource.UNKNOWN;
    }

    // -------- VIEW --------

    @Override
    public boolean isTracked() {
        return active;
    }

    @Override
    public @Nullable AtumVRPose getJointPose(@NotNull AtumVRHandJoint joint) {
        return trackedJoints.contains(joint) ? poses[joint.ordinal()] : null;
    }

    @Override
    public float getJointRadius(@NotNull AtumVRHandJoint joint) {
        return trackedJoints.contains(joint) ? radii[joint.ordinal()] : 0f;
    }

    @Override
    public void collectJoints(@NotNull BiConsumer<AtumVRHandJoint, AtumVRPose> sink) {
        for (AtumVRHandJoint joint : trackedJoints) {
            sink.accept(joint, poses[joint.ordinal()]);
        }
    }

    @Override
    public @NotNull Set<AtumVRHandJoint> getTrackedJoints() {
        return trackedJointsView;
    }

    @Override
    public @NotNull DataSource getDataSource() {
        return dataSource;
    }
}
