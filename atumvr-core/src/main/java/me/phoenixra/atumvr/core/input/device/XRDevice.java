package me.phoenixra.atumvr.core.input.device;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.VRDevice;
import me.phoenixra.atumvr.api.misc.pose.VRPose;
import me.phoenixra.atumvr.api.misc.pose.VRPoseMutable;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;


public abstract class XRDevice implements VRDevice {
    @Getter
    protected final XRProvider provider;
    @Getter
    private final String id;


    protected final VRPoseMutable pose = new VRPoseMutable();

    @Getter
    protected boolean active;


    public XRDevice(XRProvider provider, String id){
        this.provider = provider;
        this.id = id;
    }

    public abstract void update();


    @Override
    public @NotNull VRPose getPose() {
        return pose;
    }
}
