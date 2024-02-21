package me.phoenixra.atumvr.devices.pose;

import is.dreams.library.api.misc.AtumQuaternion;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class VRDevicePose {
    private boolean poseValid;
    private TrackingState trackingState;

    private AtumQuaternion position;

    private float[] velocity;
    private float[] angularVelocity;


}
