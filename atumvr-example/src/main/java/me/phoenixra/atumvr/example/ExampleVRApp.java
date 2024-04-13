package me.phoenixra.atumvr.example;

import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.core.AtumVRApp;
import me.phoenixra.atumvr.example.rendering.ExampleVRRenderer;
import org.jetbrains.annotations.NotNull;

public class ExampleVRApp extends AtumVRApp {
    public ExampleVRApp(VRCore vrCore) {
        super(vrCore);
    }

    @Override
    public @NotNull VRRenderer createVRRenderer(@NotNull VRApp vrApp) {
        return new ExampleVRRenderer(vrApp);
    }

}
