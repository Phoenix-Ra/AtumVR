package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.VRProvider;
import org.jetbrains.annotations.NotNull;


public interface VRScene {

    /**
     * Initialize scene
     */
    void init();

    /**
     * Render scene
     *
     * @param context the render context
     */
    void render(@NotNull VRRenderContext context);

    /**
     * Destroy VR scene and release all associated resources
     */
    void destroy();

    /**
     * Get VR renderer associated with this instance
     *
     * @return VRRenderer
     */
    VRRenderer getRenderer();

    /**
     * Get VR provider associated with this instance
     *
     * @return VRProvider
     */
    @NotNull
    default VRProvider getVrProvider(){
        return getRenderer().getVrProvider();
    }
}
