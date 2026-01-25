package me.phoenixra.atumvr.core.input.action.data;

import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for VR action data.
 * <p>
 *     Its purpose is to allow multi(sub-actions) and single actions data
 *     be accessed under the same base class for convenience
 * </p>
 */
public interface VRActionData {

    /**
     * If action is active
     *
     * @return true/false
     */
    boolean isActive();

    /**
     * If action data has been changed
     *
     * @return true/false
     */
    boolean isChanged();

    /**
     * Get last data changed
     *
     * @return the value retrieved from openXR
     */
    long getLastChangeTime();


    /**
     * Get id
     *
     * @return the action identifier
     */
    @NotNull ActionIdentifier getId();
}
