package me.phoenixra.atumvr.api.input.action.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

/**
 * VR action data for vec2 type
 */
public interface VRActionDataVec2 extends VRActionData{

    /**
     * Get Vec2
     *
     * @return the vec2 value
     */
    @NotNull Vector2f getVec2Data();

}
