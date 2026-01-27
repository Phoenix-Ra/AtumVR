package me.phoenixra.atumvr.example.rendering;



import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.rendering.XRRenderer;
import me.phoenixra.atumvr.core.rendering.XRScene;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.scene.ExampleScene;
import org.jetbrains.annotations.NotNull;

public class ExampleVRRenderer extends XRRenderer {
    private XRScene vrScene;
    public ExampleVRRenderer(XRProvider vrProvider) {
        super(vrProvider);
        vrScene = new ExampleScene(this);
    }


    @Override
    public void onInit() {
        vrScene.init();
    }

    @Override
    public @NotNull XRScene getCurrentScene() {
        return vrScene;
    }


    @Override
    public @NotNull ExampleVRProvider getVrProvider() {
        return (ExampleVRProvider) super.getVrProvider();
    }
}
