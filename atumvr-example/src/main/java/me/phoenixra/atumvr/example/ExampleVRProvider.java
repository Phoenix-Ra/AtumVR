package me.phoenixra.atumvr.example;

import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.core.openxr.OpenXRProvider;
import me.phoenixra.atumvr.core.openxr.rendering.OpenXRRenderer;
import me.phoenixra.atumvr.example.rendering.ExampleVRRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExampleVRProvider extends OpenXRProvider {

    @Override
    public @NotNull OpenXRRenderer createVRRenderer(@NotNull VRApp vrApp) {
        return new ExampleVRRenderer(vrApp);
    }

    @Override
    public @Nullable VRInputHandler createVRInputHandler() {
        return null;
    }
}
