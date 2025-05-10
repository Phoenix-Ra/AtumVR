package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.rendering.VREyeCamera;
import me.phoenixra.atumvr.api.rendering.VRScene;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public abstract class OpenXRScene implements VRScene {

    @Getter
    private OpenXRRenderer vrRenderer;



    @Getter
    protected VREyeCamera rightEyeCamera;
    @Getter
    protected VREyeCamera leftEyeCamera;

    public OpenXRScene(OpenXRRenderer vrRenderer) {
        this.vrRenderer = vrRenderer;

    }

    public abstract void updateEyeTexture(@NotNull EyeType eyeType);

    public abstract void onInit();

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            leftEyeCamera = new OpenXREyeCamera(
                    vrRenderer.getVrProvider(),
                    new Vector3f(),new Quaternionf()
            );
            rightEyeCamera = new OpenXREyeCamera(
                    vrRenderer.getVrProvider(),
                    new Vector3f(),new Quaternionf()
            );
            setupMvp(stack);
            onInit();
        }
    }

    @Override
    public void prepareFrame() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            //update mvp variable
            setupMvp(stack);

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




    protected void setupMvp(MemoryStack stack) {
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
