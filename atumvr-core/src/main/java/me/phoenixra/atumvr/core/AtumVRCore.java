package me.phoenixra.atumvr.core;

import lombok.Getter;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumconfig.core.config.AtumConfigManager;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.input.VRInputHandler;
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
    @Getter
    private VRInputHandler inputHandler;


    public AtumVRCore(){
        configManager = createConfigManager();

        vrApp = createVRApp();
        inputHandler = createVRInputHandler();

        devicesManager = createDevicesManager();
        overlaysManager = createOverlaysManager();

    }


    @Override
    public void initializeVR() {
        vrApp.init();
    }


    @Override
    public void clear() {
        vrApp.destroy();
    }

    @Override
    public ConfigManager createConfigManager() {
        return new AtumConfigManager(this);
    }

    @Override
    public VRDevicesManager createDevicesManager() {
        return new AtumVRDevicesManager(this);
    }

    @Override
    public VROverlaysManager createOverlaysManager() {
        return new AtumVROverlaysManager(this);
    }
}
