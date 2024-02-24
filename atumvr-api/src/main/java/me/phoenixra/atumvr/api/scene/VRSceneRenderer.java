package me.phoenixra.atumvr.api.scene;

import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.VRFrameBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VRSceneRenderer {

    void init();

    void updateFrame();

    void destroy();

    VRFrameBuffer getFrameBufferRightEye();

    VRFrameBuffer getFrameBufferLeftEye();
    @NotNull
    VRApp getVrApp();
}
