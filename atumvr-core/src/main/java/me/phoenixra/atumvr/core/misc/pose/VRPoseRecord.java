package me.phoenixra.atumvr.core.misc.pose;


import org.jetbrains.annotations.NotNull;
import org.joml.*;

/**
 * VRPoseRecord is an immutable implementation of {@link VRPose}
 */
public record VRPoseRecord(@NotNull Matrix4fc matrix,
                           @NotNull Quaternionfc orientation,
                           @NotNull Vector3fc position) implements VRPose{

    public static VRPoseRecord EMPTY = new VRPoseRecord(new Matrix4f(), new Quaternionf(), new Vector3f());
}
