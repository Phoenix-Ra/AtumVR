package me.phoenixra.atumvr.api.rendering.backend;

/**
 * Opaque handle over a buffer of {@code XrSwapchainImage*} structs allocated by
 * an {@link XRGraphicsBackend}. The buffer layout is graphics-API specific
 * (e.g. {@code XrSwapchainImageOpenGLKHR}, {@code XrSwapchainImageOpenGLESKHR},
 * {@code XrSwapchainImageVulkan2KHR}); this interface hides those types from
 * the rest of the renderer.
 */
public interface XRSwapchainImages {

    /** Native address of the underlying struct buffer (for {@code xrEnumerateSwapchainImages}). */
    long address();

    /** Number of images allocated. */
    int capacity();

    /**
     * Returns the backend-specific texture/image handle at the given index.
     * For OpenGL/OpenGL ES backends this is the GL texture id; for Vulkan it
     * would be a {@code VkImage} pointer.
     */
    int getImageId(int index);
}
