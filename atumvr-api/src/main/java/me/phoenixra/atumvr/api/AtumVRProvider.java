package me.phoenixra.atumvr.api;


import me.phoenixra.atumvr.api.input.AtumVRInputHandler;
import me.phoenixra.atumvr.api.rendering.AtumVRRenderContext;
import me.phoenixra.atumvr.api.rendering.AtumVRRenderer;
import org.jetbrains.annotations.NotNull;


/**
 * The entry point and manager of VR
 */
public interface AtumVRProvider {

    /**
     * Initialize VR session
     *
     * @throws Throwable the error or exception received during initialization
     */
    void initializeVR() throws Throwable;


    /**
     * Sync VR State with VR runtime.
     * <p>
     *     Has to be called before {@link #startFrame}
     * </p>
     */
    void syncState();


    /**
     * Start the frame
     * <p>
     *     Waits for VR runtime next frame to become available,
     *     then prepares the VR frame to render and retrieves relevant VR data
     * </p>
     */
    void startFrame();


    /**
     * Render the frame and let VR runtime know
     * it is ready to be displayed on HMD
     *
     * @param context the render context
     */
    void render(@NotNull AtumVRRenderContext context);


    /**
     * Post render.
     * <p>
     *     Called after {@link #render(AtumVRRenderContext)}<br>
     *     Do nothing by default, intended to be overwritten when needed
     * </p>
     */
    void postRender();


    /**
     * Prepares destroy, by stopping any active things that may be incorrectly destroyed if done late.
     * Usually, this method can be ignored, but
     * if you noticed for example haptic pulse incorrectly working during app shutdown,
     * then use this method at early app shutdown process
     */
    void prepareDestroy();

    /**
     * Destroy VR session and release all associated resources
     */
    void destroy();


    /**
     * Get VR state
     *
     * @return the VR state
     */
    @NotNull
    AtumVRState getState();

    /**
     * Get VR session
     *
     * @return the VR session
     */
    @NotNull
    AtumVRSession getSession();

    /**
     * Get VR Input Handler
     *
     * @return the VR state
     */
    @NotNull
    AtumVRInputHandler getInputHandler();

    /**
     * Get VR Renderer
     *
     * @return the VR state
     */
    @NotNull
    AtumVRRenderer getRenderer();


    /**
     * The name of the VR application
     *
     * @return the app name
     */
    @NotNull
    String getAppName();

    /**
     * Get VR logger
     *
     * @return the VRLogger
     */
    @NotNull
    AtumVRLogger getLogger();


}
