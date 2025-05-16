package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.VRProvider;
import org.jetbrains.annotations.NotNull;

public interface VRScene {

    void init();

    void prepareFrame();

    void destroy();


    @NotNull
    VRRenderer getVrRenderer();

    default VRProvider getVrProvider(){
        return getVrRenderer().getVrProvider();
    }
}
