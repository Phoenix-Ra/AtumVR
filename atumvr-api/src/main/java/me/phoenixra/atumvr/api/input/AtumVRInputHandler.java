package me.phoenixra.atumvr.api.input;

import me.phoenixra.atumvr.api.AtumVRProvider;
import me.phoenixra.atumvr.api.input.action.AtumVRActionSet;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandsView;
import me.phoenixra.atumvr.api.input.haptics.AtumVRBodyHaptics;
import me.phoenixra.atumvr.api.input.treadmill.AtumVRTreadmillView;
import me.phoenixra.atumvr.api.input.device.AtumVRDevice;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Base class for VR input
 */
public interface AtumVRInputHandler {

    /**
     * Initialize VR input
     */
    void init();

    /**
     * Update input data from VR session
     */
    void update();

    /**
     * Destroy VR input and release all resources attached
     */
    void destroy();


    /**
     * Returns all registered action sets.
     *
     * @return collection of action sets
     */
    Collection<? extends AtumVRActionSet> getActionSets();

    /**
     * Registers a VR device.
     *
     * @param device the device to register
     */
    void registerDevice(AtumVRDevice device);

    /**
     * Gets a device by its ID.
     *
     * @param id the device ID
     * @return the device, or null if not found
     */
    AtumVRDevice getDevice(String id);

    /**
     * Gets a device by ID with type casting.
     *
     * @param id    the device ID
     * @param clazz the expected device class
     * @param <T>   the device type
     * @return the device cast to the specified type
     */
    default <T extends AtumVRDevice> T getDevice(String id, Class<T> clazz){
        return (T) getDevice(id);
    }

    /**
     * Returns all registered VR devices.
     *
     * @return collection of devices
     */
    Collection<? extends AtumVRDevice> getDevices();

    /**
     * Get VR Body view
     *
     * @return the VR Body view
     */
    @NotNull AtumVRBodyView getVRBody();

    /**
     * Get VR Hands view (hand tracking)
     *
     * @return the VR Hands view
     */
    @NotNull AtumVRHandsView getVRHands();

    /**
     * Get VR Treadmill view (locomotion hardware)
     *
     * @return the VR Treadmill view
     */
    @NotNull AtumVRTreadmillView getVRTreadmill();

    /**
     * Get VR Body haptics (vests, suits and their accessories)
     *
     * @return the VR Body haptics bridge
     */
    @NotNull AtumVRBodyHaptics getVRBodyHaptics();


    /**
     * Get supported interaction profile types by the user's hardware
     */
    @NotNull List<VRInteractionProfileType> getSupportedProfileTypes();

    /**
     * Get supported interaction profile types by the user's hardware of specified kind
     */
    default @NotNull List<VRInteractionProfileType> getSupportedProfileTypes(@NotNull VRInteractionProfileType.Kind kind){
        return getSupportedProfileTypes()
                .stream()
                .filter(profile -> profile.getKind() == kind)
                .toList();
    }

    /**
     * Get VR provider associated with this instance
     *
     * @return VRProvider
     */
    @NotNull
    AtumVRProvider getVrProvider();


}
