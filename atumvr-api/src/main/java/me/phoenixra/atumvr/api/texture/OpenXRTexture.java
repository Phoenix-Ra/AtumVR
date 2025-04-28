package me.phoenixra.atumvr.api.texture;

import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.VR;

import java.nio.ByteBuffer;

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


        init();
    }
    protected void init(){

        //FRAME BUFFER
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

    }



    public void destroy(){
        GL30.glDeleteTextures(glTextureId);
        GL30.glDeleteFramebuffers(frameBufferId);

    }
}
