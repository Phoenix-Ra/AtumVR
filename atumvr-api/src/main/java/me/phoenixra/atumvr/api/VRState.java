package me.phoenixra.atumvr.api;

import org.jetbrains.annotations.NotNull;

public interface VRState{

    void init() throws Throwable;

    boolean isInitialized();

    boolean isPaused();


    int getEyeMaxWidth();

    int getEyeMaxHeight();


    @NotNull
    VRProvider getVrProvider();

}
