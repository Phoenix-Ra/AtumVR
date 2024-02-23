package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.VRApp;
import org.jetbrains.annotations.NotNull;

public interface VRSceneRenderer {

    void init();

    void updateFrame();

    int getTextureIdRightEye();
    int getTextureIdLeftEye();
    @NotNull
    VRApp getVrApp();
}
