package me.phoenixra.atumvr.api.provider.openvr.devices;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.openvr.VR;

public enum VRDeviceRole {
    LEFT_HAND,
    RIGHT_HAND,
    STYLUS,
    TREADMILL,
    OPT_OUT,
    UNKNOWN;




    @NotNull
    public static VRDeviceRole parseFromInt(int roleHint) {
        switch (roleHint) {
            case VR.ETrackedControllerRole_TrackedControllerRole_LeftHand:
                return LEFT_HAND;
            case VR.ETrackedControllerRole_TrackedControllerRole_RightHand:
                return RIGHT_HAND;
            case VR.ETrackedControllerRole_TrackedControllerRole_Treadmill:
                return TREADMILL;
            case VR.ETrackedControllerRole_TrackedControllerRole_OptOut:
                return OPT_OUT;
            case VR.ETrackedControllerRole_TrackedControllerRole_Stylus:
                return STYLUS;
            default:
                return UNKNOWN;
        }
    }
}
