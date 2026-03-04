package me.phoenixra.atumvr.api.misc.pose;

import org.jetbrains.annotations.NotNull;
import org.joml.*;

/**
 * Mutable representation of {@link AtumVRPose}
 */
public class AtumVRPoseMutable implements AtumVRPose {

    private final Matrix4f matrix;
    private final Quaternionf orientation;
    private final Vector3f position;

    public AtumVRPoseMutable(){
        this.matrix = new Matrix4f();
        this.orientation = new Quaternionf();
        this.position = new Vector3f();
    }


    public void update(@NotNull AtumVRPoseRecord pose){
        update(pose.matrix(), pose.orientation(), pose.position());
    }

    public void update(@NotNull Matrix4fc poseMatrix,
                       @NotNull Quaternionfc orientation,
                       @NotNull Vector3fc position){
        this.matrix.set(poseMatrix);
        this.orientation.set(orientation);
        this.position.set(position);
    }


    @Override
    public Matrix4fc matrix(){
        return matrix;
    }

    @Override
    public Quaternionfc orientation() {
        return orientation;
    }

    @Override
    public Vector3fc position() {
        return position;
    }
}
