package me.phoenixra.atumvr.core.openvr.rendering;

import lombok.Getter;
import me.phoenixra.atumconfig.api.tuples.PairRecord;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
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

public abstract class BuiltInOpenVRRenderer implements VRRenderer {
    @Getter
    private VRApp vrApp;


    @Getter
    protected int resolutionWidth;
    @Getter
    protected int resolutionHeight;


    @Getter
    private OpenVRTexture textureLeftEye;
    @Getter
    private OpenVRTexture textureRightEye;

    private final HashMap<EyeType, float[]> hiddenArea = new HashMap<>();

    public BuiltInOpenVRRenderer(VRApp vrApp) {
        this.vrApp = vrApp;

    }


    public abstract void onInit() throws Throwable;

    @Override
    public void init() throws Throwable{
        try (MemoryStack stack = MemoryStack.stackPush()) {
            setupResolution(stack);
            setupHiddenArea(stack);
            PairRecord<OpenVRTexture, OpenVRTexture> eyes = setupEyes();
            textureLeftEye = eyes.getFirst();
            textureRightEye = eyes.getSecond();
            onInit();
        }
    }

    @Override
    public void renderFrame() {
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
        textureLeftEye.destroy();
        textureRightEye.destroy();
    }

    protected void setupResolution(MemoryStack stack) {
        IntBuffer widthBuffer = stack.mallocInt(1);
        IntBuffer heightBuffer = stack.mallocInt(1);
        VRSystem.VRSystem_GetRecommendedRenderTargetSize(widthBuffer, heightBuffer);

        resolutionWidth = widthBuffer.get(0);
        resolutionHeight = heightBuffer.get(0);
    }

    protected PairRecord<OpenVRTexture, OpenVRTexture> setupEyes() {
        OpenVRTexture textureLeft = new OpenVRTexture(
                getResolutionWidth(),
                getResolutionHeight(),
                false
        ).init(false);

        OpenVRTexture textureRight = new OpenVRTexture(
                getResolutionWidth(),
                getResolutionHeight(),
                false
        ).init(false);
        return new PairRecord<>(textureLeft, textureRight);

    }

    protected void setupHiddenArea(MemoryStack stack) {
        for (int eye = 0; eye < 2; ++eye) {
            HiddenAreaMesh areaMesh = HiddenAreaMesh.malloc(stack);
            VRSystem_GetHiddenAreaMesh(eye, 0, areaMesh);
            int triangleCount = areaMesh.unTriangleCount();

            if (triangleCount <= 0) {
                getVrApp().logInfo("No stencil mesh found for eye " + eye);
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

    @Override
    public float[] getHiddenAreaVertices(EyeType eyeType) {
        return hiddenArea.get(eyeType);
    }
}
