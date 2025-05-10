package me.phoenixra.atumvr.api.input.devices;

import me.phoenixra.atumvr.api.enums.EyeType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public interface VRDeviceHMD extends VRDevice{
    @NotNull
    Matrix4f getEyePose(@NotNull EyeType eyeType);
}
