package me.phoenixra.atumvr.core.openxr;

import lombok.Getter;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumconfig.core.config.AtumConfigManager;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.VRProviderType;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.core.openxr.rendering.OpenXRRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class OpenXRProvider implements VRProvider {


    @Getter
    private VRApp attachedApp;


    @Getter
    private VRDevicesManager devicesManager;

    @Getter
    private VRInputHandler inputHandler;

    @Getter
    private VRRenderer vrRenderer;


    @Getter
    private XRInitializer xrInitializer;
    @Getter
    private final List<OpenXREvent> xrEventsReceived = new ArrayList<>();
    @Getter
    protected long xrDisplayTime;


    @Getter
    protected int eyeTextureWidth;
    @Getter
    protected int eyeTextureHeight;

    @Getter
    private boolean paused = true;
    @Getter
    private boolean initialized = false;




    @Override
    public void initializeVR(@NotNull VRApp vrApp) {
        if (initialized) {
            throw new VRException("Already initialized!");
        }
        this.xrInitializer = new XRInitializer(this);
        this.attachedApp = vrApp;
        this.inputHandler = createVRInputHandler();
        this.devicesManager = createDevicesManager();
        this.vrRenderer = createVRRenderer(vrApp);

        xrInitializer.init();

        try {
            vrRenderer.init();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        initialized = true;
    }

    @Override
    public void onPreRender(float partialTick) {
        pollVREvents();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrFrameState frameState = XrFrameState.calloc(stack).type(XR10.XR_TYPE_FRAME_STATE);

            checkXRError(
                    XR10.xrWaitFrame(
                            xrInitializer.xrSession,
                            XrFrameWaitInfo.calloc(stack)
                                    .type(XR10.XR_TYPE_FRAME_WAIT_INFO),
                            frameState
                    ),
                    "xrWaitFrame", ""
            );

            xrDisplayTime = frameState.predictedDisplayTime();

            checkXRError(
                    XR10.xrBeginFrame(
                            xrInitializer.xrSession,
                            XrFrameBeginInfo.calloc(stack)
                                    .type(XR10.XR_TYPE_FRAME_BEGIN_INFO)
                    ),
                    "xrBeginFrame", ""
            );


            XrViewState viewState = XrViewState.calloc(stack).type(XR10.XR_TYPE_VIEW_STATE);
            IntBuffer intBuf = stack.callocInt(1);

            XrViewLocateInfo viewLocateInfo = XrViewLocateInfo.calloc(stack);
            viewLocateInfo.set(
                    XR10.XR_TYPE_VIEW_LOCATE_INFO,
                    0,
                    XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO,
                    frameState.predictedDisplayTime(),
                    xrInitializer.xrAppSpace
            );

            checkXRError(
                    XR10.xrLocateViews(
                            xrInitializer.xrSession,
                            viewLocateInfo, viewState,
                            intBuf, xrInitializer.xrViewBuffer
                    ),
                    "xrLocateViews", ""
            );


        }
    }

    @Override
    public void onRender(float partialTick) {
        vrRenderer.renderFrame();
    }

    @Override
    public void onPostRender(float partialTick) {

    }



    protected void pollVREvents() {
        xrEventsReceived.clear();
        while (true) {
            xrInitializer.xrEventBuffer.clear();
            xrInitializer.xrEventBuffer.type(XR10.XR_TYPE_EVENT_DATA_BUFFER);
            int error = XR10.xrPollEvent(xrInitializer.xrInstance, xrInitializer.xrEventBuffer);
            checkXRError(error, "xrPollEvent", "");
            if (error != XR10.XR_SUCCESS) {
                break;
            }
            var event = XrEventDataBaseHeader.create(xrInitializer.xrEventBuffer.address());
            var vrEvent = OpenXREvent.fromId(event.type());
            if (vrEvent == OpenXREvent.SESSION_STATE_CHANGED) {
                this.xrStateChanged(
                        XrEventDataSessionStateChanged.create(event.address())
                );
            }else {
                xrEventsReceived.add(vrEvent);
            }
        }
    }
    private void xrStateChanged(XrEventDataSessionStateChanged event) {
        var stateChange = XRSessionStateChange.fromId(event.state());
        switch (stateChange) {
            case READY: {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    XrSessionBeginInfo sessionBeginInfo = XrSessionBeginInfo.calloc(stack)
                            .type(XR10.XR_TYPE_SESSION_BEGIN_INFO)
                            .next(NULL)
                            .primaryViewConfigurationType(XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO);

                    checkXRError(
                            XR10.xrBeginSession(xrInitializer.xrSession, sessionBeginInfo),
                            "xrBeginSession", "XRStateChangeREADY"
                    );
                }
                this.paused = false;
                break;
            }
            case STOPPING: {
                checkXRError(
                        XR10.xrEndSession(xrInitializer.xrSession),
                        "xrEndSession", "XRStateChangeSTOPPING"
                );

                attachedApp.destroy();
            }
            case VISIBLE, FOCUSED: {
                paused = false;
                break;
            }
            case EXITING, IDLE, SYNCHRONIZED: {
                paused = true;
                break;
            }
            case LOSS_PENDING: {
                break;
            }
            default:
                break;
        }
    }


    public XrView getXrView(EyeType eyeType){
        return xrInitializer.xrViewBuffer.get(eyeType.getId());
    }

    @Override
    public void destroy() {
        xrInitializer.destroy();
    }

    public void checkXRError(int xrResult, String caller, String... args) {
        if (xrResult < 0) {
            attachedApp.logError(String.format(
                    "%s for %s error: %s", caller, String.join(" ", args), getXRActionResult(xrResult)
            ));
        }
    }
    public String getXRActionResult(int resultId) {
        var result = XRActionResult.fromId(resultId);
        String resultString = result != null
                ? result.toString()
                : null;
        if (resultString == null) {
            if(xrInitializer.xrInstance == null){
                return  "Unknown XR Action Result: " + resultId;
            }
            // ask the runtime for the xrResult name
            try (MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer str = stack.calloc(XR10.XR_MAX_RESULT_STRING_SIZE);

                if (XR10.xrResultToString(xrInitializer.xrInstance, resultId, str) == XR10.XR_SUCCESS) {
                    resultString = (memUTF8(memAddress(str)));
                } else {
                    resultString = "Unknown XR Action Result: " + resultId;
                }
            }
        }
        return resultString;
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
    public @NotNull VRRenderer createVRRenderer(@NotNull VRApp vrApp) {
        return null;
    }

    @Override
    public @NotNull VRProviderType getType() {
        return VRProviderType.OPEN_XR;
    }
}
