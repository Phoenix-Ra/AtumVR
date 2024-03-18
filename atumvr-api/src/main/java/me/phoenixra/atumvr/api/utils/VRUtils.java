package me.phoenixra.atumvr.api.utils;

import org.joml.Matrix4f;
import org.lwjgl.openvr.HmdMatrix34;

public class VRUtils {

    public static Matrix4f convertVrMatrix(HmdMatrix34 hmdMatrix){
        Matrix4f mat = new Matrix4f();
        return mat.set(hmdMatrix.m(0), hmdMatrix.m(1), hmdMatrix.m(2), hmdMatrix.m(3),
                hmdMatrix.m(4), hmdMatrix.m(5), hmdMatrix.m(6), hmdMatrix.m(7),
                hmdMatrix.m(8), hmdMatrix.m(9), hmdMatrix.m(10), hmdMatrix.m(11),
                0f, 0f, 0f, 1f
        );
    }



    public static String getInputErrorMessage(int error){
        if(error == 0) return "VRInputError_None";
        if(error == 1) return "VRInputError_NameNotFound";
        if(error == 2) return "VRInputError_WrongType";
        if(error == 3) return "VRInputError_InvalidHandle";
        if(error == 4) return "VRInputError_InvalidParam";
        if(error == 5) return "VRInputError_NoSteam";
        if(error == 6) return "VRInputError_MaxCapacityReached";
        if(error == 7) return "VRInputError_IPCError";
        if(error == 8) return "VRInputError_NoActiveActionSet";
        if(error == 9) return "VRInputError_InvalidDevice";
        if(error == 10) return "VRInputError_InvalidPriority";
        if(error == 11) return "VRInputError_InvalidBoneCount";
        if(error == 12) return "VRInputError_InvalidCompressedData";
        if(error == 13) return "VRInputError_NoData";
        if(error == 14) return "VRInputError_BufferTooSmall";
        if(error == 15) return "VRInputError_MismatchedActionManifest";
        if(error == 16) return "VRInputError_MissingSkeletonData";
        if(error == 17) return "VRInputError_InvalidBoneIndex";
        if(error == 18) return "VRInputError_InvalidPriority";
        if(error == 19) return "VRInputError_PermissionDenied";
        if(error == 20) return "VRInputError_WrongType";
        return "Unknown";
    }
    public static String getCompositorErrorMessage(int code) {
        if(code == 0) return "VRCompositorError_None";
        if(code == 1) return "VRCompositorError_RequestFailed";
        if(code == 100) return "VRCompositorError_IncompatibleVersion";
        if(code == 101) return "VRCompositorError_DoNotHaveFocus";
        if(code == 102) return "VRCompositorError_InvalidTexture";
        if(code == 103) return "VRCompositorError_IsNotSceneApplication";
        if(code == 104) return "VRCompositorError_TextureIsOnWrongDevice";
        if(code == 105) return "VRCompositorError_TextureUsesUnsupportedFormat";
        if(code == 106) return "VRCompositorError_SharedTexturesNotSupported";
        if(code == 107) return "VRCompositorError_IndexOutOfRange";
        if(code == 108) return "VRCompositorError_AlreadySubmitted";
        if(code == 109) return "VRCompositorError_InvalidBounds";
        if(code == 110) return "VRCompositorError_AlreadySet";
        return "Unknown";
    }


}
