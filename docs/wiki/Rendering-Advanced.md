## Contents

1. [Hidden Area Mesh](#hidden-area-mesh)
2. [Custom Textures](#custom-textures)
3. [Resolution and Swapchain](#resolution-and-swapchain)
4. [Multi-pass Rendering](#multi-pass-rendering)

## Hidden Area Mesh

VR headsets have areas at the edges of each lens that the user cannot see. Rendering to these areas wastes GPU resources. AtumVR provides access to the hidden area mesh that you can use for stencil-based optimization.

### Getting hidden area data

```java
float[] leftHiddenArea = vrRenderer.getHiddenAreaVertices(EyeType.LEFT);
float[] rightHiddenArea = vrRenderer.getHiddenAreaVertices(EyeType.RIGHT);
```

The returned array contains triangle vertices in the format `[x1, y1, x2, y2, x3, y3, ...]` in pixel coordinates. These triangles represent the areas that should NOT be rendered.

### Using hidden area for optimization

You can use this data to create a stencil buffer that prevents rendering to hidden areas:

1. At the start of each eye render, draw the hidden area triangles to the stencil buffer
2. Set stencil test to fail where hidden area was drawn
3. Render your scene normally - pixels in hidden area will be skipped

This optimization can significantly improve performance, especially on complex scenes.

## Custom Textures

While `XRTexture` works for most cases, you can create custom texture classes for more control.

### Extending XRTexture

```java
public class CustomVRTexture extends XRTexture {
    private int depthBuffer;
    
    public CustomVRTexture(int width, int height, int textureId, int index) {
        super(width, height, textureId, index);
    }
    
    @Override
    public XRTexture init() {
        super.init();
        
        // Add custom depth buffer
        depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, 
            GL30.GL_DEPTH24_STENCIL8, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, 
            GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
            
        return this;
    }
}
```

Then in your renderer:
```java
@Override
protected XRTexture createTexture(int width, int height, int textureId, int index) {
    return new CustomVRTexture(width, height, textureId, index);
}
```

## Resolution and Swapchain

### Getting render resolution

```java
int width = vrRenderer.getResolutionWidth();
int height = vrRenderer.getResolutionHeight();
```

The resolution is determined by the VR runtime based on the headset capabilities. AtumVR automatically uses the recommended resolution.

### Swapchain formats

AtumVR requests swapchain images in this order of preference:
1. `GL_SRGB8_ALPHA8` - sRGB with alpha (best quality)
2. `GL_SRGB8` - sRGB without alpha
3. `GL_RGB10_A2` - 10-bit RGB
4. `GL_RGBA16F` - 16-bit float RGBA
5. `GL_RGBA8` - fallback

You can customize this by overriding `getSwapChainFormats()` in your `XRProvider`:
```java
@Override
public List<Integer> getSwapChainFormats() {
    return List.of(
        GL21.GL_SRGB8_ALPHA8,
        GL11.GL_RGBA8
    );
}
```

## Multi-pass Rendering

For complex rendering pipelines, you may need multiple passes per eye.

### Rendering to intermediate buffers

Instead of rendering directly to the swapchain framebuffer, render to your own framebuffer first:

```java
@Override
public void updateEyeTexture(@NotNull EyeType eyeType) {
    // Pass 1: Render scene to intermediate buffer
    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, myIntermediateFramebuffer);
    renderScene(eyeType);
    
    // Pass 2: Post-processing
    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, myPostProcessFramebuffer);
    applyPostProcessing();
    
    // Final pass: Copy to VR framebuffer
    int vrFramebuffer = (eyeType == EyeType.LEFT)
        ? getVrRenderer().getTextureLeftEye().getFrameBufferId()
        : getVrRenderer().getTextureRightEye().getFrameBufferId();
    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, vrFramebuffer);
    drawFullscreenQuad(myPostProcessTexture);
}
```

### Important notes

- Always end each eye's rendering with the final result in the VR framebuffer
- The VR runtime expects the swapchain image to contain the final rendered frame
- Be mindful of performance - VR requires high framerates (typically 72-120 FPS)
