package me.phoenixra.atumvr.api.scene.camera;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;

public class VRCamera {
    private VRCore vrCore;

    /**
     * Camera's location.
     */
    @Getter @Setter
    protected Vector3f location;

    /**
     * The orientation of the camera.
     */
    @Getter @Setter
    protected Quaternionf rotation;


    @Getter
    protected Matrix4f viewMatrix = new Matrix4f();

    @Getter
    protected Matrix4f projectionMatrix = new Matrix4f();


    public VRCamera(VRCore vrCore, Vector3f location, Quaternionf rotation){
        this.vrCore = vrCore;
        this.location = location;
        this.rotation = rotation;
    }
    public void setupViewMatrix(EyeType eyeType, MemoryStack stack) {
        HmdMatrix34 hmdMatrix34 = HmdMatrix34.malloc(stack);
        VRSystem.VRSystem_GetEyeToHeadTransform(
                eyeType.getId(),
                hmdMatrix34
        );
        Matrix4f eyeToHead = new Matrix4f(
                hmdMatrix34.m(0), hmdMatrix34.m(1),
                hmdMatrix34.m(2), hmdMatrix34.m(3),

                hmdMatrix34.m(4), hmdMatrix34.m(5),
                hmdMatrix34.m(6), hmdMatrix34.m(7),

                hmdMatrix34.m(8), hmdMatrix34.m(9),
                hmdMatrix34.m(10), hmdMatrix34.m(11),

                0.0f, 0.0f, 0.0f, 1.0f
        );



        viewMatrix = eyeToHead.mul(
                vrCore.getDevicesManager().getHMD().getPose().getLocationMatrix()
        ).invert();

    }

    public void setupProjectionMatrix(EyeType eyeType,
                                      float nearClip, float farClip,
                                      MemoryStack stack) {
        HmdMatrix44 hmdMatrix44 = HmdMatrix44.malloc(stack);
        VRSystem.VRSystem_GetProjectionMatrix(
                eyeType.getId(),
                nearClip,
                farClip,
                hmdMatrix44
        );
        projectionMatrix = new Matrix4f(
                hmdMatrix44.m(0), hmdMatrix44.m(1),
                hmdMatrix44.m(2), hmdMatrix44.m(3),

                hmdMatrix44.m(4), hmdMatrix44.m(5),
                hmdMatrix44.m(6), hmdMatrix44.m(7),

                hmdMatrix44.m(8), hmdMatrix44.m(9),
                hmdMatrix44.m(10), hmdMatrix44.m(11),

                hmdMatrix44.m(12), hmdMatrix44.m(13),
                hmdMatrix44.m(14), hmdMatrix44.m(15)
        );
    }

}
