package me.phoenixra.atumvr.api;

import me.phoenixra.atumvr.api.exceptions.VRInputException;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

public interface VRApp {

    void init();

    @NotNull
    VRRenderer createVRRenderer(@NotNull VRApp vrApp);

    void onPreTick();
    void onTick();
    void onPostTick();

    void destroy();


    long getInputActionHandle(@NotNull String actionPath,
                              @NotNull MemoryStack stack) throws VRInputException;
    long getInputActionSetHandle(@NotNull String actionSetPath,
                                 @NotNull MemoryStack stack) throws VRInputException;
    long getInputSourceHandle(@NotNull String path,
                              @NotNull MemoryStack stack) throws VRInputException;

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
