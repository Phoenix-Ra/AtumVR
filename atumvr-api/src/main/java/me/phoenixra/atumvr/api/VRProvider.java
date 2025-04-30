package me.phoenixra.atumvr.api;



import me.phoenixra.atumconfig.api.ConfigOwner;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface VRProvider {




    void initializeVR(@NotNull VRApp app) throws Throwable;

    void onPreRender(float partialTick);
    void onRender(float partialTick);
    void onPostRender(float partialTick);


    void destroy();

    int getEyeTextureWidth();
    int getEyeTextureHeight();


    @Nullable
    VRInputHandler createVRInputHandler();
    VRDevicesManager createDevicesManager();
    @NotNull
    VRRenderer createVRRenderer(@NotNull VRApp vrApp);

    boolean isPaused();
    boolean isInitialized();


    @NotNull
    VRApp getAttachedApp();
    @NotNull
    VRDevicesManager getDevicesManager();
    @Nullable
    VRInputHandler getInputHandler();
    @NotNull
    VRRenderer getVrRenderer();


    @NotNull VRProviderType getType();



}
