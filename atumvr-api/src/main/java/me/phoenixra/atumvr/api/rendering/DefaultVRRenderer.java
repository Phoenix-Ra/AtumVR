package me.phoenixra.atumvr.api.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.texture.VRFrameBuffer;
import me.phoenixra.atumvr.api.rendering.texture.VRTexture;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openvr.VR.EVREye_Eye_Left;
import static org.lwjgl.openvr.VR.EVREye_Eye_Right;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_PostPresentHandoff;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_Submit;

public abstract class DefaultVRRenderer implements VRRenderer{
    @Getter
    private VRApp vrApp;


    @Getter
    protected int resolutionWidth;
    @Getter
    protected int resolutionHeight;


    @Getter
    protected VRFrameBuffer frameBufferRightEye;
    @Getter
    protected VRFrameBuffer frameBufferLeftEye;


    protected long windowId;
    public DefaultVRRenderer(VRApp vrApp) {
        this.vrApp = vrApp;

    }

    public abstract void onInit();

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            setupGLContext();
            setupResolution(stack);
            setupEyes();
            onInit();
        }
    }

    @Override
    public void updateFrame() {
        GL30.glViewport(0, 0, resolutionWidth, resolutionHeight);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        getCurrentScene().prepareFrame();

        VRCompositor_Submit(EVREye_Eye_Left,
                frameBufferLeftEye.getVrTexture().getTexture(),
                null,
                0
        );

        VRCompositor_Submit(EVREye_Eye_Right,
                frameBufferRightEye.getVrTexture().getTexture(),
                null,
                0
        );
        GL30.glFlush();
        GL30.glFinish();
        VRCompositor_PostPresentHandoff();

    }


    @Override
    public void destroy() {
        getCurrentScene().destroy();
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);

        glfwTerminate();
    }


    protected void setupGLContext() {
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

    protected void setupResolution(MemoryStack stack) {
        IntBuffer widthBuffer = stack.mallocInt(1);
        IntBuffer heightBuffer = stack.mallocInt(1);
        VRSystem.VRSystem_GetRecommendedRenderTargetSize(widthBuffer, heightBuffer);

        resolutionWidth = widthBuffer.get(0);
        resolutionHeight = heightBuffer.get(0);
    }

    protected void setupEyes() {
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
}
