package me.phoenixra.atumvr.example;


import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.core.OpenXRState;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.enums.XRSessionStateChange;
import me.phoenixra.atumvr.core.input.OpenXRInputHandler;
import me.phoenixra.atumvr.core.rendering.OpenXRRenderer;
import me.phoenixra.atumvr.example.input.ExampleVRInputHandler;
import me.phoenixra.atumvr.example.rendering.ExampleVRRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExampleVRProvider extends OpenXRProvider {

    @Getter
    private boolean xrStopping = false;

    public ExampleVRProvider(@NotNull VRLogger logger) {
        super("ExampleApp",logger);
    }



    @Override
    public @Nullable OpenXRState createStateHandler() {
        return new OpenXRState(this);
    }

    @Override
    public void onStateChanged(XRSessionStateChange state) {
        if(state == XRSessionStateChange.STOPPING){
            xrStopping = true;
        }
    }

    @Override
    public @NotNull OpenXRRenderer createRenderer() {
        ExampleVRRenderer vrRenderer = new ExampleVRRenderer(this);
        vrRenderer.setupGLContext();
        return vrRenderer;
    }


    @Override
    public @NotNull OpenXRInputHandler createInputHandler() {
        return new ExampleVRInputHandler(this);
    }

    @Override
    public @NotNull ExampleVRInputHandler getInputHandler() {
        return (ExampleVRInputHandler) super.getInputHandler();
    }
}
