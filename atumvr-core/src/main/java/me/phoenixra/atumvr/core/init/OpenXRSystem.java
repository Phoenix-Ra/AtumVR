package me.phoenixra.atumvr.core.init;

import lombok.Getter;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.OpenXRState;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.*;

public class OpenXRSystem {
    private final OpenXRState xrState;

    @Getter
    private long systemId;

    public OpenXRSystem(OpenXRState xrState){
        this.xrState = xrState;

    }
    public void init(){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            OpenXRProvider provider = this.xrState.getVrProvider();
            // 1) Acquire system ID for HMD
            XrSystemGetInfo sysGetInfo = XrSystemGetInfo.calloc(stack)
                    .type(XR10.XR_TYPE_SYSTEM_GET_INFO)
                    .next(NULL)
                    .formFactor(XR10.XR_FORM_FACTOR_HEAD_MOUNTED_DISPLAY);

            LongBuffer sysIdBuf = stack.callocLong(1);
            provider.checkXRError(
                    XR10.xrGetSystem(
                            xrState.getXrInstance().getHandle(),
                            sysGetInfo,
                            sysIdBuf
                    ),
                    "xrGetSystem", "fetch HMD system ID"
            );

            systemId = sysIdBuf.get(0);
            if (systemId == XR10.XR_NULL_SYSTEM_ID) {
                throw new VRException("No compatible HMD detected (system ID == 0)");
            }

            // 2) Query system properties
            XrSystemProperties sysProps = XrSystemProperties.calloc(stack)
                    .type(XR10.XR_TYPE_SYSTEM_PROPERTIES);
            provider.checkXRError(
                    XR10.xrGetSystemProperties(
                            xrState.getXrInstance().getHandle(),
                            systemId,
                            sysProps
                    ),
                    "xrGetSystemProperties", "id=" + systemId
            );

            // Log key properties
            String name = memUTF8(memAddress(sysProps.systemName()));
            XrSystemTrackingProperties track = sysProps.trackingProperties();
            XrSystemGraphicsProperties gfx = sysProps.graphicsProperties();
            provider.getLogger().logInfo(String.format(
                    "Found HMD [%s] (vendor=%d): orientTrack=%b, posTrack=%b, maxRes=%dx%d, maxLayers=%d",
                    name,
                    sysProps.vendorId(),
                    track.orientationTracking(),
                    track.positionTracking(),
                    gfx.maxSwapchainImageWidth(),
                    gfx.maxSwapchainImageHeight(),
                    gfx.maxLayerCount()
            ));
        }
    }

    public void destroy(){

    }
}
