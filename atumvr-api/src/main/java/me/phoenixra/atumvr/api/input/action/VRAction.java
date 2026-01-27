package me.phoenixra.atumvr.api.input.action;

import org.jetbrains.annotations.NotNull;

/**
 * Interface used for VR actions
 * <p>
 *     For Both input (e.g. buttons, joystick, pose...)
 *     and output (e.g. haptic)
 * </p>
 */
public interface VRAction  {

    /**
     * Get action identifier
     *
     * @return the id
     */
    @NotNull VRActionIdentifier getId();

    /**
     * Get action set, that uses this action
     *
     * @return the action set
     */
    @NotNull VRActionSet getActionSet();

    /**
     * Get localized name
     *
     * @return the name
     */
    @NotNull String getLocalizedName();


}

