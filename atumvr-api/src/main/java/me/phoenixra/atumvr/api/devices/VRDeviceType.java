package me.phoenixra.atumvr.api.devices;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.openvr.VR;

public enum VRDeviceType {
    HMD,
    CONTROLLER,
    GENERIC_TRACKER,
    TRACKING_REFERENCE,
    DISPLAY_REDIRECT,
    UNKNOWN;




    @NotNull
    public static VRDeviceType parseFromInt(int roleHint) {
        return switch (roleHint) {
            case VR.ETrackedDeviceClass_TrackedDeviceClass_HMD -> HMD;
            case VR.ETrackedDeviceClass_TrackedDeviceClass_Controller -> CONTROLLER;
            case VR.ETrackedDeviceClass_TrackedDeviceClass_GenericTracker -> GENERIC_TRACKER;
            case VR.ETrackedDeviceClass_TrackedDeviceClass_TrackingReference -> TRACKING_REFERENCE;
            case VR.ETrackedDeviceClass_TrackedDeviceClass_DisplayRedirect -> DISPLAY_REDIRECT;
            default -> UNKNOWN;
        };
    }
}
