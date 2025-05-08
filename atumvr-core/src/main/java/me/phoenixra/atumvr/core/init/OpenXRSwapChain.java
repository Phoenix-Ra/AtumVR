package me.phoenixra.atumvr.core.init;

import lombok.Getter;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.OpenXRState;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryStack.stackCalloc;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memCalloc;

public class OpenXRSwapChain {


    private final OpenXRState xrState;


    @Getter
    protected XrSwapchain handle;


    @Getter
    protected XrView.Buffer xrViewBuffer;

    @Getter
    protected int eyeMaxWidth;
    @Getter
    protected int eyeMaxHeight;


    public OpenXRSwapChain(OpenXRState xrState){
        this.xrState = xrState;

    }

    public void init() {
        try (MemoryStack stack = stackPush()) {
            OpenXRProvider provider = this.xrState.getVrProvider();
            OpenXRInstance xrInstance  = xrState.getXrInstance();
            long systemId = xrState.getXrSystem().getSystemId();
            OpenXRSession xrSession  = xrState.getXrSession();

            // Check amount of views
            IntBuffer intBuf = stack.callocInt(1);
            int error = XR10.xrEnumerateViewConfigurationViews(xrInstance.getHandle(), systemId,
                    XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO, intBuf, null);
            provider.checkXRError(error, "xrEnumerateViewConfigurationViews", "get count");

            // Get all views
            ByteBuffer viewConfBuffer = bufferStack(intBuf.get(0), XrViewConfigurationView.SIZEOF,
                    XR10.XR_TYPE_VIEW_CONFIGURATION_VIEW);
            XrViewConfigurationView.Buffer views = new XrViewConfigurationView.Buffer(viewConfBuffer);
            error = XR10.xrEnumerateViewConfigurationViews(xrInstance.getHandle(), systemId,
                    XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO, intBuf, views);
            provider.checkXRError(error, "xrEnumerateViewConfigurationViews", "get views");
            int viewCountNumber = intBuf.get(0);

            this.xrViewBuffer = new XrView.Buffer(
                    bufferHeap(viewCountNumber, XrView.SIZEOF, XR10.XR_TYPE_VIEW)
            );
            // Check swapchain formats
            error = XR10.xrEnumerateSwapchainFormats(xrSession.getHandle(), intBuf, null);
            provider.checkXRError(error, "xrEnumerateSwapchainFormats", "get count");

            // Get swapchain formats
            LongBuffer swapchainFormats = stack.callocLong(intBuf.get(0));
            error = XR10.xrEnumerateSwapchainFormats(xrSession.getHandle(), intBuf, swapchainFormats);
            provider.checkXRError(error, "xrEnumerateSwapchainFormats", "get formats");

            long[] desiredSwapchainFormats = {
                    // SRGB formats
                    GL21.GL_SRGB8_ALPHA8,
                    GL21.GL_SRGB8,
                    // others
                    GL11.GL_RGB10_A2,
                    GL30.GL_RGBA16F,
                    GL30.GL_RGB16F,

                    // The two below should only be used as a fallback, as they are linear color formats without enough bits for color
                    // depth, thus leading to banding.
                    GL11.GL_RGBA8,
                    GL31.GL_RGBA8_SNORM,
            };

            // Choose format
            long chosenFormat = 0;
            for (long glFormatIter : desiredSwapchainFormats) {
                swapchainFormats.rewind();
                while (swapchainFormats.hasRemaining()) {
                    if (glFormatIter == swapchainFormats.get()) {
                        chosenFormat = glFormatIter;
                        break;
                    }
                }
                if (chosenFormat != 0) {
                    break;
                }
            }

            if (chosenFormat == 0) {
                var formats = new ArrayList<Long>();
                swapchainFormats.rewind();
                while (swapchainFormats.hasRemaining()) {
                    formats.add(swapchainFormats.get());
                }
                throw new RuntimeException("No compatible swapchain / framebuffer format available: " + formats);
            }

            // Make swapchain
            XrViewConfigurationView viewConfig = views.get(0);
            XrSwapchainCreateInfo swapchainCreateInfo = XrSwapchainCreateInfo.calloc(stack);
            swapchainCreateInfo.type(XR10.XR_TYPE_SWAPCHAIN_CREATE_INFO);
            swapchainCreateInfo.next(NULL);
            swapchainCreateInfo.createFlags(0);
            swapchainCreateInfo.usageFlags(XR10.XR_SWAPCHAIN_USAGE_COLOR_ATTACHMENT_BIT);
            swapchainCreateInfo.format(chosenFormat);
            swapchainCreateInfo.sampleCount(1);
            swapchainCreateInfo.width(viewConfig.recommendedImageRectWidth());
            swapchainCreateInfo.height(viewConfig.recommendedImageRectHeight());
            swapchainCreateInfo.faceCount(1);
            swapchainCreateInfo.arraySize(2);
            swapchainCreateInfo.mipCount(1);

            PointerBuffer handlePointer = stack.callocPointer(1);
            error = XR10.xrCreateSwapchain(xrSession.getHandle(), swapchainCreateInfo, handlePointer);
            provider.checkXRError(error, "xrCreateSwapchain", "format: " + chosenFormat);
            handle = new XrSwapchain(handlePointer.get(0), xrSession.getHandle());
            eyeMaxWidth = swapchainCreateInfo.width();
            eyeMaxHeight = swapchainCreateInfo.height();
        }
    }


    public void destroy(){
        if (handle != null) {
            int error = XR10.xrDestroySwapchain(handle);
            xrState.getVrProvider().checkXRError(error, "xrDestroySwapchain", "");
        }
        if (this.xrViewBuffer != null) {
            this.xrViewBuffer.close();
        }
    }


    private ByteBuffer bufferStack(int capacity, int sizeof, int type) {
        ByteBuffer b = stackCalloc(capacity * sizeof);

        for (int i = 0; i < capacity; i++) {
            b.position(i * sizeof);
            b.putInt(type);
        }
        b.rewind();
        return b;
    }
    private ByteBuffer bufferHeap(int capacity, int sizeof, int type) {
        ByteBuffer b = memCalloc(capacity * sizeof);

        for (int i = 0; i < capacity; i++) {
            b.position(i * sizeof);
            b.putInt(type);
        }
        b.rewind();
        return b;
    }
}
