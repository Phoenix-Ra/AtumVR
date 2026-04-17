package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.rendering.AtumVRTexture;
import org.lwjgl.opengl.GL30;

/**
 * XR implementation of {@link AtumVRTexture}
 */
@Getter
public class XRTexture implements AtumVRTexture {

    protected final int textureId;

    protected int textureIndex;

    protected int frameBufferId;


    protected final int width;

    protected final int height;

    public XRTexture(int width, int height,
                     int textureId,
                     int index){
        this.width = width;
        this.height = height;

        this.textureId = textureId;
        this.textureIndex = index;


    }

    public XRTexture init(){
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
        int i = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (i != GL30.GL_FRAMEBUFFER_COMPLETE) {
            if (i == GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            } else if (i == GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            } else if (i == GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            } else if (i == GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            } else if (i == GL30.GL_FRAMEBUFFER_UNSUPPORTED) {
                throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
            } else if (i == GL30.GL_OUT_OF_MEMORY) {
                throw new RuntimeException("GL_OUT_OF_MEMORY");
            } else {
                throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
            }
        }
    }


    public void destroy(){
        GL30.glDeleteFramebuffers(frameBufferId);
        GL30.glDeleteTextures(textureId);
    }

}