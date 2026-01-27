package me.phoenixra.atumvr.example;


import lombok.Getter;
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.core.XRState;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRSessionState;
import me.phoenixra.atumvr.core.input.XRInputHandler;
import me.phoenixra.atumvr.core.rendering.XRRenderer;
import me.phoenixra.atumvr.example.input.ExampleVRInputHandler;
import me.phoenixra.atumvr.example.rendering.ExampleVRRenderer;
import org.jetbrains.annotations.NotNull;

public class ExampleVRProvider extends XRProvider {

    @Getter
    private boolean xrStopping = false;

    public ExampleVRProvider(@NotNull VRLogger logger) {
        super("ExampleApp",logger);
    }



    @Override
    public @NotNull XRState createStateHandler() {
        return new XRState(this);
    }

    @Override
    public void onStateChanged(@NotNull XRSessionState state) {
        if(state == XRSessionState.STOPPING){
            xrStopping = true;
        }
    }

    @Override
    public @NotNull XRRenderer createRenderer() {
        ExampleVRRenderer vrRenderer = new ExampleVRRenderer(this);
        vrRenderer.setupGLContext();
        return vrRenderer;
    }


    @Override
    public @NotNull XRInputHandler createInputHandler() {
        return new ExampleVRInputHandler(this);
    }

    @Override
    public @NotNull ExampleVRInputHandler getInputHandler() {
        return (ExampleVRInputHandler) super.getInputHandler();
    }
}
