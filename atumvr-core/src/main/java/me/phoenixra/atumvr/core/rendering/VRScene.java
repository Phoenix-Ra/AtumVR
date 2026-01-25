package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.core.enums.EyeType;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

/**
 * Abstract base class for VR scene rendering.
 *
 * @see VRRenderer
 * @see VREyeCamera
 */
@Getter
public abstract class VRScene {

    private final VRRenderer renderer;


    protected VREyeCamera rightEyeCamera;
    protected VREyeCamera leftEyeCamera;

    public VRScene(@NotNull VRRenderer renderer) {
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
    public abstract void renderEyeTexture(@NotNull EyeType eyeType);

    /**
     * On scene initialized
     */
    public abstract void onInit();


    /**
     * Initialize scene
     */
    public void init() {
        leftEyeCamera = new VREyeCamera(
                renderer.getVrProvider()
        );
        rightEyeCamera = new VREyeCamera(
                renderer.getVrProvider()
        );
        setupCamera();
        onInit();
    }

    /**
     * Render scene
     *
     * @param context the render context
     */
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

    /**
     * Destroy VR scene and release all associated resources
     */
    public void destroy(){

    }
}
