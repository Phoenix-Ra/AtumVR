package me.phoenixra.atumvr.api.provider.openxr.oscompat;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWNativeGLX;
import org.lwjgl.glfw.GLFWNativeWGL;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.system.Struct;
import org.lwjgl.system.linux.X11;
import org.lwjgl.system.windows.User32;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GLX13.*;
import static org.lwjgl.system.MemoryStack.stackInts;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DesktopCompat implements OSCompatibility{
    @Override
    public long getPlatformInfo(MemoryStack stack) {
        return NULL;
    }

    @Override
    public void initOpenXRLoader(MemoryStack stack) {
        System.out.printf(
                "Platform: %s%n", System.getProperty("os.version")
        );
    }

    @Override
    public String getGraphicsExtension() {
        return KHROpenGLEnable.XR_KHR_OPENGL_ENABLE_EXTENSION_NAME;
    }

    @Override
    public XrSwapchainImageOpenGLKHR.Buffer createImageBuffers(int imageCount, MemoryStack stack) {
        XrSwapchainImageOpenGLKHR.Buffer swapchainImageBuffer = XrSwapchainImageOpenGLKHR.calloc(imageCount, stack);
        for (XrSwapchainImageOpenGLKHR image : swapchainImageBuffer) {
            image.type(KHROpenGLEnable.XR_TYPE_SWAPCHAIN_IMAGE_OPENGL_KHR);
        }

        return swapchainImageBuffer;
    }

    @Override
    public Struct<?> checkGraphics(MemoryStack stack,
                                XrInstance instance,
                                long systemID,
                                long windowHandle) {
        XrGraphicsRequirementsOpenGLKHR graphicsRequirements = XrGraphicsRequirementsOpenGLKHR.calloc(stack)
                .type(KHROpenGLEnable.XR_TYPE_GRAPHICS_REQUIREMENTS_OPENGL_KHR);
        KHROpenGLEnable.xrGetOpenGLGraphicsRequirementsKHR(instance, systemID, graphicsRequirements);
        //Bind the OpenGL context to the OpenXR instance and create the session
        if (Platform.get() == Platform.WINDOWS) {
            return XrGraphicsBindingOpenGLWin32KHR.calloc(stack).set(
                    KHROpenGLEnable.XR_TYPE_GRAPHICS_BINDING_OPENGL_WIN32_KHR,
                    NULL,
                    User32.GetDC(GLFWNativeWin32.glfwGetWin32Window(windowHandle)),
                    GLFWNativeWGL.glfwGetWGLContext(windowHandle)
            );
        } else if (Platform.get() == Platform.LINUX) {
            long xDisplay = GLFWNativeX11.glfwGetX11Display();

            long glXContext = GLFWNativeGLX.glfwGetGLXContext(windowHandle);
            long glXWindowHandle = GLFWNativeGLX.glfwGetGLXWindow(windowHandle);

            int fbXID = glXQueryDrawable(xDisplay, glXWindowHandle, GLX_FBCONFIG_ID);
            PointerBuffer fbConfigBuf = glXChooseFBConfig(xDisplay, X11.XDefaultScreen(xDisplay),
                    stackInts(GLX_FBCONFIG_ID, fbXID, 0));
            if (fbConfigBuf == null) {
                throw new IllegalStateException("Your framebuffer config was null, make a github issue");
            }
            long fbConfig = fbConfigBuf.get();

            return XrGraphicsBindingOpenGLXlibKHR.calloc(stack).set(
                    KHROpenGLEnable.XR_TYPE_GRAPHICS_BINDING_OPENGL_XLIB_KHR,
                    NULL,
                    xDisplay,
                    (int) Objects.requireNonNull(glXGetVisualFromFBConfig(xDisplay, fbConfig)).visualid(),
                    fbConfig,
                    glXWindowHandle,
                    glXContext
            );
        } else {
            throw new IllegalStateException("Macos not supported");
        }
    }
}
