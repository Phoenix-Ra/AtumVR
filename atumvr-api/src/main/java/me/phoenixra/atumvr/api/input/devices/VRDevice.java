package me.phoenixra.atumvr.api.input.devices;

import org.jetbrains.annotations.NotNull;

public interface VRDevice {

    int getIndex();

    boolean isConnected();

    @NotNull
    VRPose getPose();
}
