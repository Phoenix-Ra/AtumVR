package me.phoenixra.atumvr.api.scene;

import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;

public interface VRScene {

    void init();

    void prepareFrame();

    void destroy();
    @NotNull
    VRRenderer getVrRenderer();
}
