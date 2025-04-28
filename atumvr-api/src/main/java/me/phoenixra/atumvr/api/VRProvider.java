package me.phoenixra.atumvr.api;



import me.phoenixra.atumconfig.api.ConfigOwner;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumconfig.api.tuples.Pair;
import me.phoenixra.atumvr.api.provider.VRProviderType;
import me.phoenixra.atumvr.api.provider.openvr.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.provider.openvr.input.VRInputHandler;
import me.phoenixra.atumvr.api.provider.openvr.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface VRProvider extends ConfigOwner {




    void initializeVR(@NotNull VRApp app);

    void onPreRender(float partialTick);
    void onRender(float partialTick);
    void onPostRender(float partialTick);


    void destroy();



    ConfigManager createConfigManager();
    @Nullable
    VRInputHandler createVRInputHandler();
    VRDevicesManager createDevicesManager();
    @NotNull
    VRRenderer createVRRenderer(@NotNull VRApp vrApp);

    boolean isPaused();
    boolean isInitialized();

    Pair<Integer, Integer> getEyeResolution();

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
