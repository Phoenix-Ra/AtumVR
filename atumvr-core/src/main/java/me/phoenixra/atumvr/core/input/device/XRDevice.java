package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.AtumVRDevice;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPoseMutable;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Base abstract class for VR device
 */
@Getter
public abstract class XRDevice implements AtumVRDevice {

    protected final XRProvider vrProvider;


    private final String id;

    protected final AtumVRPoseMutable pose = new AtumVRPoseMutable();


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
