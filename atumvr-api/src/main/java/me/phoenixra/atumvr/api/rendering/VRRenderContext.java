package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.VRProvider;

/**
 * Context passed to VR rendering methods containing frame-specific data.
 *
 * @see VRProvider#render(VRRenderContext)
 */
public interface VRRenderContext {

    /**
     * Returns the interpolation factor between the previous and current tick.
     *
     * @return the positive float value
     */
    float partialTicks();
}