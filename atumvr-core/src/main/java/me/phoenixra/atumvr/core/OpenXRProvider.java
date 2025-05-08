package me.phoenixra.atumvr.core;

import lombok.Getter;
import me.phoenixra.atumconfig.api.ConfigLogger;
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.VRState;
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.rendering.RenderContext;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.core.enums.OpenXREvent;
import me.phoenixra.atumvr.core.enums.XRActionResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.*;


public abstract class OpenXRProvider implements VRProvider {

    @Getter
    private final String appName;

    @Getter
    private final VRLogger logger;

    @Getter
    protected OpenXRState vrState;

    @Getter
    protected VRInputHandler inputHandler;

    @Getter
    protected VRRenderer vrRenderer;



    @Getter
    protected long xrDisplayTime;





    public OpenXRProvider(@NotNull String appName,
                          @NotNull VRLogger logger){
        this.appName = appName;
        this.logger = logger;
        this.vrState =  createStateHandler();
    }


    public abstract @Nullable OpenXRState createStateHandler();

    public abstract @Nullable VRInputHandler createInputHandler();

    public abstract @NotNull VRRenderer createRenderer();


    @Override
    public void initializeVR() throws Throwable{
        if (vrState.isInitialized()) {
            throw new VRException("Already initialized!");
        }
        this.vrState = new OpenXRState(this);
        this.vrRenderer = createRenderer();
        this.inputHandler = createInputHandler();

        vrState.init();
    }

    @Override
    public void preRender(@NotNull RenderContext context) {
        vrState.pollVREvents();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrFrameState frameState = XrFrameState.calloc(stack).type(XR10.XR_TYPE_FRAME_STATE);

            checkXRError(
                    XR10.xrWaitFrame(
                            vrState.xrSession.getHandle(),
                            XrFrameWaitInfo.calloc(stack)
                                    .type(XR10.XR_TYPE_FRAME_WAIT_INFO),
                            frameState
                    ),
                    "xrWaitFrame", ""
            );

            xrDisplayTime = frameState.predictedDisplayTime();

            checkXRError(
                    XR10.xrBeginFrame(
                            vrState.xrSession.getHandle(),
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
                    vrState.getXrSession().getXrAppSpace()
            );

            checkXRError(
                    XR10.xrLocateViews(
                            vrState.xrSession.getHandle(),
                            viewLocateInfo, viewState,
                            intBuf, vrState.xrSwapChain.getXrViewBuffer()
                    ),
                    "xrLocateViews", ""
            );


        }
    }

    @Override
    public void render(@NotNull RenderContext context) {
        vrRenderer.renderFrame();
    }

    @Override
    public void postRender(@NotNull RenderContext context) {

    }







    public XrView getXrView(EyeType eyeType){
        return vrState.xrSwapChain.getXrViewBuffer().get(eyeType.getId());
    }

    @Override
    public void destroy() {
        vrState.destroy();
    }

    public void checkXRError(int xrResult, String caller, String... args) {
        if (xrResult < 0) {
            logger.logError(String.format(
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
            if(vrState.xrInstance == null){
                return  "Unknown XR Action Result: " + resultId;
            }
            // ask the runtime for the xrResult name
            try (MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer str = stack.calloc(XR10.XR_MAX_RESULT_STRING_SIZE);

                if (XR10.xrResultToString(vrState.xrInstance.getHandle(), resultId, str) == XR10.XR_SUCCESS) {
                    resultString = (memUTF8(memAddress(str)));
                } else {
                    resultString = "Unknown XR Action Result: " + resultId;
                }
            }
        }
        return resultString;
    }

}
