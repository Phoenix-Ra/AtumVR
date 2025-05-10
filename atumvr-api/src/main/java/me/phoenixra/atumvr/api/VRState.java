package me.phoenixra.atumvr.api;

import org.jetbrains.annotations.NotNull;

public interface VRState{

    void init() throws Throwable;

    boolean isInitialized();

    boolean isPaused();
    boolean isRunning();


    int getEyeTexWidth();

    int getEyeTexHeight();



    @NotNull
    VRProvider getVrProvider();

}
