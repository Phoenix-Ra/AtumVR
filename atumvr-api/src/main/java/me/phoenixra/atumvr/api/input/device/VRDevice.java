package me.phoenixra.atumvr.api.input.device;

import me.phoenixra.atumvr.api.misc.pose.VRPose;
import org.jetbrains.annotations.NotNull;

/**
 * It is a holder of VR device data and operations with it.
 * (HMD, Controllers, Trackers)
 */
public interface VRDevice {

    /**
     * Get VR device identifier
     */
    @NotNull
    String getId();

    /**
     * If device is active (connected and tracked)
     */
    boolean isActive();


    /**
     * Get VR pose data
     */
    @NotNull
    VRPose getPose();
}
