package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.core.misc.pose.VRPose;
import me.phoenixra.atumvr.core.misc.pose.VRPoseMutable;
import me.phoenixra.atumvr.core.VRProvider;
import org.jetbrains.annotations.NotNull;

/**
 * VRDevice is a holder of VR device data and operations with it.
 * (HMD, Controllers, Trackers)
 */
public abstract class VRDevice {

    @Getter
    protected final VRProvider vrProvider;

    /**
     * VR device identifier
     */
    @Getter
    private final String id;


    /**
     * VR pose data
     */
    protected final VRPoseMutable pose = new VRPoseMutable();

    /**
     * If device is active (connected, tracked)
     */
    @Getter
    protected boolean active;


    public VRDevice(@NotNull VRProvider vrProvider,
                    @NotNull String id){
        this.vrProvider = vrProvider;
        this.id = id;
    }

    /**
     * Update device data
     */
    public abstract void update();


    /**
     * Get VR pose of the device
     */
    public @NotNull VRPose getPose() {
        return pose;
    }
}
