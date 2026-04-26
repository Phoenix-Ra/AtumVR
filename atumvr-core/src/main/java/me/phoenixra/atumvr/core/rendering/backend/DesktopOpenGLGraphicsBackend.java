package me.phoenixra.atumvr.core.rendering.backend;

import me.phoenixra.atumvr.api.exceptions.AtumVRException;
import me.phoenixra.atumvr.api.rendering.backend.XRGraphicsBackend;
import me.phoenixra.atumvr.api.rendering.backend.XRSwapchainImages;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWNativeGLX;
import org.lwjgl.glfw.GLFWNativeWGL;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.glfw.GLFWNativeX11;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.openxr.KHROpenGLEnable;
import org.lwjgl.openxr.XrGraphicsBindingOpenGLWin32KHR;
import org.lwjgl.openxr.XrGraphicsBindingOpenGLXlibKHR;
import org.lwjgl.openxr.XrGraphicsRequirementsOpenGLKHR;
import org.lwjgl.openxr.XrInstance;
import org.lwjgl.openxr.XrSwapchainImageOpenGLKHR;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.system.Struct;
import org.lwjgl.system.linux.X11;
import org.lwjgl.system.windows.User32;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GLX13.*;
import static org.lwjgl.system.MemoryStack.stackInts;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Desktop OpenGL graphics backend (Windows / Linux) bound via GLFW.
 * <p>
 * This is the historical AtumVR rendering path; it owns the GLFW context
 * created in {@link #setupHostContext(String)} unless the embedding app
 * supplies its own window handle via {@link #setExternalWindowHandle(long)}.
 */
public class DesktopOpenGLGraphicsBackend implements XRGraphicsBackend {

    private long windowHandle;
    private boolean ownsWindow;

    /**
     * Inject a GLFW window handle owned by the host application. When set,
     * {@link #setupHostContext(String)} becomes a no-op.
     */
    public void setExternalWindowHandle(long windowHandle) {
        this.windowHandle = windowHandle;
        this.ownsWindow = false;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    @Override
    public @NotNull String getRequiredExtensionName() {
        return KHROpenGLEnable.XR_KHR_OPENGL_ENABLE_EXTENSION_NAME;
    }

    @Override
    public void checkGraphicsRequirements(@NotNull XrInstance instance, long systemId) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrGraphicsRequirementsOpenGLKHR req = XrGraphicsRequirementsOpenGLKHR.calloc(stack)
                    .type(KHROpenGLEnable.XR_TYPE_GRAPHICS_REQUIREMENTS_OPENGL_KHR);
            KHROpenGLEnable.xrGetOpenGLGraphicsRequirementsKHR(instance, systemId, req);
        }
    }

    @Override
    public @NotNull Struct<?> createGraphicsBinding(@NotNull MemoryStack stack,
                                                    @NotNull XrInstance instance,
                                                    long systemId) {
        if (windowHandle == 0L) {
            throw new AtumVRException(
                    "DesktopOpenGLGraphicsBackend has no GL window handle; " +
                            "call setupHostContext() or setExternalWindowHandle() first");
        }
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
            PointerBuffer fbConfigBuf = glXChooseFBConfig(
                    xDisplay, X11.XDefaultScreen(xDisplay),
                    stackInts(GLX_FBCONFIG_ID, fbXID, 0)
            );
            if (fbConfigBuf == null) {
                throw new AtumVRException("Framebuffer config is null");
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
            throw new AtumVRException("MacOS not supported");
        }
    }

    @Override
    public @NotNull XRSwapchainImages allocateSwapchainImages(int imageCount, @NotNull MemoryStack stack) {
        XrSwapchainImageOpenGLKHR.Buffer buffer = XrSwapchainImageOpenGLKHR.calloc(imageCount, stack);
        for (XrSwapchainImageOpenGLKHR image : buffer) {
            image.type(KHROpenGLEnable.XR_TYPE_SWAPCHAIN_IMAGE_OPENGL_KHR);
        }
        return new XRSwapchainImages() {
            @Override
            public long address() {
                return buffer.address();
            }

            @Override
            public int capacity() {
                return buffer.capacity();
            }

            @Override
            public int getImageId(int index) {
                return buffer.get(index).image();
            }
        };
    }

    @Override
    public @NotNull List<Integer> getDefaultSwapchainFormats() {
        return List.of(
                GL30.GL_SRGB8_ALPHA8,
                GL30.GL_SRGB8,
                GL30.GL_RGB10_A2,
                GL30.GL_RGBA16F,
                GL30.GL_RGB16F,
                // Fallback
                GL30.GL_RGBA8,
                GL31.GL_RGBA8_SNORM
        );
    }

    @Override
    public void setupHostContext(@NotNull String appName) {
        if (windowHandle != 0L) {
            return;
        }
        ownsWindow = true;
        GLFWErrorCallback.createPrint(System.out).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_DEPTH_BITS, 24);
        glfwWindowHint(GLFW_STENCIL_BITS, 8);

        windowHandle = glfwCreateWindow(640, 480, appName, 0L, 0L);
        if (windowHandle == 0L) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1);

        GL.createCapabilities();
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_CULL_FACE);
        GL30.glCullFace(GL30.GL_BACK);
    }

    @Override
    public void destroyHostContext() {
        if (!ownsWindow || windowHandle == 0L) {
            return;
        }
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        windowHandle = 0L;
        ownsWindow = false;
    }
}
