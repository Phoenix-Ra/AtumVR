package me.phoenixra.atumvr.devices;

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
        return switch (roleHint) {
            case VR.ETrackedControllerRole_TrackedControllerRole_LeftHand -> LEFT_HAND;
            case VR.ETrackedControllerRole_TrackedControllerRole_RightHand -> RIGHT_HAND;
            case VR.ETrackedControllerRole_TrackedControllerRole_Treadmill -> TREADMILL;
            case VR.ETrackedControllerRole_TrackedControllerRole_OptOut -> OPT_OUT;
            case VR.ETrackedControllerRole_TrackedControllerRole_Stylus -> STYLUS;
            default -> UNKNOWN;
        };
    }
}
