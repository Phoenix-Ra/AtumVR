package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;
import org.lwjgl.opengl.GL30;

/**
 * Represents a VR render target backed
 * by an OpenGL frameBuffer and texture array layer.
 * <p>
 *     Used by {@link VRRenderer} to render to swapChain images for each eye.
 * </p>
 */
@Getter
public class VRTexture {

    /**Texture's id*/
    protected final int textureId;

    /**Texture's index*/
    protected int textureIndex;

    /**Texture's frameBuffer id*/
    protected int frameBufferId;


    /**Texture's width*/
    protected final int width;

    /**Texture's height*/
    protected final int height;

    public VRTexture(int width, int height,
                     int textureId,
                     int index){
        this.width = width;
        this.height = height;

        this.textureId = textureId;
        this.textureIndex = index;


    }

    /**
     * Initialize VR texture
     *
     * @return the instance
     */
    public VRTexture init(){
        frameBufferId = GL30.glGenFramebuffers();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBufferId);
        GL30.glFramebufferTextureLayer(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                textureId,
                0,
                textureIndex
        );

        checkStatus();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL30.glBindTexture(3553, 0);

        return this;
    }

    private void checkStatus() {
        int i = GL30.glCheckFramebufferStatus(36160);
        if (i != 36053) {
            if (i == 36054) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            } else if (i == 36055) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            } else if (i == 36059) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            } else if (i == 36060) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            } else if (i == 36061) {
                throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
            } else if (i == 1285) {
                throw new RuntimeException("GL_OUT_OF_MEMORY");
            } else {
                throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
            }
        }
    }
    /**
     * Destroy VR texture and release all associated resources
     */
    public void destroy(){
        GL30.glDeleteTextures(textureId);
        GL30.glDeleteFramebuffers(frameBufferId);
    }

}
