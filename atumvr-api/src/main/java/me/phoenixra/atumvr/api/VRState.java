package me.phoenixra.atumvr.api;

import org.jetbrains.annotations.NotNull;

/**
 * This class is responsible for tracking the current state of the VR session.
 */
public interface VRState {



    /**
     * If VR session is initialized both on VR runtime and AtumVR side
     *
     * @return true/false
     */
    boolean isInitialized();

    /**
     * If VR session is active
     * <p>
     *     When true, the session is rendering
     * </p>
     *
     * @return true/false
     */
    boolean isActive();

    /**
     * If VR session is focused.
     * <p>
     *     When true, the VR session is active and user is interacting with your app,
     *     otherwise user is probably in VR runtime menu
     * </p>
     * <p>
     *     When its false, highly recommended to limit the resources usage, <br>
     *     (for example render only nearby objects and disable VR hands)
     * </p>
     * @return true/false
     */
    boolean isFocused();


    /**
     * Get VRProvider associated with this state object
     *
     * @return VRProvider
     */
    @NotNull
    VRProvider getVrProvider();

}
