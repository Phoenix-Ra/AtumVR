package me.phoenixra.atumvr.api.rendering.texture;

import lombok.Getter;
import org.lwjgl.opengl.GL30;
@Getter
public class VRFrameBuffer {
    protected final VRTexture vrTexture;
    protected int frameBufferId;
    public VRFrameBuffer(VRTexture texture){
        this.vrTexture = texture;
        init();
    }
    protected void init(){
        frameBufferId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferId);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, vrTexture.getId());
        GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL30.GL_TEXTURE_2D,
                vrTexture.getId(),
                0
        );

        if (!isFrameBufferReady()) {
            throw new RuntimeException("Framebuffer not complete");
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }


    public static boolean isFrameBufferReady(){
        return GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
    }

    public void destroy(){
        GL30.glDeleteBuffers(frameBufferId);
        vrTexture.destroy();
    }
}
