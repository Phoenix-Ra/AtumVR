package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.rendering.VRCamera;

import me.phoenixra.atumvr.core.OpenXRProvider;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.openxr.XrFovf;
import org.lwjgl.openxr.XrPosef;

public class OpenXRCamera implements VRCamera {
    private OpenXRProvider vrProvider;

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


    public OpenXRCamera(OpenXRProvider vrProvider, Vector3f location, Quaternionf rotation) {
        this.vrProvider = vrProvider;
        this.location = location;
        this.rotation = rotation;
    }

    public void updateViewMatrix(EyeType eyeType) {
        XrPosef p = vrProvider.getXrView(eyeType).pose();
        Quaternionf q = new Quaternionf(
                p.orientation().x(),
                p.orientation().y(),
                p.orientation().z(),
                p.orientation().w()
        ).conjugate();

        Vector3f pos = new Vector3f(
                p.position$().x(),
                p.position$().y(),
                p.position$().z()
        );

        viewMatrix.identity()
                .rotate(q)
                .translate(-pos.x, -pos.y, -pos.z);
    }

    public void updateProjectionMatrix(EyeType eyeType,
                                       float nearClip, float farClip) {
        XrFovf fov = vrProvider.getXrView(eyeType).fov();

        projectionMatrix =  new Matrix4f()
                .setPerspectiveOffCenterFov(
                        fov.angleLeft(),
                        fov.angleRight(),
                        fov.angleDown(),
                        fov.angleUp(),
                        nearClip,
                        farClip
                );
    }
}
