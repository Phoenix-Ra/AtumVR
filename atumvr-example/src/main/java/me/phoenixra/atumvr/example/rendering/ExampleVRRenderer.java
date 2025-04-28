package me.phoenixra.atumvr.example.rendering;

import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.provider.openvr.rendering.OpenVRRenderer;
import me.phoenixra.atumvr.api.provider.openvr.scene.VRScene;
import me.phoenixra.atumvr.core.openxr.OpenXRRenderer;
import me.phoenixra.atumvr.example.scene.ExampleScene;

public class ExampleVRRenderer extends OpenXRRenderer {
    private VRScene vrScene;
    public ExampleVRRenderer(VRApp vrApp) {
        super(vrApp);
        vrScene = new ExampleScene(this);
    }

    @Override
    public void onInit() {
        vrScene.init();
    }

    @Override
    public VRScene getCurrentScene() {
        return vrScene;
    }


}
