package me.phoenixra.atumvr.api.provider.openvr.devices.pose.impl;

import me.phoenixra.atumvr.api.provider.openvr.devices.VRDevice;
import me.phoenixra.atumvr.api.provider.openvr.devices.pose.DevicePoseMatch;
import org.jetbrains.annotations.NotNull;

public class DevicePoseControllerWatch implements DevicePoseMatch {
    @Override
    public boolean isMatching(@NotNull VRDevice vrDevice) {
        return false;
    }
}
