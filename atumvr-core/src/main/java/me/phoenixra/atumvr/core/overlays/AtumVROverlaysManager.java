package me.phoenixra.atumvr.core.overlays;

import lombok.Getter;
import me.phoenixra.atumvr.api.AtumVRCore;
import me.phoenixra.atumvr.api.overlays.VROverlay;
import me.phoenixra.atumvr.api.overlays.VROverlaysManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AtumVROverlaysManager implements VROverlaysManager {
    @Getter
    private final AtumVRCore vrCore;

    private final Map<String, VROverlay> overlays = new ConcurrentHashMap<>();


    public AtumVROverlaysManager(@NotNull AtumVRCore vrCore){
        this.vrCore = vrCore;
    }
    @Override
    public void update() {
        for(VROverlay overlay : overlays.values()){
            try{
                overlay.update(false);

            }catch (Exception e){
                //@TODO better handling of errors
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void registerVROverlay(@NotNull VROverlay vrOverlay) {
        VROverlay previous = overlays.put(vrOverlay.getOverlayKey(),vrOverlay);
        vrOverlay.init();
        if(previous != null){
            previous.remove();
        }
    }

    @Override
    public void unregisterVROverlay(@NotNull String overlayKey) {
        VROverlay vrOverlay = overlays.remove(overlayKey);
        if(vrOverlay != null){
            vrOverlay.remove();
        }
    }

    @Override
    public @Nullable VROverlay getVROverlay(@NotNull String overlayKey) {
        return overlays.get(overlayKey);
    }

}
