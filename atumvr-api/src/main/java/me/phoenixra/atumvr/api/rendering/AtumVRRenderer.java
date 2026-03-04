package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.AtumVRProvider;
import me.phoenixra.atumvr.api.enums.EyeType;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for VR rendering
 */
public interface AtumVRRenderer {

    /**
     * Initialize VR rendering
     *
     * @throws Throwable the error or exception received during initialization
     */
    void init() throws Throwable;

    /**
     * Prepare the frame.
     * <p>
     *     This method waits for the next frame from the VR runtime to become available. Once available,
     *     it begins the frame and allocates the necessary memory for managing frame-related data
     *     associated with the VR runtime.
     * </p>
     * <p>
     *     Has to be called at the beginning of {@link AtumVRProvider#startFrame}
     * </p>
     */
    void prepareFrame();

    /**
     * Render the frame
     *
     * @param context the render context
     */
    void renderFrame(@NotNull AtumVRRenderContext context);

    /**
     * Destroy VR renderer and release all resources attached
     */
    void destroy();


    /**
     * Get currently active scene
     *
     * @return VR scene
     */
    AtumVRScene getCurrentScene();



    /**
     * Get texture for left eye
     *
     * @return VR texture
     */
    AtumVRTexture getTextureLeftEye();

    /**
     * Get texture for right eye
     *
     * @return VR texture
     */
    AtumVRTexture getTextureRightEye();



    /**
     * Get Eye resolution width
     *
     * @return the resolution width
     */
    int getResolutionWidth();

    /**
     * Get Eye resolution height
     *
     * @return the resolution height
     */
    int getResolutionHeight();


    /**
     * Get Window handle from OpenGL
     * <p>
     *    If your app already creates OpenGL context,<br>
     *    override this method to provide your window handle,<br>
     *    <b>otherwise there will be an error during initialization</b>
     * </p>
     */
    long getWindowHandle();

    /**
     * The area for which the stencil has to be used on eye texture
     *
     * @param eyeType eye to get the hidden are for
     * @return vertices
     */
    float[] getHiddenAreaVertices(EyeType eyeType);


    /**
     * Get VR provider associated with this instance
     *
     * @return VRProvider
     */
    @NotNull
    AtumVRProvider getVrProvider();
}
