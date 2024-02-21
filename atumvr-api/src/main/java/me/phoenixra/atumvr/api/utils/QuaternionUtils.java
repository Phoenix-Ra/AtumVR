package me.phoenixra.atumvr.api.utils;

import org.joml.Quaternionf;
import org.lwjgl.openvr.HmdMatrix34;

public class QuaternionUtils {


    public static Quaternionf fromVRMatrix3f(HmdMatrix34 matrix) {

        // Elements of the matrix
        float m00 = matrix.m(0);
        float m01 = matrix.m(1);
        float m02 = matrix.m(2);
        float m10 = matrix.m(4);
        float m11 = matrix.m(5);
        float m12 = matrix.m(6);
        float m20 = matrix.m(8);
        float m21 = matrix.m(9);
        float m22 = matrix.m(10);

        // Compute the four squared components of the quaternion
        float fourXSquaredMinus1 = m00 - m11 - m22;
        float fourYSquaredMinus1 = m11 - m00 - m22;
        float fourZSquaredMinus1 = m22 - m00 - m11;
        float fourWSquaredMinus1 = m00 + m11 + m22;

        // Find the largest of the four values
        int biggestIndex = 0;
        float fourBiggestSquaredMinus1 = fourWSquaredMinus1;
        if (fourXSquaredMinus1 > fourBiggestSquaredMinus1) {
            fourBiggestSquaredMinus1 = fourXSquaredMinus1;
            biggestIndex = 1;
        }
        if (fourYSquaredMinus1 > fourBiggestSquaredMinus1) {
            fourBiggestSquaredMinus1 = fourYSquaredMinus1;
            biggestIndex = 2;
        }
        if (fourZSquaredMinus1 > fourBiggestSquaredMinus1) {
            fourBiggestSquaredMinus1 = fourZSquaredMinus1;
            biggestIndex = 3;
        }

        // Compute the largest component of the quaternion
        float biggestVal = (float)Math.sqrt(fourBiggestSquaredMinus1 + 1) * 0.5f;
        float mult = 0.25f / biggestVal;

        float scalar = 0, axisX = 0, axisY = 0, axisZ = 0;
        // Compute the other components depending on which case we're in
        switch (biggestIndex) {
            case 0: // W is the largest component
                scalar = biggestVal;
                axisX = (m21 - m12) * mult;
                axisY = (m02 - m20) * mult;
                axisZ = (m10 - m01) * mult;
                break;
            case 1: // X is the largest component
                scalar = (m21 - m12) * mult;
                axisX = biggestVal;
                axisY = (m01 + m10) * mult;
                axisZ = (m02 + m20) * mult;
                break;
            case 2: // Y is the largest component
                scalar = (m02 - m20) * mult;
                axisX = (m01 + m10) * mult;
                axisY = biggestVal;
                axisZ = (m12 + m21) * mult;
                break;
            case 3: // Z is the largest component
                scalar = (m10 - m01) * mult;
                axisX = (m02 + m20) * mult;
                axisY = (m12 + m21) * mult;
                axisZ = biggestVal;
                break;
        }

        // Optionally, update the Euler angles to match
        return new Quaternionf(
                axisX,
                axisY,
                axisZ,
                scalar
        );
    }
}
