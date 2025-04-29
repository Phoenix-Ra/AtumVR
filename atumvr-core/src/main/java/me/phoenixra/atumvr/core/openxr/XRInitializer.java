package me.phoenixra.atumvr.core.openxr;

import lombok.Getter;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.core.openxr.oscompat.OSCompatibility;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.system.MemoryStack.stackCalloc;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class XRInitializer {
    private OpenXRProvider provider;

    @Getter
    protected OSCompatibility osCompatibility;

    @Getter
    protected XrInstance xrInstance;
    @Getter
    protected XrSession xrSession;
    @Getter
    protected XrSpace xrAppSpace;
    @Getter
    protected XrSpace xrViewSpace;
    @Getter
    protected XrSwapchain xrSwapchain;

    @Getter
    protected long xrSystemId;
    @Getter
    protected String xrSystemName;



    protected XrView.Buffer xrViewBuffer;

    protected final XrEventDataBuffer xrEventBuffer = XrEventDataBuffer.calloc();


    public XRInitializer(OpenXRProvider provider){
        this.provider = provider;
        this.osCompatibility = OSCompatibility.detectDevice();
    }

    public void init(){
        initOpenXRInstance();
        initOpenXRSession();
        initOpenXRSpace();
        initOpenXRSwapChain();
        initInputAndApplication();
    }

    private void initOpenXRInstance() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            // Initialize platform-specific XR loader
            osCompatibility.initOpenXRLoader(stack);

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
                    osCompatibility.getGraphicsExtension(),
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
                    .next(osCompatibility.getPlatformInfo(stack))
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

            this.xrInstance = new XrInstance(instPtr.get(0), instInfo);
        }
    }

    private void initOpenXRSession() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // 1) Acquire system ID for HMD
            XrSystemGetInfo sysGetInfo = XrSystemGetInfo.calloc(stack)
                    .type(XR10.XR_TYPE_SYSTEM_GET_INFO)
                    .next(NULL)
                    .formFactor(XR10.XR_FORM_FACTOR_HEAD_MOUNTED_DISPLAY);

            LongBuffer sysIdBuf = stack.callocLong(1);
            provider.checkXRError(
                    XR10.xrGetSystem(xrInstance, sysGetInfo, sysIdBuf),
                    "xrGetSystem", "fetch HMD system ID"
            );
            xrSystemId = sysIdBuf.get(0);
            if (xrSystemId == XR10.XR_NULL_SYSTEM_ID) {
                throw new VRException("No compatible HMD detected (system ID == 0)");
            }

            // 2) Query system properties
            XrSystemProperties sysProps = XrSystemProperties.calloc(stack)
                    .type(XR10.XR_TYPE_SYSTEM_PROPERTIES);
            provider.checkXRError(
                    XR10.xrGetSystemProperties(xrInstance, xrSystemId, sysProps),
                    "xrGetSystemProperties", "id=" + xrSystemId
            );

            // Log key properties
            String name = memUTF8(memAddress(sysProps.systemName()));
            XrSystemTrackingProperties track = sysProps.trackingProperties();
            XrSystemGraphicsProperties gfx = sysProps.graphicsProperties();
            provider.getAttachedApp().logInfo(String.format(
                    "Found HMD [%s] (vendor=%d): orientTrack=%b, posTrack=%b, maxRes=%dx%d, maxLayers=%d",
                    name,
                    sysProps.vendorId(),
                    track.orientationTracking(),
                    track.positionTracking(),
                    gfx.maxSwapchainImageWidth(),
                    gfx.maxSwapchainImageHeight(),
                    gfx.maxLayerCount()
            ));

            // 3) Create an XR session with proper GL binding
            Struct<?> graphicsBind = osCompatibility.checkGraphics(
                    stack, xrInstance, xrSystemId, provider.getVrRenderer().getWindowHandle()
            );
            XrSessionCreateInfo sessionInfo = XrSessionCreateInfo.calloc(stack)
                    .type(XR10.XR_TYPE_SESSION_CREATE_INFO)
                    .next(graphicsBind.address())
                    .systemId(xrSystemId)
                    .createFlags(0);

            PointerBuffer sessionBuf = stack.callocPointer(1);
            provider.checkXRError(
                    XR10.xrCreateSession(xrInstance, sessionInfo, sessionBuf),
                    "xrCreateSession", "creating session"
            );
            xrSession = new XrSession(sessionBuf.get(0), xrInstance);

            // 4) Wait until the session transitions to READY
            while (provider.isPaused()) {
                provider.getAttachedApp().logInfo("Waiting for OpenXR session READY state...");
                provider.pollVREvents();
            }
        }
    }

    private void initOpenXRSpace() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Identity pose for all reference spaces
            XrPosef identity = XrPosef.calloc(stack)
                    .set(
                            XrQuaternionf.calloc(stack).set(0, 0, 0, 1),
                            XrVector3f.calloc(stack).set(0f, 0f, 0f)
                    );

            xrAppSpace  = createReferenceSpace(stack, XR10.XR_REFERENCE_SPACE_TYPE_STAGE, identity);
            xrViewSpace = createReferenceSpace(stack, XR10.XR_REFERENCE_SPACE_TYPE_VIEW,  identity);
        }
    }

    private XrSpace createReferenceSpace(MemoryStack stack, int spaceType, XrPosef identityPose) {
        XrReferenceSpaceCreateInfo spaceInfo = XrReferenceSpaceCreateInfo.calloc(stack)
                .type(XR10.XR_TYPE_REFERENCE_SPACE_CREATE_INFO)
                .next(NULL)
                .referenceSpaceType(spaceType)
                .poseInReferenceSpace(identityPose);

        PointerBuffer pSpace = stack.callocPointer(1);
        provider.checkXRError(
                XR10.xrCreateReferenceSpace(xrSession, spaceInfo, pSpace),
                "xrCreateReferenceSpace", "Spacetype: "+spaceType
        );

        return new XrSpace(pSpace.get(0), xrSession);
    }


    private void initOpenXRSwapChain() {
        try (MemoryStack stack = stackPush()) {
            // Check amount of views
            IntBuffer intBuf = stack.callocInt(1);
            int error = XR10.xrEnumerateViewConfigurationViews(this.xrInstance, this.xrSystemId,
                    XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO, intBuf, null);
            provider.checkXRError(error, "xrEnumerateViewConfigurationViews", "get count");

            // Get all views
            ByteBuffer viewConfBuffer = bufferStack(intBuf.get(0), XrViewConfigurationView.SIZEOF,
                    XR10.XR_TYPE_VIEW_CONFIGURATION_VIEW);
            XrViewConfigurationView.Buffer views = new XrViewConfigurationView.Buffer(viewConfBuffer);
            error = XR10.xrEnumerateViewConfigurationViews(this.xrInstance, this.xrSystemId,
                    XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO, intBuf, views);
            provider.checkXRError(error, "xrEnumerateViewConfigurationViews", "get views");
            int viewCountNumber = intBuf.get(0);

            this.xrViewBuffer = new XrView.Buffer(
                    bufferHeap(viewCountNumber, XrView.SIZEOF, XR10.XR_TYPE_VIEW)
            );
            // Check swapchain formats
            error = XR10.xrEnumerateSwapchainFormats(this.xrSession, intBuf, null);
            provider.checkXRError(error, "xrEnumerateSwapchainFormats", "get count");

            // Get swapchain formats
            LongBuffer swapchainFormats = stack.callocLong(intBuf.get(0));
            error = XR10.xrEnumerateSwapchainFormats(this.xrSession, intBuf, swapchainFormats);
            provider.checkXRError(error, "xrEnumerateSwapchainFormats", "get formats");

            long[] desiredSwapchainFormats = {
                    // SRGB formats
                    GL21.GL_SRGB8_ALPHA8,
                    GL21.GL_SRGB8,
                    // others
                    GL11.GL_RGB10_A2,
                    GL30.GL_RGBA16F,
                    GL30.GL_RGB16F,

                    // The two below should only be used as a fallback, as they are linear color formats without enough bits for color
                    // depth, thus leading to banding.
                    GL11.GL_RGBA8,
                    GL31.GL_RGBA8_SNORM,
            };

            // Choose format
            long chosenFormat = 0;
            for (long glFormatIter : desiredSwapchainFormats) {
                swapchainFormats.rewind();
                while (swapchainFormats.hasRemaining()) {
                    if (glFormatIter == swapchainFormats.get()) {
                        chosenFormat = glFormatIter;
                        break;
                    }
                }
                if (chosenFormat != 0) {
                    break;
                }
            }

            if (chosenFormat == 0) {
                var formats = new ArrayList<Long>();
                swapchainFormats.rewind();
                while (swapchainFormats.hasRemaining()) {
                    formats.add(swapchainFormats.get());
                }
                throw new RuntimeException("No compatible swapchain / framebuffer format available: " + formats);
            }

            // Make swapchain
            XrViewConfigurationView viewConfig = views.get(0);
            XrSwapchainCreateInfo swapchainCreateInfo = XrSwapchainCreateInfo.calloc(stack);
            swapchainCreateInfo.type(XR10.XR_TYPE_SWAPCHAIN_CREATE_INFO);
            swapchainCreateInfo.next(NULL);
            swapchainCreateInfo.createFlags(0);
            swapchainCreateInfo.usageFlags(XR10.XR_SWAPCHAIN_USAGE_COLOR_ATTACHMENT_BIT);
            swapchainCreateInfo.format(chosenFormat);
            swapchainCreateInfo.sampleCount(1);
            swapchainCreateInfo.width(viewConfig.recommendedImageRectWidth());
            swapchainCreateInfo.height(viewConfig.recommendedImageRectHeight());
            swapchainCreateInfo.faceCount(1);
            swapchainCreateInfo.arraySize(2);
            swapchainCreateInfo.mipCount(1);

            PointerBuffer handlePointer = stack.callocPointer(1);
            error = XR10.xrCreateSwapchain(this.xrSession, swapchainCreateInfo, handlePointer);
            provider.checkXRError(error, "xrCreateSwapchain", "format: " + chosenFormat);
            this.xrSwapchain = new XrSwapchain(handlePointer.get(0), this.xrSession);
            provider.eyeTextureWidth = swapchainCreateInfo.width();
            provider.eyeTextureHeight = swapchainCreateInfo.height();
        }
    }


    private void initInputAndApplication() {


        this.initDisplayRefreshRate();
    }
    private void initDisplayRefreshRate() {
        if (this.xrSession.getCapabilities().XR_FB_display_refresh_rate) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer refreshRateCount = stack.callocInt(1);
                FBDisplayRefreshRate.xrEnumerateDisplayRefreshRatesFB(this.xrSession, refreshRateCount, null);
                FloatBuffer refreshRateBuffer = stack.callocFloat(refreshRateCount.get(0));
                FBDisplayRefreshRate.xrEnumerateDisplayRefreshRatesFB(this.xrSession, refreshRateCount, refreshRateBuffer);
                refreshRateBuffer.rewind();
                FBDisplayRefreshRate.xrRequestDisplayRefreshRateFB(this.xrSession, refreshRateBuffer.get(refreshRateCount.get(0) -1));
            }
        }
    }


    private ByteBuffer bufferStack(int capacity, int sizeof, int type) {
        ByteBuffer b = stackCalloc(capacity * sizeof);

        for (int i = 0; i < capacity; i++) {
            b.position(i * sizeof);
            b.putInt(type);
        }
        b.rewind();
        return b;
    }
    private ByteBuffer bufferHeap(int capacity, int sizeof, int type) {
        ByteBuffer b = memCalloc(capacity * sizeof);

        for (int i = 0; i < capacity; i++) {
            b.position(i * sizeof);
            b.putInt(type);
        }
        b.rewind();
        return b;
    }

    public void destroy(){
        int error;

        if (this.xrSwapchain != null) {
            error = XR10.xrDestroySwapchain(this.xrSwapchain);
            provider.checkXRError(error, "xrDestroySwapchain", "");
        }
        if (this.xrViewBuffer != null) {
            this.xrViewBuffer.close();
        }
        if (this.xrAppSpace != null) {
            error = XR10.xrDestroySpace(this.xrAppSpace);
            provider.checkXRError(error, "xrDestroySpace", "xrAppSpace");
        }
        if (this.xrViewSpace != null) {
            error = XR10.xrDestroySpace(this.xrViewSpace);
            provider.checkXRError(error, "xrDestroySpace", "xrViewSpace");
        }
        if (this.xrSession != null) {
            error = XR10.xrDestroySession(this.xrSession);
            provider.checkXRError(error, "xrDestroySession", "");
        }
        if (this.xrInstance != null) {
            error = XR10.xrDestroyInstance(this.xrInstance);
            provider.checkXRError(error, "xrDestroyInstance", "");
        }
        this.xrEventBuffer.close();
    }
}
