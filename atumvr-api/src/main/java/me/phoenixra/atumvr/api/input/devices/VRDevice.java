package me.phoenixra.atumvr.api.input.devices;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public interface VRDevice {

    @NotNull
    String getId();

    boolean isActive();

    boolean isPoseValid();

    @NotNull
    Matrix4f getPose();
}
