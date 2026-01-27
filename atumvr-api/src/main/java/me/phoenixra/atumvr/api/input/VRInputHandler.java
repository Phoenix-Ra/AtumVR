package me.phoenixra.atumvr.api.input;

import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.input.action.VRActionSet;
import me.phoenixra.atumvr.api.input.device.VRDevice;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Base class for VR input
 */
public interface VRInputHandler {

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
    Collection<? extends VRActionSet> getActionSets();

    /**
     * Registers a VR device.
     *
     * @param device the device to register
     */
    void registerDevice(VRDevice device);

    /**
     * Gets a device by its ID.
     *
     * @param id the device ID
     * @return the device, or null if not found
     */
    VRDevice getDevice(String id);

    /**
     * Gets a device by ID with type casting.
     *
     * @param id    the device ID
     * @param clazz the expected device class
     * @param <T>   the device type
     * @return the device cast to the specified type
     */
    default <T extends VRDevice> T getDevice(String id, Class<T> clazz){
        return (T) getDevice(id);
    }

    /**
     * Returns all registered VR devices.
     *
     * @return collection of devices
     */
    Collection<? extends VRDevice> getDevices();

    /**
     * Get VR provider associated with this instance
     *
     * @return VRProvider
     */
    @NotNull
    VRProvider getVrProvider();


}
