package me.phoenixra.atumvr.api.provider.openvr.scene;

import me.phoenixra.atumvr.api.provider.openvr.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;

public interface VRScene {

    void init();

    void prepareFrame();

    void destroy();
    @NotNull
    VRRenderer getVrRenderer();
}
