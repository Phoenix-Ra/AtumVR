package me.phoenixra.atumvr.core.openvr.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.rendering.VRCamera;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.api.rendering.VRScene;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public abstract class OpenVRScene implements VRScene {

    @Getter
    private VRRenderer vrRenderer;


    @Getter
    protected VRCamera rightEyeCamera;
    @Getter
    protected VRCamera leftEyeCamera;

    public OpenVRScene(VRRenderer vrRenderer) {
        this.vrRenderer = vrRenderer;

    }

    public abstract void updateEyeTexture(@NotNull EyeType eyeType);

    public abstract void onInit();

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            leftEyeCamera = new OpenVRCamera(
                    getVrRenderer().getVrApp().getVrProvider(),
                    new Vector3f(),new Quaternionf()
            );
            rightEyeCamera = new OpenVRCamera(
                    getVrRenderer().getVrApp().getVrProvider(),
                    new Vector3f(),new Quaternionf()
            );
            setupMvp();
            onInit();
        }
    }

    @Override
    public void prepareFrame() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            //update mvp variable
            setupMvp();

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, getVrRenderer().getTextureLeftEye().getFrameBufferId());
            GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            updateEyeTexture(EyeType.LEFT);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, getVrRenderer().getTextureRightEye().getFrameBufferId());
            GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            updateEyeTexture(EyeType.RIGHT);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }

    }




    protected void setupMvp() {
        leftEyeCamera.updateProjectionMatrix(EyeType.LEFT,
                0.02f,100f
        );
        leftEyeCamera.updateViewMatrix(EyeType.LEFT);

        rightEyeCamera.updateProjectionMatrix(EyeType.RIGHT,
                0.02f,100f
        );
        rightEyeCamera.updateViewMatrix(EyeType.RIGHT);
    }

}
