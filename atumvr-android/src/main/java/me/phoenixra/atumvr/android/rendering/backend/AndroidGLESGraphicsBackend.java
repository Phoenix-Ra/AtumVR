package me.phoenixra.atumvr.android.rendering.backend;

import me.phoenixra.atumvr.api.exceptions.AtumVRException;
import me.phoenixra.atumvr.api.rendering.backend.XRGraphicsBackend;
import me.phoenixra.atumvr.api.rendering.backend.XRSwapchainImages;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.XrInstance;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

import java.util.List;

/**
 * OpenGL ES graphics backend for AtumVR running on Quest 3 (and other
 * Android-based standalone XR devices).
 *
 * <h2>Status: scaffold</h2>
 * This class is a skeleton. The actual native bindings to
 * {@code XrGraphicsBindingOpenGLESAndroidKHR},
 * {@code XrSwapchainImageOpenGLESKHR}, and
 * {@code xrGetOpenGLESGraphicsRequirementsKHR} are not part of LWJGL's
 * upstream artifacts yet. Quest standalone uses the Meta OpenXR Mobile
 * loader (a native AAR shipping {@code libopenxr_loader.so}). To finish this
 * backend you have two practical options:
 * <ol>
 *     <li><b>JNI shim (recommended for now):</b> wrap the four mobile-only
 *     OpenXR entry points in a small C library, called from Java via JNI,
 *     and have the methods below delegate to it.</li>
 *     <li><b>Custom LWJGL build:</b> compile LWJGL with an Android target,
 *     enabling the OpenGL ES + Android extensions in {@code openxr-mobile}.
 *     Significant build work; not currently upstreamed.</li>
 * </ol>
 *
 * <h2>Lifecycle</h2>
 * Constructed with the EGL handles produced by {@link
 * me.phoenixra.atumvr.android.activity.XrActivity}. The activity owns the
 * {@code SurfaceView} / {@code GLSurfaceView}, creates the {@code EGLDisplay}
 * / {@code EGLContext}, then hands them to this backend before
 * {@code initializeVR()} runs.
 */
public class AndroidGLESGraphicsBackend implements XRGraphicsBackend {

    /** EGL display obtained via {@code eglGetDisplay(EGL_DEFAULT_DISPLAY)}. */
    private long eglDisplay;

    /** EGL config selected for the GLES 3 surface. */
    private long eglConfig;

    /** EGL context bound to the render thread. */
    private long eglContext;

    public AndroidGLESGraphicsBackend(long eglDisplay, long eglConfig, long eglContext) {
        this.eglDisplay = eglDisplay;
        this.eglConfig = eglConfig;
        this.eglContext = eglContext;
    }

    public void updateEglHandles(long eglDisplay, long eglConfig, long eglContext) {
        this.eglDisplay = eglDisplay;
        this.eglConfig = eglConfig;
        this.eglContext = eglContext;
    }

    @Override
    public @NotNull String getRequiredExtensionName() {
        // XR_KHR_OPENGL_ES_ENABLE_EXTENSION_NAME — the mobile counterpart to
        // KHR_opengl_enable. Hard-coded here because the constant lives in the
        // openxr-mobile headers, which aren't on the desktop classpath.
        return "XR_KHR_opengl_es_enable";
    }

    @Override
    public void checkGraphicsRequirements(@NotNull XrInstance instance, long systemId) {
        // TODO(quest): call xrGetOpenGLESGraphicsRequirementsKHR via the JNI shim
        // and validate that the runtime accepts our GLES 3.x context.
        throw new AtumVRException(
                "AndroidGLESGraphicsBackend.checkGraphicsRequirements is not implemented yet — " +
                        "wire up the Meta OpenXR Mobile loader via JNI before running on Quest.");
    }

    @Override
    public @NotNull Struct<?> createGraphicsBinding(@NotNull MemoryStack stack,
                                                    @NotNull XrInstance instance,
                                                    long systemId) {
        // TODO(quest): allocate XrGraphicsBindingOpenGLESAndroidKHR via the
        // JNI shim with (display=eglDisplay, config=eglConfig, context=eglContext)
        // and return its struct so XrSessionCreateInfo.next() consumes it.
        if (eglDisplay == 0L || eglContext == 0L) {
            throw new AtumVRException(
                    "AndroidGLESGraphicsBackend was not given EGL handles before session creation");
        }
        throw new AtumVRException(
                "AndroidGLESGraphicsBackend.createGraphicsBinding is not implemented yet — " +
                        "needs an XrGraphicsBindingOpenGLESAndroidKHR shim.");
    }

    @Override
    public @NotNull XRSwapchainImages allocateSwapchainImages(int imageCount, @NotNull MemoryStack stack) {
        // TODO(quest): allocate an XrSwapchainImageOpenGLESKHR buffer (sized
        // imageCount), zero its `type` to XR_TYPE_SWAPCHAIN_IMAGE_OPENGL_ES_KHR,
        // and return a XRSwapchainImages backed by that buffer where
        // getImageId(i) returns the GLES texture name for image i.
        throw new AtumVRException(
                "AndroidGLESGraphicsBackend.allocateSwapchainImages is not implemented yet.");
    }

    @Override
    public @NotNull List<Integer> getDefaultSwapchainFormats() {
        // GLES sized internal formats (values from android.opengl.GLES30 /
        // GLES31 — duplicated as int constants so this class can be exercised
        // from JVM unit tests without pulling in the Android runtime).
        // 0x8C43 = GL_SRGB8_ALPHA8
        // 0x8C41 = GL_SRGB8
        // 0x906F = GL_RGB10_A2
        // 0x881A = GL_RGBA16F
        // 0x881B = GL_RGB16F
        // 0x8058 = GL_RGBA8
        return List.of(0x8C43, 0x8C41, 0x906F, 0x881A, 0x881B, 0x8058);
    }

    @Override
    public void setupHostContext(@NotNull String appName) {
        // No-op: on Android the EGL context is created by XrActivity and
        // injected into this backend via the constructor / updateEglHandles().
    }

    @Override
    public void destroyHostContext() {
        // No-op: XrActivity owns the EGL teardown.
    }
}
