package me.phoenixra.atumvr.api.rendering;


import org.lwjgl.opengl.GL30;

public interface VRTexture {

    void destroy();

    int getGlTextureId();
    int getFrameBufferId();

    int getWidth();
    int getHeight();




    static void checkStatus() {
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
}
