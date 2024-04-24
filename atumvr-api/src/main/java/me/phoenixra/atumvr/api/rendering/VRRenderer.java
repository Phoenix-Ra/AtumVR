package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.rendering.texture.VRTexture;
import me.phoenixra.atumvr.api.scene.VRScene;
import org.jetbrains.annotations.NotNull;

public interface VRRenderer {

    void init() throws Throwable;

    void updateFrame();

    void destroy();

    VRScene getCurrentScene();

    VRTexture getTextureRightEye();

    VRTexture getTextureLeftEye();

    int getResolutionWidth();
    int getResolutionHeight();

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
