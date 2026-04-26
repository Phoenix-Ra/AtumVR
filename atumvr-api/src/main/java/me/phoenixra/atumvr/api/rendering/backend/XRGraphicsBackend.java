package me.phoenixra.atumvr.api.rendering.backend;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.XrInstance;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

import java.util.List;

/**
 * Pluggable graphics-API binding for OpenXR.
 * <p>
 * AtumVR's OpenXR session, swapchain and renderer are written against this
 * interface so the same core can drive different graphics APIs and platforms.
 * Two implementations are planned:
 * <ul>
 *     <li>{@code DesktopOpenGLGraphicsBackend} — desktop OpenGL via GLFW,
 *         lives in {@code atumvr-core}.</li>
 *     <li>{@code AndroidGLESGraphicsBackend} — OpenGL ES via EGL on the
 *         Meta OpenXR Mobile loader, lives in {@code atumvr-android}.</li>
 * </ul>
 *
 * <h2>Lifecycle</h2>
 * The backend is owned by the VR provider. Method call order during init is:
 * <ol>
 *     <li>{@link #getRequiredExtensionName()} (before {@code xrCreateInstance})</li>
 *     <li>{@link #checkGraphicsRequirements(XrInstance, long)} (after the system
 *         id has been obtained, before session creation)</li>
 *     <li>{@link #createGraphicsBinding(MemoryStack, XrInstance, long)}
 *         (passed as the {@code next} chain of {@code XrSessionCreateInfo})</li>
 *     <li>{@link #getDefaultSwapchainFormats()} (when picking a swapchain
 *         color format)</li>
 *     <li>{@link #allocateSwapchainImages(int, MemoryStack)} (per-frame setup)</li>
 * </ol>
 */
public interface XRGraphicsBackend {

    /**
     * The OpenXR graphics extension that must be enabled on {@code xrCreateInstance}
     * for this backend (e.g. {@code XR_KHR_opengl_enable},
     * {@code XR_KHR_opengl_es_enable}, {@code XR_KHR_vulkan_enable2}).
     */
    @NotNull String getRequiredExtensionName();

    /**
     * Query the runtime's graphics-API requirements. Implementations call the
     * matching {@code xrGet*GraphicsRequirementsKHR} entry point and may throw
     * if the host context does not satisfy them.
     */
    void checkGraphicsRequirements(@NotNull XrInstance instance, long systemId);

    /**
     * Build the {@code XrGraphicsBinding*} struct that goes in the
     * {@code next} chain of {@code XrSessionCreateInfo}. The returned struct
     * must remain valid until {@code xrCreateSession} returns; allocate it on
     * the supplied stack.
     */
    @NotNull Struct<?> createGraphicsBinding(@NotNull MemoryStack stack,
                                             @NotNull XrInstance instance,
                                             long systemId);

    /**
     * Allocate a {@code XrSwapchainImage*} buffer for the swapchain enumeration
     * step. The returned handle exposes the address/capacity needed by
     * {@code xrEnumerateSwapchainImages} and the per-index image id used by
     * the renderer when wiring up framebuffers.
     */
    @NotNull XRSwapchainImages allocateSwapchainImages(int imageCount, @NotNull MemoryStack stack);

    /**
     * Default ordered list of preferred swapchain color formats, most
     * preferred first. Backed by graphics-API-specific constants
     * (desktop GL vs. GLES vs. Vulkan).
     */
    @NotNull List<Integer> getDefaultSwapchainFormats();

    /**
     * Optional one-time host-context setup (e.g. creating a hidden GLFW window
     * on desktop, or finishing EGL setup on Android). Called from
     * {@code XRRenderer#setupGLContext()} when the host hasn't already
     * provided its own context. Default: no-op.
     */
    default void setupHostContext(@NotNull String appName) {
    }

    /**
     * Tear down anything {@link #setupHostContext(String)} created. Default: no-op.
     */
    default void destroyHostContext() {
    }
}
