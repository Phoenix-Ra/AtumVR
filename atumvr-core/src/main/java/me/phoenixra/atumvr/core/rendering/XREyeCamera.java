package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.device.XRDeviceHMD;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.openxr.XrFovf;
import org.lwjgl.openxr.XrPosef;

/**
 * Represents a virtual camera for one eye in VR stereo rendering
 *
 * @see XRScene
 */
public class XREyeCamera {
    private final XRProvider vrProvider;



    @Getter
    protected final Matrix4f viewMatrix = new Matrix4f();

    @Getter
    protected final Matrix4f projectionMatrix = new Matrix4f();

    private final Quaternionf scratchOrientation = new Quaternionf();
    private final Vector3f scratchPosition = new Vector3f();


    public XREyeCamera(XRProvider vrProvider) {
        this.vrProvider = vrProvider;
    }

    public void updateViewMatrix(EyeType eyeType) {
        XrPosef p = vrProvider.getInputHandler()
                .getDevice(XRDeviceHMD.ID, XRDeviceHMD.class)
                .getXrView(eyeType).pose();
        scratchOrientation.set(
                p.orientation().x(),
                p.orientation().y(),
                p.orientation().z(),
                p.orientation().w()
        ).conjugate();

        scratchPosition.set(
                p.position$().x(),
                p.position$().y(),
                p.position$().z()
        );

        viewMatrix.identity()
                .rotate(scratchOrientation)
                .translate(-scratchPosition.x, -scratchPosition.y, -scratchPosition.z);
    }

    public void updateProjectionMatrix(EyeType eyeType,
                                       float nearClip, float farClip) {
        XrFovf fov = vrProvider.getInputHandler()
                .getDevice(XRDeviceHMD.ID, XRDeviceHMD.class)
                .getXrView(eyeType).fov();

        projectionMatrix.setPerspectiveOffCenterFov(
                fov.angleLeft(),
                fov.angleRight(),
                fov.angleDown(),
                fov.angleUp(),
                nearClip,
                farClip
        );
    }
}
