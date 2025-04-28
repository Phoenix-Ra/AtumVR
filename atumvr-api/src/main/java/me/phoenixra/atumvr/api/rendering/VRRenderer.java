package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.texture.VRTexture;
import org.jetbrains.annotations.NotNull;

public interface VRRenderer {

    void init() throws Throwable;

    void renderFrame();

    void destroy();

    VRScene getCurrentScene();

    VRTexture getTextureRightEye();

    VRTexture getTextureLeftEye();

    int getResolutionWidth();
    int getResolutionHeight();
    long getWindowHandle();

    /**
     * [USE AFTER INITIALIZATION]
     * <br>
     * The area for which the stencil has to be used.
     * It is not rendered on VR eye
     *
     * @param eyeType eye to get the hidden are for
     * @return vertices
     */
    float[] getHiddenAreaVertices(EyeType eyeType);


    @NotNull
    VRApp getVrApp();
}
