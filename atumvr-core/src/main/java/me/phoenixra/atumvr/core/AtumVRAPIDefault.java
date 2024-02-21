package me.phoenixra.atumvr.core;

import me.phoenixra.atumvr.api.AtumVRAPI;
import me.phoenixra.atumvr.api.AtumVRCore;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.overlays.VROverlaysManager;
import me.phoenixra.atumvr.core.devices.AtumVRDevicesManager;
import me.phoenixra.atumvr.core.overlays.AtumVROverlaysManager;
import org.jetbrains.annotations.NotNull;

public class AtumVRAPIDefault implements AtumVRAPI {
    @Override
    public @NotNull VRDevicesManager createVrDevicesManager(AtumVRCore vrCore) {
        return new AtumVRDevicesManager(vrCore);
    }

    @Override
    public @NotNull VROverlaysManager createVrOverlaysManager(AtumVRCore vrCore) {
        return new AtumVROverlaysManager(vrCore);
    }
}
