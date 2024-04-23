package me.phoenixra.atumvr.api.rendering.texture;


import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.Texture;

public interface VRTexture {

    default void destroy(){
        getOpenVrTexture().free();
        GL30.glDeleteTextures(getGlTextureId());
    }

    int getGlTextureId();
    int getFrameBufferId();
    @NotNull
    Texture getOpenVrTexture();
    int getWidth();
    int getHeight();


    /**
     * Check status of currently attached frameBuffer
     * @return true if ready for usage
     */
    static boolean isFrameBufferReady(){
        return GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
    }
}
