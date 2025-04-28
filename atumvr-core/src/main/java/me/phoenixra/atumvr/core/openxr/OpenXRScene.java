package me.phoenixra.atumvr.core.openxr;

import lombok.Getter;
import me.phoenixra.atumvr.api.provider.openvr.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.provider.openvr.rendering.VRRenderer;
import me.phoenixra.atumvr.api.provider.openvr.scene.VRScene;
import me.phoenixra.atumvr.api.provider.openxr.OpenXRCamera;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public abstract class OpenXRScene implements VRScene {

    @Getter
    private VRRenderer vrRenderer;


    private OpenXRProvider openXRProvider;
    @Getter
    protected OpenXRCamera openVrCameraRightEye;
    @Getter
    protected OpenXRCamera openVrCameraLeftEye;

    public OpenXRScene(VRRenderer vrRenderer) {
        this.vrRenderer = vrRenderer;

    }

    public abstract void updateEyeTexture(@NotNull EyeType eyeType);

    public abstract void onInit();

    @Override
    public void init() {
        openXRProvider = (OpenXRProvider) getVrRenderer().getVrApp().getVrProvider();
        try (MemoryStack stack = MemoryStack.stackPush()) {

            openVrCameraLeftEye = new OpenXRCamera(
                    getVrRenderer().getVrApp().getVrProvider(),
                    new Vector3f(),new Quaternionf()
            );
            openVrCameraRightEye = new OpenXRCamera(
                    getVrRenderer().getVrApp().getVrProvider(),
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
        openVrCameraLeftEye.setupProjectionMatrix(EyeType.LEFT,
                0.02f,100f, openXRProvider.viewBuffer

        );
        openVrCameraLeftEye.setupViewMatrix(EyeType.LEFT, openXRProvider.viewBuffer);

        openVrCameraRightEye.setupProjectionMatrix(EyeType.RIGHT,
                0.02f,100f, openXRProvider.viewBuffer
        );
        openVrCameraRightEye.setupViewMatrix(EyeType.RIGHT, openXRProvider.viewBuffer);
    }

}
