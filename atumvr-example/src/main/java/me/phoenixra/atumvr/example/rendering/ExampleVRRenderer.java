package me.phoenixra.atumvr.example.rendering;



import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.rendering.VRRenderer;
import me.phoenixra.atumvr.core.rendering.VRScene;
import me.phoenixra.atumvr.core.rendering.VRTexture;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.scene.ExampleScene;
import org.jetbrains.annotations.NotNull;

public class ExampleVRRenderer extends VRRenderer {
    private VRScene vrScene;
    public ExampleVRRenderer(VRProvider vrProvider) {
        super(vrProvider);
        vrScene = new ExampleScene(this);
    }


    @Override
    public void onInit() {
        vrScene.init();
    }

    @Override
    public @NotNull VRScene getCurrentScene() {
        return vrScene;
    }


    @Override
    public @NotNull ExampleVRProvider getVrProvider() {
        return (ExampleVRProvider) super.getVrProvider();
    }
}
