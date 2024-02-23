package me.phoenixra.atumvr.api.overlays;


import me.phoenixra.atumvr.api.VRCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VROverlaysManager {

    void update();

    void registerVROverlay(@NotNull VROverlay vrOverlay);

    void unregisterVROverlay(@NotNull String overlayKey);

    @Nullable
    VROverlay getVROverlay(@NotNull String overlayKey);

    @NotNull
    VRCore getVrCore();

}
