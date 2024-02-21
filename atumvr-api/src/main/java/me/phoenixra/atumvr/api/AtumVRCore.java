package me.phoenixra.atumvr.api;

import lombok.Getter;
import me.phoenixra.atumconfig.api.ConfigOwner;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.overlays.VROverlaysManager;

public abstract class AtumVRCore implements ConfigOwner {

    @Getter
    private AtumVRAPI api;
    @Getter
    private VRDevicesManager devicesManager;
    @Getter
    private VROverlaysManager overlaysManager;

    public AtumVRCore(){
        if(AtumVRAPI.getInstance() == null) {
            AtumVRAPI.Instance.loadDefault();
        }
        api = AtumVRAPI.getInstance();

        devicesManager = AtumVRAPI.getInstance().createVrDevicesManager(this);
        overlaysManager = AtumVRAPI.getInstance().createVrOverlaysManager(this);
    }







}
