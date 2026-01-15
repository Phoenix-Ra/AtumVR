package me.phoenixra.atumvr.api;

import org.jetbrains.annotations.NotNull;

/**
 * VR session state
 */
public interface VRState {

    /**
     * If VR session is ready, i.e. initialized in VR runtime
     *
     * @return true/false
     */
    boolean isReady();

    /**
     * If VR session is initialized both on VR runtime and AtumVR sides
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
     *     When true, user is interacting with your app,
     *     otherwise user is probably in VR runtime menu
     * </p>
     * <p>
     *     It is highly recommended to limit the resources usage,
     *     when its false (for example render only nearby objects and disable VR hands)
     * </p>
     *
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
