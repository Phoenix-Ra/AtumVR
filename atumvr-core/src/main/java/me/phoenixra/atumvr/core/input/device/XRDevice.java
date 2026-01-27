package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.VRDevice;
import me.phoenixra.atumvr.api.misc.pose.VRPose;
import me.phoenixra.atumvr.api.misc.pose.VRPoseMutable;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Base abstract class for VR device
 */
@Getter
public abstract class XRDevice implements VRDevice {

    protected final XRProvider vrProvider;


    private final String id;

    protected final VRPoseMutable pose = new VRPoseMutable();


    protected boolean active;


    public XRDevice(@NotNull XRProvider vrProvider,
                    @NotNull String id){
        this.vrProvider = vrProvider;
        this.id = id;
    }

    /**
     * Update device data
     */
    public abstract void update();


}
