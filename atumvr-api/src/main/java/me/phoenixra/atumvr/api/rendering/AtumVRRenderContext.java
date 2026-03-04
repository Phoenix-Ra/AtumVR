package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.AtumVRProvider;

/**
 * Context passed to VR rendering methods containing frame-specific data.
 *
 * @see AtumVRProvider#render(AtumVRRenderContext)
 */
public interface AtumVRRenderContext {

    /**
     * Returns the interpolation factor between the previous and current tick.
     *
     * @return the positive float value
     */
    float partialTicks();
}