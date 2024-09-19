package me.phoenixra.atumvr.api.rendering.texture.impl;

import lombok.Getter;
import me.phoenixra.atumvr.api.rendering.texture.VRTexture;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.VR;

import java.nio.ByteBuffer;
@Getter
public class AtumVRTexture implements VRTexture {
    protected final int glTextureId;
    protected int frameBufferId;
    protected final Texture openVrTexture;

    protected final int width;
    protected final int height;

    public AtumVRTexture(int width, int height, boolean depth){
        this.width = width;
        this.height = height;

        this.glTextureId = GL30.glGenTextures();
        openVrTexture = Texture.calloc();
        init(depth);
    }
    protected void init(boolean depth){
        //TEXTURE
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, glTextureId);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexImage2D(
                GL30.GL_TEXTURE_2D,
                0,
                depth ? GL30.GL_RGBA8 : GL30.GL_RGBA,
                width,
                height,
                0,
                GL30.GL_RGBA,
                GL30.GL_INT,
                (ByteBuffer) null
        );

        openVrTexture.eColorSpace(VR.EColorSpace_ColorSpace_Gamma);
        openVrTexture.eType(VR.ETextureType_TextureType_OpenGL);
        openVrTexture.handle(glTextureId);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);

        //FRAME BUFFER
        frameBufferId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferId);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,getGlTextureId());
        GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0,
                GL30.GL_TEXTURE_2D,
                getGlTextureId(),
                0
        );

        if (!VRTexture.isFrameBufferReady()) {
            throw new RuntimeException("Framebuffer not complete");
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public Texture applyDataToTexture(Texture texture){
        texture.eColorSpace(VR.EColorSpace_ColorSpace_Gamma);
        texture.eType(VR.ETextureType_TextureType_OpenGL);
        texture.handle(glTextureId);
        return texture;
    }


    public void destroy(){
        openVrTexture.free();
        GL30.glDeleteTextures(glTextureId);
        GL30.glDeleteFramebuffers(frameBufferId);

    }
}
