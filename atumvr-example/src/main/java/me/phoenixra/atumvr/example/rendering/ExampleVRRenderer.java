package me.phoenixra.atumvr.example.rendering;

import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.VRScene;
import me.phoenixra.atumvr.core.openxr.rendering.OpenXRRenderer;
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
