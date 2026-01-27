package me.phoenixra.atumvr.api.input.device;

import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.misc.pose.VRPose;
import org.jetbrains.annotations.NotNull;

/**
 * VRDevice for Head-Mounted Display (HMD)
 */
public interface VRDeviceHMD extends VRDevice{
    String ID = "hmd";

    /**
     * Get pose for specified eye
     *
     * @param eyeType the type of eye (RIGHT, LEFT)
     * @return VRPose
     */
    @NotNull
    VRPose getEyePose(@NotNull EyeType eyeType);

}
