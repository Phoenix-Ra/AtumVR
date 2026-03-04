package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.AtumVRProvider;
import org.jetbrains.annotations.NotNull;


public interface AtumVRScene {

    /**
     * Initialize scene
     */
    void init();

    /**
     * Render scene
     *
     * @param context the render context
     */
    void render(@NotNull AtumVRRenderContext context);

    /**
     * Destroy VR scene and release all associated resources
     */
    void destroy();

    /**
     * Get VR renderer associated with this instance
     *
     * @return VRRenderer
     */
    AtumVRRenderer getRenderer();

    /**
     * Get VR provider associated with this instance
     *
     * @return VRProvider
     */
    @NotNull
    default AtumVRProvider getVrProvider(){
        return getRenderer().getVrProvider();
    }
}
