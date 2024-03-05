package me.phoenixra.atumvr.core;

import lombok.Getter;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumconfig.core.config.AtumConfigManager;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.overlays.VROverlaysManager;
import me.phoenixra.atumvr.core.devices.AtumVRDevicesManager;
import me.phoenixra.atumvr.core.overlays.AtumVROverlaysManager;


public abstract class AtumVRCore implements VRCore {
    @Getter
    private ConfigManager configManager;

    @Getter
    private VRApp vrApp;
    @Getter
    private VRDevicesManager devicesManager;
    @Getter
    private VROverlaysManager overlaysManager;


    public AtumVRCore(){
        configManager = new AtumConfigManager(this);

        vrApp = createVRApp();

        devicesManager = new AtumVRDevicesManager(this);
        overlaysManager = new AtumVROverlaysManager(this);

    }


    @Override
    public void initializeVR() {
        vrApp.init();
    }


    @Override
    public void clear() {
        vrApp.destroy();
    }
}
