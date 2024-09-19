package me.phoenixra.atumvr.api.devices;


import me.phoenixra.atumvr.api.VRCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface VRDevicesManager {


    void update();


    /**
     * If true calls VRCompositor.VRCompositor_WaitGetPoses
     * on update call.
     *
     * @return true/false
     */
    boolean isWaitPoses();

    /**
     * Set if update method should call VRCompositor.VRCompositor_WaitGetPoses
     * before updating devices state.
     * <br><br>
     * Use it if your app do not call
     * VRCompositor_WaitGetPoses method somewhere else,
     * because it can limit FPS when called more than once per frame
     *
     */
    void setWaitPoses(boolean flag);


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
    VRDevice getDeviceByIndex(int index);

    @Nullable
    VRDevice getHMD();
    @Nullable
    VRDevice getRightHand();
    @Nullable
    VRDevice getLeftHand();

    @NotNull
    VRCore getVrCore();
}
