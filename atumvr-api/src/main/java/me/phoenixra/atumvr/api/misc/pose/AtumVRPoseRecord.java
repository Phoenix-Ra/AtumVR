package me.phoenixra.atumvr.api.misc.pose;


import org.joml.*;

/**
 * Immutable representation of {@link AtumVRPose}
 */
public record AtumVRPoseRecord(Matrix4fc matrix, Quaternionfc orientation, Vector3fc position) implements AtumVRPose {

    public static AtumVRPoseRecord EMPTY = new AtumVRPoseRecord(new Matrix4f(), new Quaternionf(), new Vector3f());
}
