package me.phoenixra.atumvr.api.input.action;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Interface used for VR action sets
 */
public interface VRActionSet {


    /**
     * Initialize action set
     */
    void init();

    /**
     * Update actions
     */
    void update();

    /**
     * Destroy action set and release all associated resources
     */
    void destroy();


    /**
     * Get all actions of this set
     *
     * @return the actions collection
     */
    Collection<? extends VRAction> getActions();


    /**
     * Get name
     * @return the name
     */
    @NotNull String getName();

    /**
     * Get localized name
     *
     * @return the localized name
     */
    @NotNull String getLocalizedName();

    /**
     * Get action set priority
     *
     * @return the priority
     */
    int getPriority();
}
