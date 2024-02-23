package me.phoenixra.atumvr.api;


import me.phoenixra.atumconfig.api.ConfigOwner;
import me.phoenixra.atumconfig.api.config.Config;
import me.phoenixra.atumconfig.api.config.LoadableConfig;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.overlays.VROverlaysManager;
import org.jetbrains.annotations.NotNull;

public interface VRCore extends ConfigOwner {

    void update();


    void initializeVR();

    void clear();


    @NotNull
    VRApp getVrApp();
    @NotNull
    VRDevicesManager getDevicesManager();
    @NotNull
    VROverlaysManager getOverlaysManager();


}
