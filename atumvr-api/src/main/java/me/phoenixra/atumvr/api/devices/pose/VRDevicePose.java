package me.phoenixra.atumvr.api.devices.pose;


import lombok.AllArgsConstructor;
import lombok.Getter;
import me.phoenixra.atumvr.api.misc.VRLocation;
import org.joml.Matrix4f;


@Getter
@AllArgsConstructor
public class VRDevicePose {
    private boolean poseValid;
    private TrackingState trackingState;

    private VRLocation location;
    private Matrix4f locationMatrix;

    private float[] velocity;
    private float[] angularVelocity;


}
