package me.phoenixra.atumvr.api.scene.impl;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.VRFrameBuffer;
import me.phoenixra.atumvr.api.rendering.VRTexture;
import me.phoenixra.atumvr.api.scene.EyeType;
import me.phoenixra.atumvr.api.scene.VRSceneRenderer;
import me.phoenixra.atumvr.api.scene.camera.VRCamera;
import me.phoenixra.atumvr.api.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openvr.VR.EVREye_Eye_Left;
import static org.lwjgl.openvr.VR.EVREye_Eye_Right;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_PostPresentHandoff;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_Submit;

public abstract class BaseVRSceneRenderer implements VRSceneRenderer {

    @Getter
    private VRApp vrApp;


    @Getter
    private int resolutionWidth;
    @Getter
    private int resolutionHeight;


    @Getter
    private VRFrameBuffer frameBufferRightEye;
    @Getter
    private VRFrameBuffer frameBufferLeftEye;


    private long windowId;


    @Getter
    private VRCamera vrCameraRightEye;
    @Getter
    private VRCamera vrCameraLeftEye;

    public BaseVRSceneRenderer(VRApp vrApp) {
        this.vrApp = vrApp;

    }

    public abstract void updateEyeTexture(@NotNull EyeType eyeType);

    public abstract void onInit();

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            setupGLContext();

            vrCameraLeftEye = new VRCamera(getVrApp().getVrCore(),new Vector3f(),new Quaternionf());
            vrCameraRightEye = new VRCamera(getVrApp().getVrCore(),new Vector3f(),new Quaternionf());
            setupMvp(stack);

            setupResolution(stack);
            setupFrameBuffers();
            onInit();
        }
    }

    @Override
    public void updateFrame() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glViewport(0, 0, resolutionWidth, resolutionHeight);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            //update mvp variable
            setupMvp(stack);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferLeftEye.getFrameBufferId());
            GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            updateEyeTexture(EyeType.LEFT);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferRightEye.getFrameBufferId());
            GL30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            updateEyeTexture(EyeType.RIGHT);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);


            //@TODO test update without creating new texture each time
            VRCompositor_Submit(EVREye_Eye_Left,
                    frameBufferLeftEye.getVrTexture().applyDataToTexture(
                            Texture.malloc(stack)
                    ),
                    null,
                    0
            );

            VRCompositor_Submit(EVREye_Eye_Right,
                    frameBufferRightEye.getVrTexture().applyDataToTexture(
                            Texture.malloc(stack)
                    ),
                    null,
                    0
            );
            GL30.glFlush();
            GL30.glFinish();
            VRCompositor_PostPresentHandoff();
        }

    }


    @Override
    public void destroy() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);

        glfwTerminate();
    }


    private void setupGLContext() {
        GLFWErrorCallback.createPrint(System.out).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }


        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_DEPTH_BITS, 24);
        glfwWindowHint(GLFW_STENCIL_BITS, 8);

        windowId = glfwCreateWindow(640, 480, getVrApp().getVrCore().getName(), 0L, 0L);
        if (windowId == 0L) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(windowId);
        glfwSwapInterval(1);

        GL.createCapabilities();
        GL30.glEnable(GL30.GL_DEPTH_TEST);

        //texture staff, better be moved to a renderers of objects
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);

    }

    private void setupResolution(MemoryStack stack) {
        IntBuffer widthBuffer = stack.mallocInt(1);
        IntBuffer heightBuffer = stack.mallocInt(1);
        VRSystem.VRSystem_GetRecommendedRenderTargetSize(widthBuffer, heightBuffer);

        resolutionWidth = widthBuffer.get(0);
        resolutionHeight = heightBuffer.get(0);
    }

    private void setupFrameBuffers() {
        VRTexture textureLeft = new VRTexture(
                resolutionWidth,
                resolutionHeight,
                false
        );
        frameBufferLeftEye = new VRFrameBuffer(textureLeft);

        VRTexture textureRight = new VRTexture(
                resolutionWidth,
                resolutionHeight,
                false
        );
        frameBufferRightEye = new VRFrameBuffer(textureRight);

    }

    private void setupMvp(MemoryStack stack) {
        vrCameraLeftEye.setupProjectionMatrix(EyeType.LEFT,stack);
        vrCameraLeftEye.setupViewMatrix(EyeType.LEFT,stack);

        vrCameraRightEye.setupProjectionMatrix(EyeType.RIGHT,stack);
        vrCameraRightEye.setupViewMatrix(EyeType.RIGHT,stack);
    }

}
