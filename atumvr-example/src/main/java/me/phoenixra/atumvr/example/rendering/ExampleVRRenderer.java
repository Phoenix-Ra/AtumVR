package me.phoenixra.atumvr.example.rendering;

import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.DefaultVRRenderer;
import me.phoenixra.atumvr.api.scene.VRScene;
import me.phoenixra.atumvr.example.scene.ExampleScene;
import me.phoenixra.atumvr.example.scene.ExampleSceneMultiView;

public class ExampleVRRenderer extends DefaultVRRenderer {
    private VRScene vrScene;
    public ExampleVRRenderer(VRApp vrApp) {
        super(vrApp);
        vrScene = new ExampleSceneMultiView(this);
    }

    @Override
    public void onInit() {
        vrScene.init();
    }

    @Override
    public VRScene getCurrentScene() {
        return vrScene;
    }

    @Override
    public boolean isMultiView() {
        return true;
    }
}
