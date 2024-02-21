package me.phoenixra.atumvr.api.devices;


import me.phoenixra.atumvr.api.AtumVRCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface VRDevicesManager {


    void update();




    void addObserverOnDeviceDisconnected(String serialNum, Consumer<VRDevice> deviceConsumer);
    void addObserverOnDeviceConnected(String serialNum, Consumer<VRDevice> deviceConsumer);
    void removeObserverOnDeviceDisconnected(String serialNum, Consumer<VRDevice> deviceConsumer);
    void removeObserverOnDeviceConnected(String serialNum, Consumer<VRDevice> deviceConsumer);




    @NotNull
    List<VRDevice> getAvailableDevices();

    @NotNull
    List<VRDevice> getDevicesByRole(@NotNull VRDeviceRole role);
    @NotNull
    List<VRDevice> getDevicesByType(@NotNull VRDeviceType type);
    @Nullable
    VRDevice getDeviceBySerial(@NotNull String serial);

    @Nullable
    VRDevice getHMD();
    @Nullable
    VRDevice getRightHand();
    @Nullable
    VRDevice getLeftHand();

    @NotNull
    AtumVRCore getVrCore();
}
