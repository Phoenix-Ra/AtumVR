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
}
