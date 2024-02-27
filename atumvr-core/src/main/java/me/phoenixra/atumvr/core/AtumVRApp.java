package me.phoenixra.atumvr.core;

import lombok.Getter;
import me.phoenixra.atumconfig.api.config.ConfigType;
import me.phoenixra.atumconfig.api.config.LoadableConfig;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.scene.VRSceneRenderer;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.system.MemoryStack;

import java.lang.management.ManagementFactory;
import java.nio.IntBuffer;

import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VRApplications.*;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_SetTrackingSpace;

public class AtumVRApp implements VRApp {
    @Getter
    private VRCore vrCore;
    @Getter
    private String appKey = "empty";

    @Getter
    private VRSceneRenderer sceneRenderer;

    @Getter
    private boolean initialized = false;
    public AtumVRApp(VRCore vrCore){
        this.vrCore = vrCore;
        this.sceneRenderer = vrCore.createSceneRenderer(this);
    }
    @Override
    public void init() {
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


            LoadableConfig vrAppManifest = vrCore.getConfigManager().createLoadableConfig(
                    "vrApp_manifest",
                    "",
                    ConfigType.JSON,
                    true
            );
            appKey = vrAppManifest.getString("app_key");
            /*System.out.println("Appkey: " + appKey);

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

            sceneRenderer.init();

            initialized = true;

        }
    }

    @Override
    public void update() {
        sceneRenderer.updateFrame();
    }

    @Override
    public void destroy() {
        VR.VR_ShutdownInternal();
        sceneRenderer.destroy();
    }


}
