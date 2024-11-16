package me.phoenixra.atumvr.api;

import me.phoenixra.atumvr.api.events.VREvent;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VRApp {

    void init();

    @NotNull
    VRRenderer createVRRenderer(@NotNull VRApp vrApp);

    void preRender();
    void render();
    void postRender();

    /**
     * Get VR Events for current tick
     *
     * @return list of VR Events
     */
    @NotNull
    List<VREvent> getVrEventsReceived();


    void destroy();


    boolean isInitialized();

    boolean isPaused();
    void setPaused(boolean value);



    @NotNull
    VRRenderer getVrRenderer();


    @NotNull
    VRCore getVrCore();
}
