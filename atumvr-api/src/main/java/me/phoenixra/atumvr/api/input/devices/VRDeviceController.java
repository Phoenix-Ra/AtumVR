package me.phoenixra.atumvr.api.input.devices;

import me.phoenixra.atumvr.api.enums.EyeType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public interface VRDeviceController extends VRDevice{
    @NotNull
    Matrix4f getAimPose();
    @NotNull
    Matrix4f getGripPose();
}
