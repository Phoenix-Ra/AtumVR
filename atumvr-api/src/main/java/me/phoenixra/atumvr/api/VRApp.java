package me.phoenixra.atumvr.api;

import me.phoenixra.atumvr.api.rendering.VRSceneRenderer;
import org.jetbrains.annotations.NotNull;

public interface VRApp {

    void init();

    void update();

    void destroy();


    boolean isInitialized();

    @NotNull
    String getAppKey();

    @NotNull
    VRSceneRenderer getSceneRenderer();


    @NotNull
    VRCore getVrCore();
}
