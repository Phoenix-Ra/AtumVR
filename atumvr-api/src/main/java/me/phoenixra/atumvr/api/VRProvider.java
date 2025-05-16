package me.phoenixra.atumvr.api;


import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.rendering.RenderContext;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;


public interface VRProvider {

    void initializeVR() throws Throwable;

    void preRender(@NotNull RenderContext context);
    void render(@NotNull RenderContext context);
    void postRender(@NotNull RenderContext context);


    void destroy();



    @NotNull
    VRState getState();

    @NotNull
    VRInputHandler getInputHandler();

    @NotNull
    VRRenderer getRenderer();



    @NotNull String getAppName();

    @NotNull
    VRLogger getLogger();


}
