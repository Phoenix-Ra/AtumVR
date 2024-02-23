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
        switch (roleHint) {
            case VR.ETrackedDeviceClass_TrackedDeviceClass_HMD:
                return HMD;
            case VR.ETrackedDeviceClass_TrackedDeviceClass_Controller:
                return CONTROLLER;
            case VR.ETrackedDeviceClass_TrackedDeviceClass_GenericTracker:
                return GENERIC_TRACKER;
            case VR.ETrackedDeviceClass_TrackedDeviceClass_TrackingReference:
                return TRACKING_REFERENCE;
            case VR.ETrackedDeviceClass_TrackedDeviceClass_DisplayRedirect:
                return DISPLAY_REDIRECT;
            default:
                return UNKNOWN;
        }
    }
}
