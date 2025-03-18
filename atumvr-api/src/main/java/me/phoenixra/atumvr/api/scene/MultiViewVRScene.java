package me.phoenixra.atumvr.api.scene;

import lombok.Getter;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.api.scene.camera.VRCamera;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public abstract class MultiViewVRScene implements VRScene {

    @Getter
    private VRRenderer vrRenderer;


    @Getter
    protected VRCamera vrCameraRightEye;
    @Getter
    protected VRCamera vrCameraLeftEye;

    public MultiViewVRScene(VRRenderer vrRenderer) {
        this.vrRenderer = vrRenderer;

    }

    public abstract void render();

    public abstract void onInit();

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            vrCameraLeftEye = new VRCamera(getVrRenderer().getVrApp().getVrCore(),new Vector3f(),new Quaternionf());
            vrCameraRightEye = new VRCamera(getVrRenderer().getVrApp().getVrCore(),new Vector3f(),new Quaternionf());
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
            render();

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }

    }




    protected void setupMvp(MemoryStack stack) {
        vrCameraLeftEye.setupProjectionMatrix(EyeType.LEFT,
                0.02f,100f,
                stack
        );
        vrCameraLeftEye.setupViewMatrix(EyeType.LEFT,stack);

        vrCameraRightEye.setupProjectionMatrix(EyeType.RIGHT,
                0.02f,100f,
                stack
        );
        vrCameraRightEye.setupViewMatrix(EyeType.RIGHT,stack);
    }
}
