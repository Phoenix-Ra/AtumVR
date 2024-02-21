package me.phoenixra.atumvr.devices.pose.impl;

import is.dreams.library.api.misc.AtumQuaternion;
import is.dreams.library.api.virtualreality.devices.VRDevice;
import is.dreams.library.api.virtualreality.devices.pose.DevicePoseMatch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class DevicePoseControllerRight implements DevicePoseMatch {
    @Override
    public boolean isMatching(@NotNull VRDevice vrDevice) {
        AtumQuaternion devicePosition = vrDevice.getPose().getPosition();

        Vector3f forward = new Vector3f(0, 0, -1f);
        Vector3f right = new Vector3f(1, 0, 0); // Down direction in world space

        Vector3f transformedForward = AtumQuaternion.rotateVector(forward,devicePosition);

        return transformedForward.dot(right) > 0.8;
    }
}
