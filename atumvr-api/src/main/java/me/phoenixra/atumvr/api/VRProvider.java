package me.phoenixra.atumvr.api;


import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.rendering.IRenderContext;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.jetbrains.annotations.NotNull;


/**
 * Entry point for VR.
 */
public interface VRProvider {

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
     * it is ready to be displayed
     *
     * @param context the render context
     */
    void render(@NotNull IRenderContext context);


    /**
     * Post render.
     * <p>
     *     Called after {@link #render(IRenderContext)}
     *     Do nothing by default, can be overwritten.
     * </p>
     */
    void postRender();


    /**
     * Destroy VR session
     */
    void destroy();


    /**
     * Get VR state associated with this provider
     *
     * @return the VR state
     */
    @NotNull
    VRState getState();

    /**
     * Get VR Input Handler associated with this provider
     *
     * @return the VR state
     */
    @NotNull
    VRInputHandler getInputHandler();

    /**
     * Get VR Renderer associated with this provider
     *
     * @return the VR state
     */
    @NotNull
    VRRenderer getRenderer();


    /**
     * Get App name, that is shown for VR user by VR runtime
     *
     * @return the app name
     */
    @NotNull
    String getAppName();

    /**
     * Get logger
     *
     * @return the VRLogger
     */
    @NotNull
    VRLogger getLogger();


}
