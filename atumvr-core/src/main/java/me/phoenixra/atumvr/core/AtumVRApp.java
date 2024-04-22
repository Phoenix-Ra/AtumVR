package me.phoenixra.atumvr.core;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_SetTrackingSpace;
import static org.lwjgl.openvr.VRSystem.VRSystem_PollNextEvent;
import static org.lwjgl.openvr.VRSystem.VRSystem_ShouldApplicationPause;

public abstract class AtumVRApp implements VRApp {
    @Getter
    private VRCore vrCore;
    @Getter
    private String appKey = "empty";

    @Getter
    private VRRenderer vrRenderer;

    @Getter
    private List<VREvent> vrEventsTick;


    @Getter @Setter
    private boolean paused;
    @Getter
    private boolean initialized = false;
    public AtumVRApp(VRCore vrCore){
        this.vrCore = vrCore;
        this.vrRenderer = createVRRenderer(this);
    }
    @Override
    public void init() {
        if(initialized) return;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            vrCore.logInfo("Initializing VR App");
            IntBuffer error = stack.mallocInt(1);
            int token = VR_InitInternal(error, EVRApplicationType_VRApplication_Scene);
            if (error.get(0) != EVRInitError_VRInitError_None) {
                throw new RuntimeException(
                        VR.VR_GetVRInitErrorAsEnglishDescription(error.get(0))
                );
            }
            vrCore.logInfo("Successfully initialized VR app...");

            OpenVR.create(token);


            if (OpenVR.VRSystem == null || OpenVR.VRCompositor == null) {
                throw new RuntimeException(
                        "VRSystem and VRCompositor not found!"
                );
            }
            vrCore.logInfo("Successfully initialized VR system...");

            VRCompositor_SetTrackingSpace(
                    ETrackingUniverseOrigin_TrackingUniverseStanding
            );
            vrCore.logInfo("Successfully initialized VR Compositor...");


            if(getVrCore().getInputHandler() != null) {
                getVrCore().getInputHandler().init();
            }
            vrCore.logInfo("Successfully initialized VR Input handler...");

            vrCore.getDevicesManager().update();

            vrRenderer.init();

            initialized = true;

        }
    }

    @Override
    public void preTick() {
        if(!initialized) return;
        setPaused(VRSystem_ShouldApplicationPause());
        getVrCore().getDevicesManager().update();
        updateEvents();
    }

    @Override
    public void tick() {
        if(!initialized) return;
        vrRenderer.updateFrame();
    }

    @Override
    public void postTick() {
        getVrCore().getOverlaysManager().update();
    }
    private void updateEvents() {
        vrEventsTick.clear();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for (VREvent vrevent = VREvent.malloc(stack);
                 VRSystem_PollNextEvent(vrevent, VREvent.SIZEOF);
                 vrevent = VREvent.malloc(stack)) {
                this.vrEventsTick.add(vrevent);
            }
        }
    }

    @Override
    public void destroy() {
        VR.VR_ShutdownInternal();
        vrRenderer.destroy();
        initialized = false;
    }


}
