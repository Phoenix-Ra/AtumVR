package me.phoenixra.atumvr.api;

import me.phoenixra.atumvr.api.scene.VRSceneRenderer;
import org.jetbrains.annotations.NotNull;

public interface VRApp {

    void init();



    void onPreTick();
    void onTick();
    void onPostTick();

    void destroy();


    boolean isInitialized();

    boolean isPaused();
    void setPaused(boolean value);


    @NotNull
    String getAppKey();

    @NotNull
    VRSceneRenderer getSceneRenderer();


    @NotNull
    VRCore getVrCore();
}
