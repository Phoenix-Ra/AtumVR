package me.phoenixra.atumvr.core.openxr;

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
import me.phoenixra.atumvr.api.provider.openxr.oscompat.OSCompatibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class OpenXRProvider implements VRProvider {

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
    private OpenXRRenderer vrRenderer;

    @Getter
    private boolean initialized = false;
    @Getter
    private boolean paused = false;
    private boolean active = false;
    public long time;



    @Getter
    private OSCompatibility osCompatibility;

    public XrInstance instance;
    public XrSession session;
    public XrSpace xrAppSpace;
    public XrSpace xrViewSpace;
    public XrSwapchain swapchain;
    public final XrEventDataBuffer eventDataBuffer = XrEventDataBuffer.calloc();

    private long systemId;
    public String systemName;

    public XrView.Buffer viewBuffer;
    @Getter
    private Pair<Integer, Integer> eyeResolution;



    public OpenXRProvider(){
        this.type = VRProviderType.OPEN_VR;
    }

    @Override
    public void initializeVR(@NotNull VRApp vrApp) {
        if (initialized) {
            throw new VRException("Already initialized!");
        }
        this.osCompatibility = OSCompatibility.detectDevice();
        this.attachedApp = vrApp;
        this.configManager = createConfigManager();
        this.inputHandler = createVRInputHandler();
        this.devicesManager = createDevicesManager();
        this.vrRenderer = createVRRenderer(vrApp);
        vrRenderer.setupGLContext();

        initOpenXRInstance();
        initOpenXRSession();
        initOpenXRSpace();
        initOpenXRSwapChain();
        initInputAndApplication();
        try {
            vrRenderer.init();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        initialized = true;
    }


    private void initOpenXRInstance() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            osCompatibility.initOpenXRLoader(stack);

            // Check extensions
            IntBuffer numExtensions = stack.callocInt(1);
            int error = XR10.xrEnumerateInstanceExtensionProperties((ByteBuffer) null, numExtensions, null);
            logError(error, "xrEnumerateInstanceExtensionProperties", "get count");

            XrExtensionProperties.Buffer properties = new XrExtensionProperties.Buffer(
                    bufferStack(numExtensions.get(0), XrExtensionProperties.SIZEOF, XR10.XR_TYPE_EXTENSION_PROPERTIES)
            );

            // Load extensions
            error = XR10.xrEnumerateInstanceExtensionProperties((ByteBuffer) null, numExtensions, properties);
            logError(error, "xrEnumerateInstanceExtensionProperties", "get extensions");

            // get needed extensions
            String graphicsExtension = osCompatibility.getGraphicsExtension();
            boolean missingGraphics = true;
            PointerBuffer extensions = stack.callocPointer(5);
            while (properties.hasRemaining()) {
                XrExtensionProperties prop = properties.get();
                String extensionName = prop.extensionNameString();
                if (extensionName.equals(graphicsExtension)) {
                    missingGraphics = false;
                    extensions.put(memAddress(stackUTF8(graphicsExtension)));
                }
                if (extensionName.equals(
                        EXTHPMixedRealityController.XR_EXT_HP_MIXED_REALITY_CONTROLLER_EXTENSION_NAME))
                {
                    extensions.put(memAddress(
                            stackUTF8(EXTHPMixedRealityController.XR_EXT_HP_MIXED_REALITY_CONTROLLER_EXTENSION_NAME)));
                }
                if (extensionName.equals(
                        HTCViveCosmosControllerInteraction.XR_HTC_VIVE_COSMOS_CONTROLLER_INTERACTION_EXTENSION_NAME))
                {
                    extensions.put(memAddress(stackUTF8(
                            HTCViveCosmosControllerInteraction.XR_HTC_VIVE_COSMOS_CONTROLLER_INTERACTION_EXTENSION_NAME)));
                }
                if (extensionName.equals(
                        BDControllerInteraction.XR_BD_CONTROLLER_INTERACTION_EXTENSION_NAME))
                {
                    extensions.put(memAddress(stackUTF8(
                            BDControllerInteraction.XR_BD_CONTROLLER_INTERACTION_EXTENSION_NAME)));
                }
                if (extensionName.equals(
                        FBDisplayRefreshRate.XR_FB_DISPLAY_REFRESH_RATE_EXTENSION_NAME))
                {
                    extensions.put(memAddress(stackUTF8(
                            FBDisplayRefreshRate.XR_FB_DISPLAY_REFRESH_RATE_EXTENSION_NAME)));
                }
            }

            if (missingGraphics) {
                throw new RuntimeException("OpenXR runtime is missing a supported graphics extension.");
            }

            // Create APP info
            XrApplicationInfo applicationInfo = XrApplicationInfo.calloc(stack);
            applicationInfo.apiVersion(XR10.XR_MAKE_VERSION(1, 0, 40));
            applicationInfo.applicationName(stack.UTF8("AtumVRExample"));
            applicationInfo.applicationVersion(1);

            // Create instance info
            XrInstanceCreateInfo createInfo = XrInstanceCreateInfo.calloc(stack);
            createInfo.type(XR10.XR_TYPE_INSTANCE_CREATE_INFO);
            createInfo.next(osCompatibility.getPlatformInfo(stack));
            createInfo.createFlags(0);
            createInfo.applicationInfo(applicationInfo);
            createInfo.enabledApiLayerNames(null);
            createInfo.enabledExtensionNames(extensions.flip());

            // Create XR instance
            PointerBuffer instancePtr = stack.callocPointer(1);
            int xrResult = XR10.xrCreateInstance(createInfo, instancePtr);
            if (xrResult == XR10.XR_ERROR_RUNTIME_FAILURE) {
                throw new VRException("Failed to create xrInstance, are you sure your headset is plugged in?");
            } else if (xrResult == XR10.XR_ERROR_INSTANCE_LOST) {
                throw new VRException("Failed to create xrInstance due to runtime updating");
            } else if (xrResult < 0) {
                throw new VRException("XR method returned " + xrResult);
            }
            this.instance = new XrInstance(instancePtr.get(0), createInfo);

        }
    }

    private void initOpenXRSession() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create system
            XrSystemGetInfo system = XrSystemGetInfo.calloc(stack);
            system.type(XR10.XR_TYPE_SYSTEM_GET_INFO);
            system.next(NULL);
            system.formFactor(XR10.XR_FORM_FACTOR_HEAD_MOUNTED_DISPLAY);

            LongBuffer longBuffer = stack.callocLong(1);
            int error = XR10.xrGetSystem(this.instance, system, longBuffer);
            logError(error, "xrGetSystem", "");
            this.systemId = longBuffer.get(0);

            if (this.systemId == 0) {
                throw new RuntimeException("No compatible headset detected");
            }

            XrSystemProperties systemProperties = XrSystemProperties.calloc(stack).type(XR10.XR_TYPE_SYSTEM_PROPERTIES);
            error = XR10.xrGetSystemProperties(this.instance, this.systemId, systemProperties);
            logError(error, "xrGetSystemProperties", "");
            XrSystemTrackingProperties trackingProperties = systemProperties.trackingProperties();
            XrSystemGraphicsProperties graphicsProperties = systemProperties.graphicsProperties();

            systemName = memUTF8(memAddress(systemProperties.systemName()));
            int vendor = systemProperties.vendorId();
            boolean orientationTracking = trackingProperties.orientationTracking();
            boolean positionTracking = trackingProperties.positionTracking();
            int maxWidth = graphicsProperties.maxSwapchainImageWidth();
            int maxHeight = graphicsProperties.maxSwapchainImageHeight();
            int maxLayerCount = graphicsProperties.maxLayerCount();

            logInfo("Found device with id: "+ this.systemId);
            logInfo(String.format("Headset Name: %s, Vendor: %s", systemName, vendor));
            logInfo(String.format("Headset Orientation Tracking: %s, Position Tracking: %s", orientationTracking,
                    positionTracking));
            logInfo(String.format("Headset Max Width: %s, Max Height: %s, Max Layer Count: %s", maxWidth, maxHeight,
                    maxLayerCount));

            // Create session
            XrSessionCreateInfo info = XrSessionCreateInfo.calloc(stack);
            info.type(XR10.XR_TYPE_SESSION_CREATE_INFO);
            info.next(osCompatibility.checkGraphics(stack, this.instance, this.systemId, vrRenderer.getWindowHandle()).address());
            info.createFlags(0);
            info.systemId(this.systemId);

            PointerBuffer sessionPtr = stack.callocPointer(1);
            error = XR10.xrCreateSession(this.instance, info, sessionPtr);
            logError(error, "xrCreateSession", "");

            this.session = new XrSession(sessionPtr.get(0), this.instance);

            while (!active) {
                logInfo("Vivecraft: waiting for OpenXR session to start");
                pollVREvents();
            }
        }
    }

    private void initOpenXRSpace() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrPosef identityPose = XrPosef.calloc(stack);
            identityPose.set(
                    XrQuaternionf.calloc(stack).set(0, 0, 0, 1),
                    XrVector3f.calloc(stack)
            );

            XrReferenceSpaceCreateInfo referenceSpaceCreateInfo = XrReferenceSpaceCreateInfo.calloc(stack);
            referenceSpaceCreateInfo.type(XR10.XR_TYPE_REFERENCE_SPACE_CREATE_INFO);
            referenceSpaceCreateInfo.next(NULL);
            referenceSpaceCreateInfo.referenceSpaceType(XR10.XR_REFERENCE_SPACE_TYPE_STAGE);
            referenceSpaceCreateInfo.poseInReferenceSpace(identityPose);

            PointerBuffer pp = stack.callocPointer(1);
            int error = XR10.xrCreateReferenceSpace(this.session, referenceSpaceCreateInfo, pp);
            this.xrAppSpace = new XrSpace(pp.get(0), this.session);
            logError(error, "xrCreateReferenceSpace", "XR_REFERENCE_SPACE_TYPE_STAGE");

            referenceSpaceCreateInfo.referenceSpaceType(XR10.XR_REFERENCE_SPACE_TYPE_VIEW);
            error = XR10.xrCreateReferenceSpace(this.session, referenceSpaceCreateInfo, pp);
            logError(error, "xrCreateReferenceSpace", "XR_REFERENCE_SPACE_TYPE_VIEW");
            this.xrViewSpace = new XrSpace(pp.get(0), this.session);
        }
    }

    private void initOpenXRSwapChain() {
        try (MemoryStack stack = stackPush()) {
            // Check amount of views
            IntBuffer intBuf = stack.callocInt(1);
            int error = XR10.xrEnumerateViewConfigurationViews(this.instance, this.systemId,
                    XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO, intBuf, null);
            logError(error, "xrEnumerateViewConfigurationViews", "get count");

            // Get all views
            ByteBuffer viewConfBuffer = bufferStack(intBuf.get(0), XrViewConfigurationView.SIZEOF,
                    XR10.XR_TYPE_VIEW_CONFIGURATION_VIEW);
            XrViewConfigurationView.Buffer views = new XrViewConfigurationView.Buffer(viewConfBuffer);
            error = XR10.xrEnumerateViewConfigurationViews(this.instance, this.systemId,
                    XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO, intBuf, views);
            logError(error, "xrEnumerateViewConfigurationViews", "get views");
            int viewCountNumber = intBuf.get(0);

            this.viewBuffer = new XrView.Buffer(
                    bufferHeap(viewCountNumber, XrView.SIZEOF, XR10.XR_TYPE_VIEW)
            );
            // Check swapchain formats
            error = XR10.xrEnumerateSwapchainFormats(this.session, intBuf, null);
            logError(error, "xrEnumerateSwapchainFormats", "get count");

            // Get swapchain formats
            LongBuffer swapchainFormats = stack.callocLong(intBuf.get(0));
            error = XR10.xrEnumerateSwapchainFormats(this.session, intBuf, swapchainFormats);
            logError(error, "xrEnumerateSwapchainFormats", "get formats");

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
            error = XR10.xrCreateSwapchain(this.session, swapchainCreateInfo, handlePointer);
            logError(error, "xrCreateSwapchain", "format: " + chosenFormat);
            this.swapchain = new XrSwapchain(handlePointer.get(0), this.session);
            eyeResolution = new Pair<>(swapchainCreateInfo.width(), swapchainCreateInfo.height());
        }
    }
    private void initInputAndApplication() {


        this.initDisplayRefreshRate();
    }
    private void initDisplayRefreshRate() {
        if (this.session.getCapabilities().XR_FB_display_refresh_rate) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer refreshRateCount = stack.callocInt(1);
                FBDisplayRefreshRate.xrEnumerateDisplayRefreshRatesFB(this.session, refreshRateCount, null);
                FloatBuffer refreshRateBuffer = stack.callocFloat(refreshRateCount.get(0));
                FBDisplayRefreshRate.xrEnumerateDisplayRefreshRatesFB(this.session, refreshRateCount, refreshRateBuffer);
                refreshRateBuffer.rewind();
                FBDisplayRefreshRate.xrRequestDisplayRefreshRateFB(this.session, refreshRateBuffer.get(refreshRateCount.get(0) -1));
            }
        }
    }

    private void pollVREvents() {
        while (true) {
            this.eventDataBuffer.clear();
            this.eventDataBuffer.type(XR10.XR_TYPE_EVENT_DATA_BUFFER);
            int error = XR10.xrPollEvent(this.instance, this.eventDataBuffer);
            logError(error, "xrPollEvent", "");
            if (error != XR10.XR_SUCCESS) {
                break;
            }
            XrEventDataBaseHeader event = XrEventDataBaseHeader.create(this.eventDataBuffer.address());

            switch (event.type()) {
                case XR10.XR_TYPE_EVENT_DATA_INSTANCE_LOSS_PENDING -> {
                    XrEventDataInstanceLossPending instanceLossPending = XrEventDataInstanceLossPending.create(
                            event.address());
                }
                case XR10.XR_TYPE_EVENT_DATA_SESSION_STATE_CHANGED -> {
                    this.sessionChanged(XrEventDataSessionStateChanged.create(event.address()));
                }
                case XR10.XR_TYPE_EVENT_DATA_INTERACTION_PROFILE_CHANGED -> {
                }
                case XR10.XR_TYPE_EVENT_DATA_REFERENCE_SPACE_CHANGE_PENDING -> {
                }
                default -> {
                }
            }
        }
    }
    private void sessionChanged(XrEventDataSessionStateChanged xrEventDataSessionStateChanged) {
        int state = xrEventDataSessionStateChanged.state();

        switch (state) {
            case XR10.XR_SESSION_STATE_READY: {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    XrSessionBeginInfo sessionBeginInfo = XrSessionBeginInfo.calloc(stack);
                    sessionBeginInfo.type(XR10.XR_TYPE_SESSION_BEGIN_INFO);
                    sessionBeginInfo.next(NULL);
                    sessionBeginInfo.primaryViewConfigurationType(XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO);

                    int error = XR10.xrBeginSession(this.session, sessionBeginInfo);
                    logError(error, "xrBeginSession", "XR_SESSION_STATE_READY");
                }
                this.active = true;
                break;
            }
            case XR10.XR_SESSION_STATE_STOPPING: {

                int error = XR10.xrEndSession(this.session);
                logError(error, "xrEndSession", "XR_SESSION_STATE_STOPPING");

                destroy();
            }
            case XR10.XR_SESSION_STATE_VISIBLE, XR10.XR_SESSION_STATE_FOCUSED: {
                active = true;
                break;
            }
            case XR10.XR_SESSION_STATE_EXITING, XR10.XR_SESSION_STATE_IDLE, XR10.XR_SESSION_STATE_SYNCHRONIZED: {
                active = false;
                break;
            }
            case XR10.XR_SESSION_STATE_LOSS_PENDING: {
                break;
            }
            default:
                break;
        }
    }


    @Override
    public void onPreRender(float partialTick) {
        pollVREvents();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrFrameState frameState = XrFrameState.calloc(stack).type(XR10.XR_TYPE_FRAME_STATE);

            int error = XR10.xrWaitFrame(
                    session,
                    XrFrameWaitInfo.calloc(stack).type(XR10.XR_TYPE_FRAME_WAIT_INFO),
                    frameState);
            logError(error, "xrWaitFrame", "");

            time = frameState.predictedDisplayTime();

            error = XR10.xrBeginFrame(
                    this.session,
                    XrFrameBeginInfo.calloc(stack).type(XR10.XR_TYPE_FRAME_BEGIN_INFO));
            logError(error, "xrBeginFrame", "");


            XrViewState viewState = XrViewState.calloc(stack).type(XR10.XR_TYPE_VIEW_STATE);
            IntBuffer intBuf = stack.callocInt(1);

            XrViewLocateInfo viewLocateInfo = XrViewLocateInfo.calloc(stack);
            viewLocateInfo.set(XR10.XR_TYPE_VIEW_LOCATE_INFO,
                    0,
                    XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO,
                    frameState.predictedDisplayTime(),
                    this.xrAppSpace
            );

            error = XR10.xrLocateViews(this.session, viewLocateInfo, viewState, intBuf, this.viewBuffer);
            logError(error, "xrLocateViews", "");


        }
    }

    @Override
    public void onRender(float partialTick) {
        vrRenderer.renderFrame();
    }

    @Override
    public void onPostRender(float partialTick) {

    }

    @Override
    public void destroy() {
        int error;

        if (this.swapchain != null) {
            error = XR10.xrDestroySwapchain(this.swapchain);
            logError(error, "xrDestroySwapchain", "");
        }
        if (this.viewBuffer != null) {
            this.viewBuffer.close();
        }
        if (this.xrAppSpace != null) {
            error = XR10.xrDestroySpace(this.xrAppSpace);
            logError(error, "xrDestroySpace", "xrAppSpace");
        }
        if (this.xrViewSpace != null) {
            error = XR10.xrDestroySpace(this.xrViewSpace);
            logError(error, "xrDestroySpace", "xrViewSpace");
        }
        if (this.session != null) {
            error = XR10.xrDestroySession(this.session);
            logError(error, "xrDestroySession", "");
        }
        if (this.instance != null) {
            error = XR10.xrDestroyInstance(this.instance);
            logError(error, "xrDestroyInstance", "");
        }
        this.eventDataBuffer.close();
    }

    void logError(int xrResult, String caller, String... args) {
        if (xrResult < 0) {
            logError(String.format(
                    "%s for %s errored: %s", caller, String.join(" ", args), getResultName(xrResult)
            ));
        }
    }
    private String getResultName(int xrResult) {
        String resultString = switch (xrResult) {
            case 1 -> "XR_TIMEOUT_EXPIRED";
            case 3 -> "XR_SESSION_LOSS_PENDING";
            case 4 -> "XR_EVENT_UNAVAILABLE";
            case 7 -> "XR_SPACE_BOUNDS_UNAVAILABLE";
            case 8 -> "XR_SESSION_NOT_FOCUSED";
            case 9 -> "XR_FRAME_DISCARDED";
            case -1 -> "XR_ERROR_VALIDATION_FAILURE";
            case -2 -> "XR_ERROR_RUNTIME_FAILURE";
            case -3 -> "XR_ERROR_OUT_OF_MEMORY";
            case -4 -> "XR_ERROR_API_VERSION_UNSUPPORTED";
            case -6 -> "XR_ERROR_INITIALIZATION_FAILED";
            case -7 -> "XR_ERROR_FUNCTION_UNSUPPORTED";
            case -8 -> "XR_ERROR_FEATURE_UNSUPPORTED";
            case -9 -> "XR_ERROR_EXTENSION_NOT_PRESENT";
            case -10 -> "XR_ERROR_LIMIT_REACHED";
            case -11 -> "XR_ERROR_SIZE_INSUFFICIENT";
            case -12 -> "XR_ERROR_HANDLE_INVALID";
            case -13 -> "XR_ERROR_INSTANCE_LOST";
            case -14 -> "XR_ERROR_SESSION_RUNNING";
            case -16 -> "XR_ERROR_SESSION_NOT_RUNNING";
            case -17 -> "XR_ERROR_SESSION_LOST";
            case -18 -> "XR_ERROR_SYSTEM_INVALID";
            case -19 -> "XR_ERROR_PATH_INVALID";
            case -20 -> "XR_ERROR_PATH_COUNT_EXCEEDED";
            case -21 -> "XR_ERROR_PATH_FORMAT_INVALID";
            case -22 -> "XR_ERROR_PATH_UNSUPPORTED";
            case -23 -> "XR_ERROR_LAYER_INVALID";
            case -24 -> "XR_ERROR_LAYER_LIMIT_EXCEEDED";
            case -25 -> "XR_ERROR_SWAPCHAIN_RECT_INVALID";
            case -26 -> "XR_ERROR_SWAPCHAIN_FORMAT_UNSUPPORTED";
            case -27 -> "XR_ERROR_ACTION_TYPE_MISMATCH";
            case -28 -> "XR_ERROR_SESSION_NOT_READY";
            case -29 -> "XR_ERROR_SESSION_NOT_STOPPING";
            case -30 -> "XR_ERROR_TIME_INVALID";
            case -31 -> "XR_ERROR_REFERENCE_SPACE_UNSUPPORTED";
            case -32 -> "XR_ERROR_FILE_ACCESS_ERROR";
            case -33 -> "XR_ERROR_FILE_CONTENTS_INVALID";
            case -34 -> "XR_ERROR_FORM_FACTOR_UNSUPPORTED";
            case -35 -> "XR_ERROR_FORM_FACTOR_UNAVAILABLE";
            case -36 -> "XR_ERROR_API_LAYER_NOT_PRESENT";
            case -37 -> "XR_ERROR_CALL_ORDER_INVALID";
            case -38 -> "XR_ERROR_GRAPHICS_DEVICE_INVALID";
            case -39 -> "XR_ERROR_POSE_INVALID";
            case -40 -> "XR_ERROR_INDEX_OUT_OF_RANGE";
            case -41 -> "XR_ERROR_VIEW_CONFIGURATION_TYPE_UNSUPPORTED";
            case -42 -> "XR_ERROR_ENVIRONMENT_BLEND_MODE_UNSUPPORTED";
            case -44 -> "XR_ERROR_NAME_DUPLICATED";
            case -45 -> "XR_ERROR_NAME_INVALID";
            case -46 -> "XR_ERROR_ACTIONSET_NOT_ATTACHED";
            case -47 -> "XR_ERROR_ACTIONSETS_ALREADY_ATTACHED";
            case -48 -> "XR_ERROR_LOCALIZED_NAME_DUPLICATED";
            case -49 -> "XR_ERROR_LOCALIZED_NAME_INVALID";
            case -50 -> "XR_ERROR_GRAPHICS_REQUIREMENTS_CALL_MISSING";
            case -51 -> "XR_ERROR_RUNTIME_UNAVAILABLE";
            default -> null;
        };
        if (resultString == null) {
            // ask the runtime for the xrResult name
            try (MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer str = stack.calloc(XR10.XR_MAX_RESULT_STRING_SIZE);

                if (XR10.xrResultToString(this.instance, xrResult, str) == XR10.XR_SUCCESS) {
                    resultString = (memUTF8(memAddress(str)));
                } else {
                    resultString = "Unknown Error: " + xrResult;
                }
            }
        }
        return resultString;
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
    @Override
    public ConfigManager createConfigManager() {
        return new AtumConfigManager(this);
    }

    @Override
    public VRDevicesManager createDevicesManager() {
        return null;
    }

    @Override
    public @Nullable VRInputHandler createVRInputHandler() {
        return null;
    }

    @Override
    public @NotNull OpenXRRenderer createVRRenderer(@NotNull VRApp vrApp) {
        return null;
    }
}
