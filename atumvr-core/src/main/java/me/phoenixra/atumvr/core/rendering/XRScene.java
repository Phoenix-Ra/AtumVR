package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.rendering.VRRenderContext;
import me.phoenixra.atumvr.api.rendering.VRScene;
import me.phoenixra.atumvr.api.enums.EyeType;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

/**
 * Abstract base class for XR scene rendering.
 *
 * @see XRRenderer
 * @see XREyeCamera
 */
@Getter
public abstract class XRScene implements VRScene {

    private final XRRenderer renderer;


    protected XREyeCamera rightEyeCamera;
    protected XREyeCamera leftEyeCamera;

    public XRScene(@NotNull XRRenderer renderer) {
        this.renderer = renderer;

    }

    /**
     * Renders scene content for the specified eye.
     * <p>
     * Called once per eye during the render loop with the appropriate
     * frameBuffer already bound.
     * </p>
     *
     * @param eyeType the eye being rendered
     */
    protected abstract void renderEyeTexture(@NotNull EyeType eyeType);

    /**
     * On scene initialized
     */
    protected abstract void onInit();



    @Override
    public void init() {
        leftEyeCamera = new XREyeCamera(
                renderer.getVrProvider()
        );
        rightEyeCamera = new XREyeCamera(
                renderer.getVrProvider()
        );
        setupCamera();
        onInit();
    }


    @Override
    public void render(@NotNull VRRenderContext context) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            //stack not used here but necessary for some OpenGL operations,
            // so its pushed then auto-popped together with OpenGL staff
            // that might be there during rendering

            setupCamera();

            for (EyeType eyeType : EyeType.values()) {

                int fbo = (eyeType == EyeType.LEFT)
                        ? renderer.getTextureLeftEye().getFrameBufferId()
                        : renderer.getTextureRightEye().getFrameBufferId();
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);


                GL30.glClearColor(0, 0, 0, 1);
                GL30.glClear(
                        GL30.GL_COLOR_BUFFER_BIT |
                                GL30.GL_DEPTH_BUFFER_BIT |
                                GL30.GL_STENCIL_BUFFER_BIT
                );

                renderEyeTexture(eyeType);
            }

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }
    }


    /**
     * Setup camera for each eye
     */
    protected void setupCamera() {
        leftEyeCamera.updateProjectionMatrix(EyeType.LEFT,
                0.02f,100f

        );
        leftEyeCamera.updateViewMatrix(EyeType.LEFT);

        rightEyeCamera.updateProjectionMatrix(EyeType.RIGHT,
                0.02f,100f
        );
        rightEyeCamera.updateViewMatrix(EyeType.RIGHT);
    }


    @Override
    public void destroy(){

    }
}
