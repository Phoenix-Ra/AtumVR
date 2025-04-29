package me.phoenixra.atumvr.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VRApp {

    void init();


    void preRender(float partialTick);
    void render(float partialTick);
    void postRender(float partialTick);



    void destroy();


    boolean isInitialized();

    boolean isPaused();
    void setPaused(boolean value);



    @NotNull
    VRProvider getVrProvider();
}

