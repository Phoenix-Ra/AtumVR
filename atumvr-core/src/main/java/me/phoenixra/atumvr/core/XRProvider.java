package me.phoenixra.atumvr.core;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.api.rendering.IRenderContext;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.core.enums.XRActionResult;
import me.phoenixra.atumvr.core.enums.XRSessionStateChange;
import me.phoenixra.atumvr.core.input.XRInputHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memUTF8;


public abstract class XRProvider implements VRProvider {

    @Getter
    private final String appName;

    @Getter
    private final VRLogger logger;

    @Getter
    protected XRState state;

    @Getter
    protected XRInputHandler inputHandler;

    @Getter
    protected VRRenderer renderer;



    @Getter @Setter
    protected long xrDisplayTime;


    public XRProvider(@NotNull String appName,
                      @NotNull VRLogger logger){
        this.appName = appName;
        this.logger = logger;
        this.state = createStateHandler();
        this.renderer = createRenderer();
        this.inputHandler = createInputHandler();
    }


    public abstract @Nullable XRState createStateHandler();

    public abstract @Nullable XRInputHandler createInputHandler();

    public abstract @NotNull VRRenderer createRenderer();

    public abstract void onStateChanged(XRSessionStateChange state);

    public List<String> getXRAppExtensions(){
        return List.of(
                EXTHPMixedRealityController.XR_EXT_HP_MIXED_REALITY_CONTROLLER_EXTENSION_NAME,
                HTCViveCosmosControllerInteraction.XR_HTC_VIVE_COSMOS_CONTROLLER_INTERACTION_EXTENSION_NAME,
                BDControllerInteraction.XR_BD_CONTROLLER_INTERACTION_EXTENSION_NAME

        );
    }
    public List<Integer> getSwapChainFormats(){
        return List.of(
                GL21.GL_SRGB8_ALPHA8,
                GL21.GL_SRGB8,
                // others
                GL11.GL_RGB10_A2,
                GL30.GL_RGBA16F,
                GL30.GL_RGB16F,

                // Fallback
                GL11.GL_RGBA8,
                GL31.GL_RGBA8_SNORM
        );
    }

    @Override
    public void initializeVR() throws Throwable{
        if (state.isInitialized()) {
            throw new VRException("Already initialized!");
        }


        state.init();

    }

    @Override
    public void syncState(){
        if(!state.isReady() || !state.isInitialized()){
            return;
        }
        state.pollVREvents();
    }

    @Override
    public void startFrame() {
        if(!state.isReady()){
            return;
        }
        renderer.prepareFrame();
        inputHandler.update();
    }

    @Override
    public void render(@NotNull IRenderContext context) {
        if(!state.isReady()){
            return;
        }
        renderer.renderFrame(context);
    }

    @Override
    public void postRender() {

    }




    public void checkXRError(int xrResult, String caller, String... args) throws VRException{
        checkXRError(true,xrResult,caller,args);
    }
    public void checkXRError(boolean throwError,
                             int xrResult, String caller, String... args) throws VRException{
        if (xrResult < 0) {
            String msg = String.format(
                    "%s for %s error: %s",
                    caller,
                    String.join(" ", args),
                    getXRActionResult(xrResult)
            );
            if(!throwError){
                logger.logError(
                        msg
                );
                return;
            }
            throw new VRException(msg);
        }
    }
    public String getXRActionResult(int resultId) {
        var result = XRActionResult.fromId(resultId);
        String resultString = result != null
                ? result.toString()
                : null;
        if (resultString == null) {
            if(state.vrInstance == null){
                return  "Unknown XR Action Result: " + resultId;
            }
            // ask the runtime for the xrResult name
            try (MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer str = stack.calloc(XR10.XR_MAX_RESULT_STRING_SIZE);

                if (XR10.xrResultToString(state.vrInstance.getHandle(), resultId, str) == XR10.XR_SUCCESS) {
                    resultString = (memUTF8(memAddress(str)));
                } else {
                    resultString = "Unknown XR Action Result: " + resultId;
                }
            }
        }
        return resultString;
    }


    @Override
    public void destroy() {
        if(renderer != null){
            renderer.destroy();
        }
        if(inputHandler != null){
            inputHandler.destroy();
        }
        state.destroy();
    }
}
