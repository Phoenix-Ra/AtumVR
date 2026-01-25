package me.phoenixra.atumvr.example;


import lombok.Getter;
import me.phoenixra.atumvr.core.VRLogger;
import me.phoenixra.atumvr.core.VRState;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRSessionState;
import me.phoenixra.atumvr.core.input.VRInputHandler;
import me.phoenixra.atumvr.core.rendering.VRRenderer;
import me.phoenixra.atumvr.example.input.ExampleVRInputHandler;
import me.phoenixra.atumvr.example.rendering.ExampleVRRenderer;
import org.jetbrains.annotations.NotNull;

public class ExampleVRProvider extends VRProvider {

    @Getter
    private boolean xrStopping = false;

    public ExampleVRProvider(@NotNull VRLogger logger) {
        super("ExampleApp",logger);
    }



    @Override
    public @NotNull VRState createStateHandler() {
        return new VRState(this);
    }

    @Override
    public void onStateChanged(@NotNull XRSessionState state) {
        if(state == XRSessionState.STOPPING){
            xrStopping = true;
        }
    }

    @Override
    public @NotNull VRRenderer createRenderer() {
        ExampleVRRenderer vrRenderer = new ExampleVRRenderer(this);
        vrRenderer.setupGLContext();
        return vrRenderer;
    }


    @Override
    public @NotNull VRInputHandler createInputHandler() {
        return new ExampleVRInputHandler(this);
    }

    @Override
    public @NotNull ExampleVRInputHandler getInputHandler() {
        return (ExampleVRInputHandler) super.getInputHandler();
    }
}
