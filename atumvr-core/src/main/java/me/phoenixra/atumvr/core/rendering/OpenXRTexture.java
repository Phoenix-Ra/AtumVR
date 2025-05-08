package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;

import me.phoenixra.atumvr.api.rendering.VRTexture;
import org.lwjgl.opengl.GL30;

@Getter
public class OpenXRTexture implements VRTexture {
    protected final int glTextureId;
    protected int textureIndex;
    protected int frameBufferId;

    protected final int width;
    protected final int height;

    public OpenXRTexture(int width, int height, int colorId, int index){
        this.width = width;
        this.height = height;

        this.glTextureId = colorId;
        this.textureIndex = index;


    }
    public OpenXRTexture init(){
        frameBufferId = GL30.glGenFramebuffers();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.frameBufferId);
        GL30.glFramebufferTextureLayer(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                glTextureId,
                0,
                textureIndex
        );

        VRTexture.checkStatus();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL30.glBindTexture(3553, 0);

        return this;
    }



    public void destroy(){
        GL30.glDeleteTextures(glTextureId);
        GL30.glDeleteFramebuffers(frameBufferId);

    }
}
