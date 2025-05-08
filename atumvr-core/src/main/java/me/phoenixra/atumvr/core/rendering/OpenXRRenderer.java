package me.phoenixra.atumvr.core.rendering;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.api.rendering.VRTexture;
import me.phoenixra.atumvr.api.utils.GLUtils;
import me.phoenixra.atumvr.core.OpenXRProvider;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public abstract class OpenXRRenderer implements VRRenderer {
    @Getter
    protected OpenXRProvider vrProvider;


    @Getter
    protected int resolutionWidth;
    @Getter
    protected int resolutionHeight;




    protected XrCompositionLayerProjectionView.Buffer projectionLayerViews;
    protected int swapIndex;

    @Getter
    protected long windowHandle;

    protected OpenXRTexture[] leftFramebuffers;
    protected OpenXRTexture[] rightFramebuffers;

    public OpenXRRenderer(OpenXRProvider vrProvider) {
        this.vrProvider = vrProvider;

    }

    public abstract void onInit() throws Throwable;

    @Override
    public void init() throws Throwable{
        setupResolution();
        setupEyes();
        onInit();
    }

    @Override
    public void renderFrame() {

        prepareXrFrame();

        GL30.glViewport(0, 0, resolutionWidth, resolutionHeight);
        GL30.glEnable(GL30.GL_DEPTH_TEST);

        getCurrentScene().prepareFrame();

        finishXrFrame();

        GL30.glFlush();
        GL30.glFinish();
    }

    protected void prepareXrFrame(){
        XrSwapchain xrSwapchain = vrProvider.getVrState().getXrSwapChain().getHandle();
        this.projectionLayerViews = XrCompositionLayerProjectionView.calloc(2);
        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer intBuf2 = stack.callocInt(1);

            vrProvider.checkXRError(
                    XR10.xrAcquireSwapchainImage(
                            xrSwapchain,
                            XrSwapchainImageAcquireInfo
                                    .calloc(stack)
                                    .type(XR10.XR_TYPE_SWAPCHAIN_IMAGE_ACQUIRE_INFO),
                            intBuf2
                    ),
                    "xrAcquireSwapchainImage", ""
            );

            vrProvider.checkXRError(
                    XR10.xrWaitSwapchainImage(xrSwapchain,
                            XrSwapchainImageWaitInfo.calloc(stack)
                                    .type(XR10.XR_TYPE_SWAPCHAIN_IMAGE_WAIT_INFO)
                                    .timeout(XR10.XR_INFINITE_DURATION)
                    ),
                    "xrWaitSwapchainImage", ""
            );

            this.swapIndex = intBuf2.get(0);

            // Render view to the appropriate part of the swapchain image.
            for (EyeType eyeType : EyeType.values()) {
                int index = eyeType.getId();
                XrView xrView = vrProvider.getXrView(eyeType);
                XrSwapchainSubImage subImage = this.projectionLayerViews.get(index)
                        .type(XR10.XR_TYPE_COMPOSITION_LAYER_PROJECTION_VIEW)
                        .pose(xrView.pose())
                        .fov(xrView.fov())
                        .subImage();
                subImage.swapchain(xrSwapchain);
                subImage.imageRect().offset().set(0, 0);
                subImage.imageRect().extent().set(resolutionWidth, resolutionHeight);
                subImage.imageArrayIndex(index);
            }

        }
    }
    protected void finishXrFrame(){
        XrSwapchain xrSwapchain = vrProvider.getVrState().getXrSwapChain().getHandle();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer layers = stack.callocPointer(1);
            int error;

            error = XR10.xrReleaseSwapchainImage(
                    xrSwapchain,
                    XrSwapchainImageReleaseInfo.calloc(stack)
                            .type(XR10.XR_TYPE_SWAPCHAIN_IMAGE_RELEASE_INFO));
            vrProvider.checkXRError(error, "xrReleaseSwapchainImage", "");

            XrCompositionLayerProjection compositionLayerProjection = XrCompositionLayerProjection.calloc(stack)
                    .type(XR10.XR_TYPE_COMPOSITION_LAYER_PROJECTION)
                    .space(vrProvider.getVrState().getXrSession().getXrAppSpace())
                    .views(this.projectionLayerViews);

            layers.put(compositionLayerProjection);

            layers.flip();

            error = XR10.xrEndFrame(
                    vrProvider.getVrState().getXrSession().getHandle(),
                    XrFrameEndInfo.calloc(stack)
                            .type(XR10.XR_TYPE_FRAME_END_INFO)
                            .displayTime(vrProvider.getXrDisplayTime())
                            .environmentBlendMode(XR10.XR_ENVIRONMENT_BLEND_MODE_OPAQUE)
                            .layers(layers));
            vrProvider.checkXRError(error, "xrEndFrame", "");

            this.projectionLayerViews.close();
        }
    }


    @Override
    public void destroy() {
        getCurrentScene().destroy();
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        glfwTerminate();
    }


    public void setupGLContext() {
        GLFWErrorCallback.createPrint(System.out).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }


        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_DEPTH_BITS, 24);
        glfwWindowHint(GLFW_STENCIL_BITS, 8);

        windowHandle = glfwCreateWindow(640, 480, vrProvider.getAppName(), 0L, 0L);
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

    protected void setupResolution() {

        resolutionWidth = vrProvider.getVrState().getEyeMaxWidth();
        resolutionHeight = vrProvider.getVrState().getEyeMaxHeight();
    }

    protected void setupEyes() {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            // Get amount of views in the swapchain
            IntBuffer intBuffer = stack.ints(0); //Set value to 0
            int error = XR10.xrEnumerateSwapchainImages(vrProvider.getVrState().getXrSwapChain().getHandle(), intBuffer, null);
            vrProvider.checkXRError(error, "xrEnumerateSwapchainImages", "get count");

            // Now we know the amount, create the image buffer
            int imageCount = intBuffer.get(0);
            XrSwapchainImageOpenGLKHR.Buffer swapchainImageBuffer = vrProvider.getVrState().getOsCompatibility().createImageBuffers(imageCount,
                    stack);

            error = XR10.xrEnumerateSwapchainImages(vrProvider.getVrState().getXrSwapChain().getHandle(), intBuffer,
                    XrSwapchainImageBaseHeader.create(swapchainImageBuffer.address(), swapchainImageBuffer.capacity()));
            vrProvider.checkXRError(error, "xrEnumerateSwapchainImages", "get images");

            this.leftFramebuffers = new OpenXRTexture[imageCount];
            this.rightFramebuffers = new OpenXRTexture[imageCount];

            for (int i = 0; i < imageCount; i++) {
                XrSwapchainImageOpenGLKHR openxrImage = swapchainImageBuffer.get(i);
                this.leftFramebuffers[i] = new OpenXRTexture(
                        resolutionWidth, resolutionHeight,
                        openxrImage.image(), 0
                ).init();
                GLUtils.checkGLError("Left Eye " + i + " framebuffer setup");
                this.rightFramebuffers[i] = new OpenXRTexture(
                        resolutionWidth, resolutionHeight,
                        openxrImage.image(),
                        1
                ).init();
                GLUtils.checkGLError("Right Eye " + i + " framebuffer setup");

            }
        }


    }

    @Override
    public VRTexture getTextureLeftEye() {
        return leftFramebuffers[swapIndex];
    }

    @Override
    public VRTexture getTextureRightEye() {
        return rightFramebuffers[swapIndex];
    }

    @Override
    public float[] getHiddenAreaVertices(EyeType eyeType) {
        return new float[0];
    }

}
