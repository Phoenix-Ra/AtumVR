package me.phoenixra.atumvr.api;

import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;

public interface VRApp {

    void init();

    @NotNull
    VRRenderer createVRRenderer(@NotNull VRApp vrApp);

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
    VRRenderer getVrRenderer();


    @NotNull
    VRCore getVrCore();
}
