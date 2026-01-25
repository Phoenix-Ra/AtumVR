package me.phoenixra.atumvr.core.misc.pose;

import me.phoenixra.atumvr.core.input.device.VRDevice;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

/**
 * VRPose is a class, that contains pose data and used primarily in {@link VRDevice}
 */
public interface VRPose {

    /**
     * The Pose matrix
     *
     * @return the 4x4 matrix
     */
    Matrix4fc matrix();

    /**
     * The orientation
     *
     * @return the quaternion
     */
    Quaternionfc orientation();

    /**
     * The position
     *
     * @return the 3D vector
     */
    Vector3fc position();

}
