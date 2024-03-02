package me.phoenixra.atumvr.api.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.texture.VRFrameBuffer;
import me.phoenixra.atumvr.api.rendering.texture.VRTexture;
import org.lwjgl.opengl.GL30;

import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;


import java.nio.IntBuffer;

import static org.lwjgl.openvr.VR.EVREye_Eye_Left;
import static org.lwjgl.openvr.VR.EVREye_Eye_Right;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_PostPresentHandoff;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_Submit;

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

    public BuiltInVRRenderer(VRApp vrApp) {
        this.vrApp = vrApp;

    }


    public abstract void onInit();

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            setupResolution(stack);
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
}
