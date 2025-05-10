package me.phoenixra.atumvr.api.input;

import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.input.devices.VRDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;


public interface VRInputHandler<DEVICE extends VRDevice> {

    void init();
    void update();





    Collection<DEVICE> getDevices();

    DEVICE getDevice(String id);

    void registerDevice(DEVICE device);

    @NotNull
    DEVICE getHmd();
    @NotNull
    DEVICE getRightController();
    @NotNull
    DEVICE getLeftController();




    @NotNull
    VRProvider getVrProvider();


}
