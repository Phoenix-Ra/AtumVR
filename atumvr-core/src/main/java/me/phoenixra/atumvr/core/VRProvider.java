package me.phoenixra.atumvr.core;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.core.enums.XREvent;
import me.phoenixra.atumvr.core.exceptions.VRException;
import me.phoenixra.atumvr.core.rendering.VRRenderContext;
import me.phoenixra.atumvr.core.enums.XRActionResult;
import me.phoenixra.atumvr.core.enums.XRSessionState;
import me.phoenixra.atumvr.core.rendering.VRRenderer;
import me.phoenixra.atumvr.core.session.VRSession;
import me.phoenixra.atumvr.core.input.VRInputHandler;
import org.jetbrains.annotations.NotNull;
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

/**
 * The entry point and manager of VR
 *
 * <h2>Thread Safety</h2>
 * <p>
 *     All VR operations should be performed on the
 *     same thread (typically the render thread).
 * </p>
 */
@Getter
public abstract class VRProvider {

    /**
     * The name of the VR application
     */
    private final String appName;

    /**
     * The logger instance.
     */
    private final VRLogger logger;

    /**
     * The VR session handler
     */
    protected VRSession session;

    /**
     * The state manager tracking VR session lifecycle.
     */
    protected VRState state;

    /**
     * The VR input handler
     */
    protected VRInputHandler inputHandler;

    /**
     * The VR renderer
     */
    protected VRRenderer renderer;



    /**
     * The predicted display time for the current frame from the OpenXR runtime.
     */
    @Setter
    protected long xrDisplayTime;


    public VRProvider(@NotNull String appName,
                      @NotNull VRLogger logger){
        this.appName = appName;
        this.logger = logger;
        this.state = createStateHandler();
        this.renderer = createRenderer();
        this.inputHandler = createInputHandler();
    }


    // -------- SETTING UP --------

    /**
     * Create renderer
     *
     * @return new VRRenderer instance
     */
    protected abstract @NotNull VRRenderer createRenderer();

    /**
     * Create input handler
     *
     * @return new VRInputHandler instance
     */
    protected abstract @NotNull VRInputHandler createInputHandler();


    /**
     * Create state handler
     * <p>
     *     Override if you need custom state handler
     * </p>
     *
     * @return new VRState instance
     */
    protected @NotNull VRState createStateHandler(){
        return new VRState(this);
    }

    /**
     * Create session handler
     * <p>
     *     Override if you need custom session handler
     * </p>
     *
     * @return new VRSession instance
     */
    protected @NotNull VRSession createSessionHandler(){
        return new VRSession(this);
    }


    /**
     * Get XR extensions to apply for app during initialization
     * <br>
     * By default, the following OpenXR extensions are applied:
     * <ul>
     *     <li>{@link EXTHPMixedRealityController#XR_EXT_HP_MIXED_REALITY_CONTROLLER_EXTENSION_NAME}</li>
     *     <li>{@link HTCViveCosmosControllerInteraction#XR_HTC_VIVE_COSMOS_CONTROLLER_INTERACTION_EXTENSION_NAME}</li>
     *     <li>{@link BDControllerInteraction#XR_BD_CONTROLLER_INTERACTION_EXTENSION_NAME}</li>
     * </ul>
     *
     * <p>
     *     Override if you need different OpenXR extensions
     * </p>
     *
     * @return list of XR extensions
     */
    public @NotNull List<String> getXRAppExtensions(){
        return List.of(
                EXTHPMixedRealityController.XR_EXT_HP_MIXED_REALITY_CONTROLLER_EXTENSION_NAME,
                HTCViveCosmosControllerInteraction.XR_HTC_VIVE_COSMOS_CONTROLLER_INTERACTION_EXTENSION_NAME,
                BDControllerInteraction.XR_BD_CONTROLLER_INTERACTION_EXTENSION_NAME

        );
    }

    /**
     * Retrieves the list of supported swap chain formats.
     * <p>
     *     The first format in the list that is supported
     *     by the user's hardware will be applied during initialization,
     *     starting from index 0.
     * </p>
     *
     * By default, the following swap chain formats are included, in order of priority:
     * <ul>
     *     <li>{@code GL21.GL_SRGB8_ALPHA8}</li>
     *     <li>{@code GL21.GL_SRGB8}</li>
     *     <li>{@code GL11.GL_RGB10_A2}</li>
     *     <li>{@code GL30.GL_RGBA16F}</li>
     *     <li>{@code GL30.GL_RGB16F}</li>
     *     <li>Fallback formats:
     *         <ul>
     *             <li>{@code GL11.GL_RGBA8}</li>
     *             <li>{@code GL31.GL_RGBA8_SNORM}</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * <p>
     *     Override if you need to specify
     *     a different list of formats or modify their apply priority.
     * </p>
     *
     * @return list of integers representing swap chain formats.
     */
    public @NotNull List<Integer> getSwapChainFormats(){
        return List.of(
                GL30.GL_SRGB8_ALPHA8,
                GL30.GL_SRGB8,
                // others
                GL30.GL_RGB10_A2,
                GL30.GL_RGBA16F,
                GL30.GL_RGB16F,

                // Fallback
                GL30.GL_RGBA8,
                GL31.GL_RGBA8_SNORM
        );
    }

    // -------- LIFECYCLE --------

    /**
     * Initialize VR session
     *
     * @throws Throwable the error or exception received during initialization
     */
    public void initializeVR() throws Throwable{
        if (state.isInitialized()) {
            throw new VRException("Already initialized!");
        }

        session = createSessionHandler();
        session.init();
        state.init();
    }

    /**
     * Sync VR State with VR runtime.
     * <p>
     *     Has to be called before {@link #startFrame}
     * </p>
     */
    public void syncState(){
        if(!state.isInitialized()){
            return;
        }
        state.pollVREvents();
    }

    /**
     * Start the frame
     * <p>
     *     Waits for VR runtime next frame to become available,
     *     then prepares the VR frame to render and retrieves relevant VR data
     * </p>
     */
    public void startFrame() {
        if(!state.isInitialized()){
            return;
        }
        renderer.prepareFrame();
        inputHandler.update();
    }

    /**
     * Render the frame and let VR runtime know
     * it is ready to be displayed on HMD
     *
     * @param context the render context
     */
    public void render(@NotNull VRRenderContext context) {
        if(!state.isInitialized()){
            return;
        }
        renderer.renderFrame(context);
    }

    /**
     * Post render.
     * <p>
     *     Called after {@link #render(VRRenderContext)}<br>
     *     Do nothing by default, intended to be overwritten when needed
     * </p>
     */
    public void postRender() {

    }

    /**
     * On session state changed.
     * <p>
     *     Called from VRState only.<br>
     *     Do nothing by default, intended to be overwritten when needed
     * </p>
     *
     * @param state the new session state
     */
    protected void onStateChanged(@NotNull XRSessionState state){

    }

    /**
     * On XR event received
     * <p>
     *     Called from VRState only.<br>
     *     Do nothing by default, intended to be overwritten when needed
     * </p>
     * <p>
     *     This method won't be called for state change,
     *     use {@link #onStateChanged(XRSessionState)} instead
     * </p>
     * @param state the new session state
     */
    protected void onXREventReceived(@NotNull XREvent state){

    }



    // -------- HELPER METHODS --------

    public void checkXRError(int xrResult,
                             @NotNull String caller,
                             String... args) throws VRException{
        checkXRError(true,xrResult,caller,args);
    }

    public void checkXRError(boolean throwError,
                             int xrResult,
                             @NotNull String caller,
                             String... args) throws VRException{
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
            if(session.getInstance() == null){
                return  "Unknown XR Action Result: " + resultId;
            }
            // ask the runtime for the xrResult name
            try (MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer str = stack.calloc(XR10.XR_MAX_RESULT_STRING_SIZE);

                if (XR10.xrResultToString(session.getInstance().getHandle(), resultId, str) == XR10.XR_SUCCESS) {
                    resultString = (memUTF8(memAddress(str)));
                } else {
                    resultString = "Unknown XR Action Result: " + resultId;
                }
            }
        }
        return resultString;
    }


    // -------- DESTROY --------

    /**
     * Destroy VR session and release all associated resources
     */
    public void destroy() {
        if(renderer != null){
            renderer.destroy();
        }
        if(inputHandler != null){
            inputHandler.destroy();
        }
        session.destroy();
        state.destroy();
    }
}
