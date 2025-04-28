package me.phoenixra.atumvr.api.rendering;

import org.jetbrains.annotations.NotNull;

public interface VRScene {

    void init();

    void prepareFrame();

    void destroy();
    @NotNull
    VRRenderer getVrRenderer();
}
