package me.phoenixra.atumvr.api.rendering;

import lombok.Getter;
import org.lwjgl.opengl.GL30;
@Getter
public class VRFrameBuffer {
    private final VRTexture vrTexture;
    private final int frameBufferId;
    public VRFrameBuffer(VRTexture texture){
        this.vrTexture = texture;

        frameBufferId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferId);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture.getId());
        GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL30.GL_TEXTURE_2D,
                texture.getId(),
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
}
