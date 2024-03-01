package me.phoenixra.atumvr.api;


import me.phoenixra.atumconfig.api.ConfigOwner;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.overlays.VROverlaysManager;
import me.phoenixra.atumvr.api.scene.VRSceneRenderer;
import org.jetbrains.annotations.NotNull;

public interface VRCore extends ConfigOwner {




    void initializeVR();

    void clear();

    @NotNull
    VRSceneRenderer createSceneRenderer(@NotNull VRApp vrApp);

    @NotNull
    VRApp getVrApp();
    @NotNull
    VRDevicesManager getDevicesManager();
    @NotNull
    VROverlaysManager getOverlaysManager();


}
