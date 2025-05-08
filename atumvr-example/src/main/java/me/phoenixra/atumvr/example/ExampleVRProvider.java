package me.phoenixra.atumvr.example;


import me.phoenixra.atumconfig.api.ConfigLogger;
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.core.OpenXRState;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.rendering.OpenXRRenderer;
import me.phoenixra.atumvr.example.rendering.ExampleVRRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExampleVRProvider extends OpenXRProvider {

    public ExampleVRProvider(@NotNull VRLogger logger) {
        super("ExampleApp",logger);
    }


    @Override
    public @Nullable OpenXRState createStateHandler() {
        return new OpenXRState(this);
    }


    @Override
    public @NotNull OpenXRRenderer createRenderer() {
        ExampleVRRenderer vrRenderer = new ExampleVRRenderer(this);
        vrRenderer.setupGLContext();
        return vrRenderer;
    }


    @Override
    public @NotNull VRInputHandler createInputHandler() {
        return null;
    }
}
