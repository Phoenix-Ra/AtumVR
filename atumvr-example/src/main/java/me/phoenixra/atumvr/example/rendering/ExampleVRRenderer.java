package me.phoenixra.atumvr.example.rendering;


import me.phoenixra.atumvr.api.rendering.VRScene;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.rendering.OpenXRRenderer;
import me.phoenixra.atumvr.core.rendering.OpenXRTexture;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.scene.ExampleScene;
import org.jetbrains.annotations.NotNull;

public class ExampleVRRenderer extends OpenXRRenderer {
    private VRScene vrScene;
    public ExampleVRRenderer(OpenXRProvider provider) {
        super(provider);
        vrScene = new ExampleScene(this);
    }

    @Override
    protected OpenXRTexture createTexture(int width, int height, int textureId, int index) {
        return new OpenXRTexture(width, height, textureId, index);
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
