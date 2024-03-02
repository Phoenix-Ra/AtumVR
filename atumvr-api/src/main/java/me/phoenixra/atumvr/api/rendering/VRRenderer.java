package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.texture.VRFrameBuffer;
import me.phoenixra.atumvr.api.scene.VRScene;
import org.jetbrains.annotations.NotNull;

public interface VRRenderer {

    void init();

    void updateFrame();

    void destroy();

    VRScene getCurrentScene();

    VRFrameBuffer getFrameBufferRightEye();

    VRFrameBuffer getFrameBufferLeftEye();

    int getResolutionWidth();
    int getResolutionHeight();


    @NotNull
    VRApp getVrApp();
}
