package me.phoenixra.atumvr.api;

import me.phoenixra.atumconfig.api.ConfigOwner;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VRApp extends ConfigOwner {

    boolean init() throws Throwable;


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

