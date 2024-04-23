package me.phoenixra.atumvr.api.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.rendering.texture.VRFrameBuffer;
import me.phoenixra.atumvr.api.rendering.texture.VRTexture;
import org.lwjgl.opengl.GL30;

import org.lwjgl.openvr.HiddenAreaMesh;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;


import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.openvr.VR.EVREye_Eye_Left;
import static org.lwjgl.openvr.VR.EVREye_Eye_Right;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_PostPresentHandoff;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_Submit;
import static org.lwjgl.openvr.VRSystem.VRSystem_GetHiddenAreaMesh;

public abstract class BuiltInVRRenderer implements VRRenderer {
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

    private final HashMap<EyeType, float[]> hiddenArea = new HashMap<>();

    public BuiltInVRRenderer(VRApp vrApp) {
        this.vrApp = vrApp;

    }


    public abstract void onInit();

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            setupResolution(stack);
            setupHiddenArea();
            setupEyes();
            onInit();
        }
    }

    @Override
    public void updateFrame() {
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
        frameBufferLeftEye.destroy();
        frameBufferRightEye.destroy();
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

    protected void setupHiddenArea() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            for (int eye = 0; eye < 2; ++eye) {
                HiddenAreaMesh areaMesh = HiddenAreaMesh.malloc(stack);
                VRSystem_GetHiddenAreaMesh(eye, 0, areaMesh);
                int triangleCount = areaMesh.unTriangleCount();

                if (triangleCount <= 0) {
                    getVrApp().getVrCore().logInfo("No stencil mesh found for eye " + eye);
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
        return hiddenArea.get(eyeType);
    }
}
