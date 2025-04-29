package me.phoenixra.atumvr.core.openvr.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.HiddenAreaMesh;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openvr.VR.EVREye_Eye_Left;
import static org.lwjgl.openvr.VR.EVREye_Eye_Right;
import static org.lwjgl.openvr.VRCompositor.*;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetHiddenAreaMesh;

public abstract class OpenVRRenderer implements VRRenderer {
    @Getter
    private VRApp vrApp;


    @Getter
    protected int resolutionWidth;
    @Getter
    protected int resolutionHeight;


    @Getter
    protected OpenVRTexture textureLeftEye;
    @Getter
    protected OpenVRTexture textureRightEye;


    private final HashMap<EyeType, float[]> hiddenArea = new HashMap<>();


    @Getter
    protected long windowHandle;

    public OpenVRRenderer(VRApp vrApp) {
        this.vrApp = vrApp;

    }

    public abstract void onInit() throws Throwable;

    @Override
    public void init() throws Throwable{
        try (MemoryStack stack = MemoryStack.stackPush()) {
            setupGLContext();
            setupResolution(stack);
            setupHiddenArea();
            setupEyes();
            onInit();
        }
    }

    @Override
    public void renderFrame() {
        GL30.glViewport(0, 0, resolutionWidth, resolutionHeight);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        getCurrentScene().prepareFrame();

        VRCompositor_Submit(EVREye_Eye_Left,
                textureLeftEye.getOpenVrTexture(),
                null,
                0
        );
        VRCompositor_Submit(EVREye_Eye_Right,
                textureRightEye.getOpenVrTexture(),
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
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

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

        windowHandle = glfwCreateWindow(640, 480, getVrApp().getVrProvider().getName(), 0L, 0L);
        if (windowHandle == 0L) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(windowHandle);
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

        resolutionWidth = getVrApp().getVrProvider().getEyeTextureWidth();
        resolutionHeight = getVrApp().getVrProvider().getEyeTextureHeight();
    }

    protected void setupEyes() {
        textureLeftEye = new OpenVRTexture(
                resolutionWidth,
                resolutionHeight,
                false
        );

        textureRightEye = new OpenVRTexture(
                resolutionWidth,
                resolutionHeight,
                false
        );

    }

    protected void setupHiddenArea() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            for (int eye = 0; eye < 2; ++eye) {
                HiddenAreaMesh areaMesh = HiddenAreaMesh.malloc(stack);
                VRSystem_GetHiddenAreaMesh(eye, 0, areaMesh);
                int triangleCount = areaMesh.unTriangleCount();

                if (triangleCount <= 0) {
                    getVrApp().getVrProvider().logInfo("No stencil mesh found for eye " + eye);
                } else {
                    float[] area = new float[areaMesh.unTriangleCount() * 3 * 2];
                    MemoryUtil.memFloatBuffer(
                            MemoryUtil.memAddress(
                                    areaMesh.pVertexData()
                            ),
                            area.length
                    ).get(area);

                    for (int vertex = 0; vertex < area.length; vertex += 2) {
                        area[vertex] *= (float) getResolutionWidth();
                        area[vertex + 1] *= (float) getResolutionHeight();
                    }
                    hiddenArea.put(EyeType.fromInt(eye),
                            area
                    );

                    System.out.println("Stencil mesh loaded for eye " + eye);
                }
            }
        }
    }
    @Override
    public float[] getHiddenAreaVertices(EyeType eyeType) {
        return new float[0];
    }
}
