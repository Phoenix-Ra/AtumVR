package me.phoenixra.atumvr.api.devices.pose;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.openvr.VR;

public enum TrackingState {
    RUNNING_OK,
    RUNNING_OUT_OF_RANGE,
    CALIBRATING_IN_PROGRESS,
    CALIBRATING_OUT_OF_RANGE,
    FALLBACK_ROTATION_ONLY,
    NOT_INITIALIZED;




    @NotNull
    public static TrackingState parseFromInt(int trackingHint) {
        switch (trackingHint) {
            case VR.ETrackingResult_TrackingResult_Running_OK:
                return RUNNING_OK;
            case VR.ETrackingResult_TrackingResult_Running_OutOfRange:
                return RUNNING_OUT_OF_RANGE;
            case VR.ETrackingResult_TrackingResult_Calibrating_InProgress:
                return CALIBRATING_IN_PROGRESS;
            case VR.ETrackingResult_TrackingResult_Calibrating_OutOfRange:
                return CALIBRATING_OUT_OF_RANGE;
            case VR.ETrackingResult_TrackingResult_Fallback_RotationOnly:
                return FALLBACK_ROTATION_ONLY;
            default:
                return NOT_INITIALIZED;
        }
    }
}
