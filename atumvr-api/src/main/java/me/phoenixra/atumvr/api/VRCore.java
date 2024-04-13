package me.phoenixra.atumvr.api;


import me.phoenixra.atumconfig.api.ConfigOwner;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.overlays.VROverlaysManager;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VRCore extends ConfigOwner {




    void initializeVR();

    void clear();


    @NotNull
    VRApp createVRApp();
    @Nullable
    VRInputHandler createVRInputHandler();


    @NotNull
    VRApp getVrApp();
    @NotNull
    VRDevicesManager getDevicesManager();
    @Nullable
    VRInputHandler getInputHandler();
    @NotNull
    VROverlaysManager getOverlaysManager();



}
