package me.phoenixra.atumvr.core;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.VRProvider;

public abstract class AtumVRApp implements VRApp {

    @Getter
    protected final VRProvider vrProvider;
    @Getter
    protected ConfigManager configManager;

    @Getter @Setter
    protected boolean paused;

    public AtumVRApp(VRProvider vrProvider){
        this.vrProvider = vrProvider;
        configManager = createConfigManager();
    }


    @Override
    public boolean init() throws Throwable{
        vrProvider.initializeVR(this);
        return true;
    }
    protected abstract ConfigManager createConfigManager();
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
        vrProvider.destroy();
    }

    @Override
    public boolean isInitialized() {
        return vrProvider != null && vrProvider.isInitialized();
    }
}
