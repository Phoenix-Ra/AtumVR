package me.phoenixra.atumvr.core.openvr;

import lombok.Getter;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumconfig.core.config.AtumConfigManager;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.VRProviderType;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VREvent;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VR.ETrackingUniverseOrigin_TrackingUniverseStanding;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_SetTrackingSpace;
import static org.lwjgl.openvr.VRSystem.VRSystem_PollNextEvent;
import static org.lwjgl.openvr.VRSystem.VRSystem_ShouldApplicationPause;


public abstract class OpenVRProvider implements VRProvider {


    @Getter
    private VRApp attachedApp;

    @Getter
    private final List<OpenVREvent> vrEventsReceived = new ArrayList<>();
    

    @Getter
    private VRDevicesManager devicesManager;

    @Getter
    private VRInputHandler inputHandler;

    @Getter
    private VRRenderer vrRenderer;

    @Getter
    private int eyeTextureWidth;
    @Getter
    private int eyeTextureHeight;

    @Getter
    private boolean initialized = false;

    @Getter
    private boolean paused;




    @Override
    public void initializeVR(@NotNull VRApp vrApp) {
        if(initialized) {
            throw new VRException("Tried to initialize already initialized provider!");
        }

        this.attachedApp = vrApp;

        this.inputHandler = createVRInputHandler();
        this.devicesManager = createDevicesManager();
        this.vrRenderer = createVRRenderer(vrApp);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            attachedApp.logInfo("Initializing VR App");
            IntBuffer error = stack.mallocInt(1);
            int token = VR_InitInternal(error, EVRApplicationType_VRApplication_Scene);
            if (error.get(0) != EVRInitError_VRInitError_None) {
                throw new VRException(
                        VR.VR_GetVRInitErrorAsEnglishDescription(error.get(0))
                );
            }
            attachedApp.logInfo("Successfully initialized VR app...");

            OpenVR.create(token);


            if (OpenVR.VRSystem == null || OpenVR.VRCompositor == null) {
                throw new VRException(
                        "VRSystem and VRCompositor not found!"
                );
            }
            attachedApp.logInfo("Successfully initialized VR system...");

            VRCompositor_SetTrackingSpace(
                    ETrackingUniverseOrigin_TrackingUniverseStanding
            );
            attachedApp.logInfo("Successfully initialized VR Compositor...");


            if(inputHandler != null) {
                inputHandler.init();
            }
            attachedApp.logInfo("Successfully initialized VR Input handler...");

            getDevicesManager().update();

            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            VRSystem.VRSystem_GetRecommendedRenderTargetSize(widthBuffer, heightBuffer);
            eyeTextureWidth = widthBuffer.get(0);
            eyeTextureHeight = heightBuffer.get(0);

            vrRenderer.init();

            initialized = true;

        }catch (Throwable exception){
            throw new VRException(
                    "Exception while initializing VR provider",
                    exception
            );
        }
    }

    @Override
    public void onPreRender(float partialTick) {
        if(!initialized) return;
        paused = VRSystem_ShouldApplicationPause();
        devicesManager.update();
        pullVREvents();
    }

    @Override
    public void onRender(float partialTick) {
        vrRenderer.renderFrame();
    }

    @Override
    public void onPostRender(float partialTick) {

    }

    public void pullVREvents() {
        vrEventsReceived.clear();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for (VREvent vrevent = VREvent.malloc(stack);
                 VRSystem_PollNextEvent(vrevent, VREvent.SIZEOF);
                 vrevent = VREvent.malloc(stack)) {
                OpenVREvent event =
                        OpenVREvent.fromId(
                                vrevent.eventType()
                        );
                if(event==null) continue;
                this.vrEventsReceived.add(
                        event
                );
            }
        }
    }

    @Override
    public void destroy() {
        VR.VR_ShutdownInternal();

        vrRenderer.destroy();
        attachedApp.destroy();
    }

    @Override
    public VRDevicesManager createDevicesManager() {
        return new OpenVRDevicesManager(this);
    }

    @Override
    public @NotNull VRProviderType getType() {
        return VRProviderType.OPEN_VR;
    }
}
