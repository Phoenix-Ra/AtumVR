package me.phoenixra.atumvr.api.rendering;


/**
 * Represents a VR render target backed
 * by an OpenGL frameBuffer and texture array layer.
 * <p>
 *     Used by {@link VRRenderer} to render display for each eye.
 * </p>
 */
public interface VRTexture {


    /**
     * Initialize VR texture
     *
     * @return the instance
     */
    VRTexture init();

    /**
     * Destroy VR texture and release all associated resources
     */
    void destroy();

    /**
     * Get texture id
     *
     * @return the texture id
     */
    int getTextureId();


    /**
     * Get texture FrameBuffer id
     *
     * @return the FrameBuffer id
     */
    int getFrameBufferId();


    /**
     * Get texture width
     *
     * @return the texture width
     */
    int getWidth();

    /**
     * Get texture height
     *
     * @return the texture height
     */
    int getHeight();



}
