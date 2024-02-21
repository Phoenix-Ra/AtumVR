package me.phoenixra.atumvr.api.misc;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.utils.MathUtils;
import org.joml.Quaternionf;
import org.lwjgl.openvr.HmdMatrix34;

@Getter @Setter
public class VRLocation {
    private Quaternionf rotation;
    private float posX;
    private float posY;
    private float posZ;


    public VRLocation(Quaternionf quaternionf,
                      float offsetX, float offsetY, float offsetZ) {

        this(
                quaternionf.w,
                quaternionf.x,quaternionf.y,quaternionf.z,
                offsetX,offsetY,offsetZ
        );
    }
    public VRLocation(float scalar,
                      float axisX, float axisY, float axisZ,
                      float posX, float posY, float posZ) {
        this.rotation = new Quaternionf(axisX,axisY,axisZ,scalar);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;

        this.rotation.normalize();

    }
    public VRLocation(float yaw, float pitch, float roll,
                      float posX, float posY, float posZ) {

        setRotationFromEuler(yaw,pitch,roll);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;

        this.rotation.normalize();

    }
    public VRLocation(HmdMatrix34 matrix) {
        setFromVrMatrix(matrix);
    }


    public void setRotationFromEuler(float yaw, float pitch, float roll){
        float cy = (float) MathUtils.fastCos(yaw * 0.5);
        float sy = (float)MathUtils.fastSin(yaw * 0.5);
        float cp = (float)MathUtils.fastCos(pitch * 0.5);
        float sp = (float)MathUtils.fastSin(pitch * 0.5);
        float cr = (float)MathUtils.fastCos(roll * 0.5);
        float sr = (float)MathUtils.fastSin(roll * 0.5);

        float scalar = cr * cy * cp + sr * sy * sp;
        float axisX = sr * cy * cp - cr * sy * sp;
        float axisY = cr * sy * cp + sr * cy * sp;
        float axisZ = cr * cy * sp - sr * sy * cp;

        this.rotation = new Quaternionf(axisX,axisY,axisZ,scalar);
    }




    public void setFromVrMatrix(HmdMatrix34 matrix){
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

        this.rotation = new Quaternionf(
                axisX,axisY,axisZ,scalar
        );
        this.posX = matrix.m(3);
        this.posY = matrix.m(7);
        this.posZ = matrix.m(11);
    }

    public float[] toMatrix() {
        float scalar = rotation.w;
        float axisX = rotation.x;
        float axisY = rotation.y;
        float axisZ = rotation.z;

        float[] matrix = new float[12];
        matrix[0] = 1.0f - 2.0f * (axisY * axisY + axisZ * axisZ);
        matrix[1] = 2.0f * (axisX * axisY - axisZ * scalar);
        matrix[2] = 2.0f * (axisX * axisZ + axisY * scalar);
        matrix[3] = posX;

        matrix[4] = 2.0f * (axisX * axisY + axisZ * scalar);
        matrix[5] = 1.0f - 2.0f * (axisX * axisX + axisZ * axisZ);
        matrix[6] = 2.0f * (axisY * axisZ - axisX * scalar);
        matrix[7] = posY;

        matrix[8] = 2.0f * (axisX * axisZ - axisY * scalar);
        matrix[9] = 2.0f * (axisY * axisZ + axisX * scalar);
        matrix[10] = 1.0f - 2.0f * (axisX * axisX + axisY * axisY);
        matrix[11] = posZ;
        return matrix;
    }

}
