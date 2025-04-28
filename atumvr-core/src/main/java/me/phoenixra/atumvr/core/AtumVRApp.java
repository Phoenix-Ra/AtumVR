package me.phoenixra.atumvr.core;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRProvider;

public class AtumVRApp implements VRApp {

    @Getter
    private final VRProvider vrProvider;


    @Getter @Setter
    private boolean paused;

    public AtumVRApp(VRProvider vrProvider){
        this.vrProvider = vrProvider;
    }


    @Override
    public void init() {
        vrProvider.initializeVR(this);
    }

    @Override
    public void preRender(float partialTick) {
        vrProvider.onPreRender(partialTick);
        setPaused(vrProvider.isPaused());
    }

    @Override
    public void render(float partialTick) {
        vrProvider.onRender(partialTick);
    }

    @Override
    public void postRender(float partialTick) {
        vrProvider.onPostRender(partialTick);
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean isInitialized() {
        return vrProvider != null && vrProvider.isInitialized();
    }
}
