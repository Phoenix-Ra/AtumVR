package me.phoenixra.atumvr.devices.pose.impl;

import is.dreams.library.api.virtualreality.devices.VRDevice;
import is.dreams.library.api.virtualreality.devices.pose.DevicePoseMatch;
import org.jetbrains.annotations.NotNull;

public class DevicePoseControllerWatch implements DevicePoseMatch {
    @Override
    public boolean isMatching(@NotNull VRDevice vrDevice) {
        return false;
    }
}
