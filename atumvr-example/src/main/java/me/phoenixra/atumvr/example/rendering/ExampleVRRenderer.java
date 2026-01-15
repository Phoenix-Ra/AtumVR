package me.phoenixra.atumvr.example.rendering;


import me.phoenixra.atumvr.api.rendering.VRScene;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.rendering.XRRenderer;
import me.phoenixra.atumvr.core.rendering.XRTexture;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.scene.ExampleScene;
import org.jetbrains.annotations.NotNull;

public class ExampleVRRenderer extends XRRenderer {
    private VRScene vrScene;
    public ExampleVRRenderer(XRProvider provider) {
        super(provider);
        vrScene = new ExampleScene(this);
    }

    @Override
    protected XRTexture createTexture(int width, int height, int textureId, int index) {
        return new XRTexture(width, height, textureId, index);
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
    public @NotNull ExampleVRProvider getVrProvider() {
        return (ExampleVRProvider) super.getVrProvider();
    }
}
