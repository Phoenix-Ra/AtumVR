package me.phoenixra.atumvr.api;

import org.jetbrains.annotations.NotNull;

public interface VRState{



    boolean isInitialized();

    boolean isPaused();

    boolean isRunning();




    @NotNull
    VRProvider getVrProvider();

}
