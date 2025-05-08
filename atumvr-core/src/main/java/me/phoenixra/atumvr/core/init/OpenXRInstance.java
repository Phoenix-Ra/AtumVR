package me.phoenixra.atumvr.core.init;

import lombok.Getter;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.OpenXRState;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpenXRInstance {
    private final OpenXRState xrState;
    @Getter
    protected XrInstance handle;
    @Getter
    protected final XrEventDataBuffer xrEventBuffer;

    private XrDebugUtilsMessengerEXT debugMessenger;

    public OpenXRInstance(OpenXRState xrState){
        this.xrState = xrState;
        xrEventBuffer = XrEventDataBuffer.calloc();
    }

    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {


            OpenXRProvider provider = this.xrState.getVrProvider();
            // Initialize platform-specific XR loader
            this.xrState.getOsCompatibility().initOpenXRLoader(stack);

            // 1) Enumerate available instance extensions
            IntBuffer extCountBuf = stack.callocInt(1);
            provider.checkXRError(
                    XR10.xrEnumerateInstanceExtensionProperties((ByteBuffer)null, extCountBuf, null),
                    "xrEnumerateInstanceExtensionProperties", "count"
            );

            int extCount = extCountBuf.get(0);
            XrExtensionProperties.Buffer extProps = XrExtensionProperties
                    .calloc(extCount, stack);
            extProps.forEach(prop -> prop.type(XR10.XR_TYPE_EXTENSION_PROPERTIES));

            provider.checkXRError(
                    XR10.xrEnumerateInstanceExtensionProperties((ByteBuffer)null, extCountBuf, extProps),
                    "xrEnumerateInstanceExtensionProperties", "properties"
            );

            // Collect supported extension names
            Set<String> availableExtensions = new HashSet<>(extCount);
            for (XrExtensionProperties prop : extProps) {
                availableExtensions.add(prop.extensionNameString());
            }

            // 2) Define desired extensions in priority order
            List<String> desiredExtensions = List.of(
                    this.xrState.getOsCompatibility().getGraphicsExtension(),
                    EXTDebugUtils.XR_EXT_DEBUG_UTILS_EXTENSION_NAME,
                    EXTHPMixedRealityController.XR_EXT_HP_MIXED_REALITY_CONTROLLER_EXTENSION_NAME,
                    HTCViveCosmosControllerInteraction.XR_HTC_VIVE_COSMOS_CONTROLLER_INTERACTION_EXTENSION_NAME,
                    BDControllerInteraction.XR_BD_CONTROLLER_INTERACTION_EXTENSION_NAME,
                    FBDisplayRefreshRate.XR_FB_DISPLAY_REFRESH_RATE_EXTENSION_NAME,
                    KHRVisibilityMask.XR_KHR_VISIBILITY_MASK_EXTENSION_NAME //@TODO test mask for performance improvements
            );

            // Ensure graphics extension is present
            String graphicsExt = desiredExtensions.get(0);
            if (!availableExtensions.contains(graphicsExt)) {
                throw new VRException("Missing required graphics extension: " + graphicsExt);
            }

            // Build PointerBuffer of only the extensions actually supported
            PointerBuffer enabledExtBuf = stack.mallocPointer(desiredExtensions.size());
            for (String extName : desiredExtensions) {
                if (availableExtensions.contains(extName)) {
                    enabledExtBuf.put(stack.UTF8(extName));
                }
            }
            enabledExtBuf.flip();

            // 3) Fill XrApplicationInfo
            XrApplicationInfo appInfo = XrApplicationInfo.calloc(stack)
                    .applicationName(stack.UTF8("AtumVRExample"))
                    .applicationVersion(1)
                    .engineName(stack.UTF8("AtumEngine"))
                    .engineVersion(1)
                    .apiVersion(XR10.XR_MAKE_VERSION(1, 0, 40));

            // 4) Create XrInstanceCreateInfo
            XrInstanceCreateInfo instInfo = XrInstanceCreateInfo.calloc(stack)
                    .type(XR10.XR_TYPE_INSTANCE_CREATE_INFO)
                    .next(0)
                    .applicationInfo(appInfo)
                    .enabledExtensionNames(enabledExtBuf)
                    .enabledApiLayerNames(null);

            // 5) Create the instance and handle errors
            PointerBuffer instPtr = stack.callocPointer(1);
            int result = XR10.xrCreateInstance(instInfo, instPtr);
            if (result == XR10.XR_ERROR_RUNTIME_FAILURE) {
                throw new VRException("Failed to create XrInstance: runtime failure (is headset connected?)");
            } else if (result == XR10.XR_ERROR_INSTANCE_LOST) {
                throw new VRException("Failed to create XrInstance: instance lost during creation");
            } else if (result != XR10.XR_SUCCESS) {
                throw new VRException("xrCreateInstance returned: " + provider.getXRActionResult(result));
            }

            this.handle = new XrInstance(instPtr.get(0), instInfo);

            if(handle.getCapabilities().XR_EXT_debug_utils) {
                setupDebugMessenger();
            }
        }
    }
    private void setupDebugMessenger() {
        try ( MemoryStack stack = MemoryStack.stackPush() ) {
            XrDebugUtilsMessengerCreateInfoEXT createInfo = XrDebugUtilsMessengerCreateInfoEXT
                    .calloc(stack)
                    .type$Default()  // XR_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT :contentReference[oaicite:0]{index=0}
                    .messageSeverities(
                            EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT
                                    | EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT
                                    | EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT
                                    | EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT
                    )
                    // catch every message type:
                    .messageTypes(
                            EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT
                                    | EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT
                                    | EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT
                                    | EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_TYPE_CONFORMANCE_BIT_EXT
                    );


            XrDebugUtilsMessengerCallbackEXTI debugCallback = (messageSeverity, messageTypes, pCallbackData, pUserData) -> {

                XrDebugUtilsMessengerCallbackDataEXT data =
                        XrDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);

                xrState.getVrProvider().getLogger().logDebug(
                        String.format(
                                "[OpenXR][%s] %s%n",
                                data.functionNameString(),
                                data.messageString()
                        )
                );
                return XR10.XR_FALSE; // don't abort the call that triggered this
            };

            createInfo
                    .userCallback(debugCallback)
                    .userData(0);

            PointerBuffer pMessenger = stack.mallocPointer(1);
            int err = EXTDebugUtils.xrCreateDebugUtilsMessengerEXT(
                    handle, createInfo, pMessenger
            );
            xrState.getVrProvider().checkXRError(
                    err, "xrCreateDebugUtilsMessengerEXT", ""
            );
            debugMessenger = new XrDebugUtilsMessengerEXT(pMessenger.get(0), handle);
        }
    }
    public void destroy(){
        if (debugMessenger != null) {
            EXTDebugUtils.xrDestroyDebugUtilsMessengerEXT(debugMessenger);
        }
        if (handle != null) {
            int error = XR10.xrDestroyInstance(handle);
            xrState.getVrProvider().checkXRError(error, "xrDestroyInstance", "");
        }
        xrEventBuffer.close();
    }
}
