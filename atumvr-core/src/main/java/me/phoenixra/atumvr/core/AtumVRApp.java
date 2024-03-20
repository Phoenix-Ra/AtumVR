package me.phoenixra.atumvr.core;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.exceptions.VRInputException;
import me.phoenixra.atumvr.api.input.InputAnalogData;
import me.phoenixra.atumvr.api.input.InputDigitalData;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.api.utils.VRUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;
import java.io.File;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_SetTrackingSpace;
import static org.lwjgl.openvr.VRInput.*;
import static org.lwjgl.openvr.VRSystem.VRSystem_ShouldApplicationPause;

public abstract class AtumVRApp implements VRApp {
    @Getter
    private VRCore vrCore;
    @Getter
    private String appKey = "empty";

    @Getter
    private VRRenderer vrRenderer;

    private File actionManifest;

    @Getter @Setter
    private boolean paused;
    @Getter
    private boolean initialized = false;
    public AtumVRApp(VRCore vrCore, @Nullable File actionManifest){
        this.vrCore = vrCore;
        this.vrRenderer = createVRRenderer(this);
        this.actionManifest = actionManifest;
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

            loadActionManifest();
            /*LoadableConfig vrAppManifest = vrCore.getConfigManager().createLoadableConfig(
                    "vrApp_manifest",
                    "",
                    ConfigType.JSON,
                    true
            );
            appKey = vrAppManifest.getString("app_key");
            System.out.println("Appkey: " + appKey);

            if (VRApplications_IsApplicationInstalled(appKey)) {
                System.out.println("Application manifest already installed");
            } else {
                int appError = VRApplications_AddApplicationManifest(vrAppManifest.getFile().getAbsolutePath(), true);
                if (appError != EVRApplicationError_VRApplicationError_None) {
                    throw new RuntimeException("Failed to install application manifest: " +
                            VRApplications_GetApplicationsErrorNameFromEnum(appError)+"\nFile path:"+
                            vrAppManifest.getFile().getAbsolutePath()
                    );

                }

                System.out.println("Application manifest installed successfully");
            }

            int processId;

            try {
                String s1 = ManagementFactory.getRuntimeMXBean().getName();
                processId = Integer.parseInt(s1.split("@")[0]);
            } catch (Exception exception) {
                System.out.println("Error getting process id");
                exception.printStackTrace();
                return;
            }

            int k = VRApplications_IdentifyApplication(processId, appKey);

            if (k != 0) {
                System.out.println("Failed to identify application: " + VRApplications_GetApplicationsErrorNameFromEnum(k));
            } else {
                System.out.println("Application identified successfully");
            }*/
            vrCore.getDevicesManager().update();

            vrRenderer.init();

            initialized = true;

        }
    }

    protected void loadActionManifest() {
        if(actionManifest == null) {
            return;
        }
        getVrCore().logInfo("[LOADING] Action manifest");
        int error = VRInput_SetActionManifestPath(
                actionManifest.getAbsolutePath()
        );
        if (error != 0) {
            throw new VRInputException(
                    "Error while loading action manifest", error
            );
        }
        getVrCore().logInfo("[SUCCESS] Action manifest");

    }

    @Override
    public void onPreTick() {
        if(!initialized) return;
        setPaused(VRSystem_ShouldApplicationPause());
        getVrCore().getDevicesManager().update();
    }

    @Override
    public void onTick() {
        if(!initialized) return;
        vrRenderer.updateFrame();
    }

    @Override
    public void onPostTick() {
        getVrCore().getOverlaysManager().update();
    }

    @Override
    public final long getInputActionHandle(@NotNull String actionPath,
                                           @NotNull MemoryStack stack) throws VRInputException{

        LongBuffer longbyreference = stack.mallocLong(1);
        int error = VRInput.VRInput_GetActionHandle(actionPath, longbyreference);

        if (error != 0) {
            throw new VRInputException(
                    "Error getting action handle for '" + actionPath+"'",
                    error
            );
        }
        return longbyreference.get(0);
    }
    @Override
    public final long getInputActionSetHandle(@NotNull String actionSetPath,
                                              @NotNull MemoryStack stack) throws VRInputException{
        LongBuffer longbyreference = stack.mallocLong(1);
        int error = VRInput_GetActionSetHandle(
                actionSetPath,
                longbyreference
        );

        if (error != 0) {
            throw new VRInputException(
                    "Error getting action set handle for '"+actionSetPath+"'", error
            );
        }
        return longbyreference.get(0);
    }
    @Override
    public final long getInputSourceHandle(@NotNull String path,
                                           @NotNull MemoryStack stack) throws VRInputException{
        LongBuffer longbyreference = stack.mallocLong(1);
        int error = VRInput_GetInputSourceHandle(
                path,
                longbyreference
        );

        if (error != 0) {
            throw new VRInputException(
                    "Error getting input source handle for '"+path+"'", error
            );
        }
        return longbyreference.get(0);
    }

    @Override
    public List<Long> getOrigins(String actionSet, long handle, String nameForLog) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer longbyreference = stack.mallocLong(16);
            int i = VRInput_GetActionOrigins(
                    getInputActionSetHandle(actionSet,stack),
                    handle,
                    longbyreference
            );

            if (i != 0) {
                throw new RuntimeException("Error getting action origins for" +
                        " '" + nameForLog + "': " + VRUtils.getInputErrorMessage(i));
            } else {
                List<Long> list = new ArrayList<>();

                while (longbyreference.remaining() > 0) {
                    long j = longbyreference.get();
                    if (j != 0L) {
                        list.add(j);
                    }
                }

                return list;
            }
        }
    }
    @Override
    public InputDigitalData getDigitalData(long controllerHandle,
                                           long actionHandle,
                                           @NotNull String nameForLog) {

        try(MemoryStack stack =  MemoryStack.stackPush()) {
            InputDigitalActionData out = InputDigitalActionData.malloc(stack);
            int error = VRInput_GetDigitalActionData(actionHandle,
                    out,
                    InputDigitalActionData.SIZEOF,
                    controllerHandle
            );

            if (error != 0) {
                throw new RuntimeException("Error reading digital data for '"
                        + nameForLog + "': " + VRUtils.getInputErrorMessage(error));
            } else {
                return new InputDigitalData(
                        out.activeOrigin(),
                        out.bActive(),
                        out.bState(),
                        out.bChanged(),
                        out.fUpdateTime()
                );
            }
        }

    }

    @Override
    public InputAnalogData getAnalogData(long controllerHandle,
                                         long actionHandle,
                                         @NotNull String nameForLog) {
        try(MemoryStack stack =  MemoryStack.stackPush()) {
            InputAnalogActionData out = InputAnalogActionData.malloc(stack);
            int j = VRInput_GetAnalogActionData(
                    actionHandle,
                    out,
                    InputAnalogActionData.SIZEOF,
                    controllerHandle
            );

            if (j != 0) {
                throw new RuntimeException("Error reading analog data for '"
                        + nameForLog + "': " + VRUtils.getInputErrorMessage(j));
            } else {
                return new InputAnalogData(
                        out.activeOrigin(),
                        out.bActive(),
                        false,
                        out.fUpdateTime(),
                        out.x(),
                        out.y(),
                        out.z(),
                        out.deltaX(),
                        out.deltaY(),
                        out.deltaZ()
                );
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
