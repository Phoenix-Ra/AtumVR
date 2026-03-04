package me.phoenixra.atumvr.api.input.device;

import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import org.jetbrains.annotations.NotNull;

/**
 * VRDevice for Head-Mounted Display (HMD)
 */
public interface AtumVRDeviceHMD extends AtumVRDevice {
    String ID = "hmd";

    /**
     * Get pose for specified eye
     *
     * @param eyeType the type of eye (RIGHT, LEFT)
     * @return VRPose
     */
    @NotNull
    AtumVRPose getEyePose(@NotNull EyeType eyeType);

}
