package me.phoenixra.atumvr.api.devices.pose.impl;

import me.phoenixra.atumvr.api.devices.VRDevice;
import me.phoenixra.atumvr.api.devices.pose.DevicePoseMatch;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DevicePoseControllerUp implements DevicePoseMatch {

    @Override
    public boolean isMatching(@NotNull VRDevice vrDevice) {
        Quaternionf devicePosition = vrDevice.getPose().getPosition();

        Vector3f forward = new Vector3f(0, 0, -1f);
        Vector3f up = new Vector3f(0, 1, 0); // Down direction in world space

        /*Vector3f transformedForward = AtumQuaternion.rotateVector(forward,devicePosition);

        return transformedForward.dot(up) > 0.8;*/
        return false;
    }
}
