package me.phoenixra.atumvr.core.openvr;

import lombok.Getter;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumconfig.api.tuples.Pair;
import me.phoenixra.atumconfig.core.config.AtumConfigManager;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.provider.VRProviderType;
import me.phoenixra.atumvr.api.provider.openvr.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.provider.openvr.events.OpenVREvent;
import me.phoenixra.atumvr.api.provider.openvr.exceptions.VRException;
import me.phoenixra.atumvr.api.provider.openvr.input.VRInputHandler;
import me.phoenixra.atumvr.api.provider.openvr.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VREvent;
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
    private final VRProviderType type;

    @Getter
    private VRApp attachedApp;

    @Getter
    private final List<OpenVREvent> vrEventsReceived = new ArrayList<>();
    
    @Getter
    private ConfigManager configManager;

    @Getter
    private VRDevicesManager devicesManager;

    @Getter
    private VRInputHandler inputHandler;

    @Getter
    private VRRenderer vrRenderer;

    @Getter
    private boolean initialized = false;

    @Getter
    private boolean paused;

    public OpenVRProvider(){
        this.type = VRProviderType.OPEN_VR;

    }


    @Override
    public void initializeVR(@NotNull VRApp vrApp) {
        if(initialized) {
            throw new VRException("Tried to initialize already initialized provider!");
        }

        this.attachedApp = vrApp;
        this.configManager = createConfigManager();
        this.inputHandler = createVRInputHandler();
        this.devicesManager = createDevicesManager();
        this.vrRenderer = createVRRenderer(vrApp);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            logInfo("Initializing VR App");
            IntBuffer error = stack.mallocInt(1);
            int token = VR_InitInternal(error, EVRApplicationType_VRApplication_Scene);
            if (error.get(0) != EVRInitError_VRInitError_None) {
                throw new VRException(
                        VR.VR_GetVRInitErrorAsEnglishDescription(error.get(0))
                );
            }
            logInfo("Successfully initialized VR app...");

            OpenVR.create(token);


            if (OpenVR.VRSystem == null || OpenVR.VRCompositor == null) {
                throw new VRException(
                        "VRSystem and VRCompositor not found!"
                );
            }
            logInfo("Successfully initialized VR system...");

            VRCompositor_SetTrackingSpace(
                    ETrackingUniverseOrigin_TrackingUniverseStanding
            );
            logInfo("Successfully initialized VR Compositor...");


            if(inputHandler != null) {
                inputHandler.init();
            }
            logInfo("Successfully initialized VR Input handler...");

            getDevicesManager().update();

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
    public ConfigManager createConfigManager() {
        return new AtumConfigManager(this);
    }

    @Override
    public VRDevicesManager createDevicesManager() {
        return new OpenVRDevicesManager(this);
    }

    @Override
    public Pair<Integer, Integer> getEyeResolution() {
        return null;
    }
}
