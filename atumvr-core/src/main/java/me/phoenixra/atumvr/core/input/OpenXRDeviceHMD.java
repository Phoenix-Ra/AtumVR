package me.phoenixra.atumvr.core.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.input.devices.VRDeviceHMD;
import me.phoenixra.atumvr.core.OpenXRHelper;
import me.phoenixra.atumvr.core.OpenXRProvider;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

public class OpenXRDeviceHMD extends OpenXRDevice implements VRDeviceHMD {

    private final Matrix4f eyeLeftPose = new Matrix4f();

    private final Matrix4f eyeRightPose = new Matrix4f();

    public OpenXRDeviceHMD(OpenXRProvider provider) {
        super(provider, "hmd");
    }

    public void initSpace(MemoryStack stack){
        XrPosef identity = XrPosef.calloc(stack)
                .set(
                        XrQuaternionf.calloc(stack).set(0, 0, 0, 1),
                        XrVector3f.calloc(stack).set(0f, 0f, 0f)
                );
        space = OpenXRHelper.createReferenceSpace(
                provider.getVrState(),
                XR10.XR_REFERENCE_SPACE_TYPE_VIEW,
                identity,
                stack
        );
    }

    @Override
    protected void onUpdate(long predictedTime, MemoryStack stack) {
        XrPosef p = getXrView(EyeType.LEFT).pose();
        eyeLeftPose.identity()
                .translate(p.position$().x(), p.position$().y(), p.position$().z())
                .rotate(p.orientation().x(), p.orientation().y(), p.orientation().z(), p.orientation().w());

        p = getXrView(EyeType.RIGHT).pose();
        eyeRightPose.identity()
                .translate(p.position$().x(), p.position$().y(), p.position$().z())
                .rotate(p.orientation().x(), p.orientation().y(), p.orientation().z(), p.orientation().w());
    }

    @Override
    public @NotNull Matrix4f getEyePose(@NotNull EyeType eyeType) {
        if(eyeType == EyeType.LEFT){
            return eyeLeftPose;
        }else{
            return eyeRightPose;
        }
    }

    public XrView getXrView(EyeType eyeType){
        return provider.getVrState().getXrSwapChain()
                .getXrViewBuffer().get(eyeType.getId());
    }
}
