## Contents

1. [Setting up handler](#setting-up-handler)
2. [Scene](#scene)
3. [Eye Cameras](#eye-cameras)
4. [Textures](#textures)

## Setting up handler

Lets make a renderer for your VRProvider.

Its actually pretty simple.

Create a class ExampleVRRenderer (call it however you want). Then, make it extend `XRRenderer`. Then, create its instance inside `createRenderer()` method of VRProvider.

`XRRenderer` already does most of the work, it only needs a Scene object from you that will actually render your VR scene. Also, you need to setup OpenGL context. You can use built-in method to create OpenGL context for you using `setupGLContext()`. Has to be used inside `createRenderer()` method of VRProvider. Otherwise, you will have to override `getWindowHandle()` and provide your app's window handle.

Example:
```java
public class ExampleVRRenderer extends XRRenderer {
    private VRScene vrScene;
    
    public ExampleVRRenderer(XRProvider provider) {
        super(provider);
        vrScene = new ExampleScene(this);
    }

    @Override
    protected XRTexture createTexture(int width, int height, int textureId, int index) {
        return new XRTexture(width, height, textureId, index);
    }

    @Override
    public void onInit() {
        vrScene.init();
    }

    @Override
    public VRScene getCurrentScene() {
        return vrScene;
    }
}
```

And in your VRProvider:
```java
@Override
public @NotNull XRRenderer createRenderer() {
    ExampleVRRenderer vrRenderer = new ExampleVRRenderer(this);
    vrRenderer.setupGLContext();
    return vrRenderer;
}
```

## Scene

Now lets make a VR scene.

Our goal is to draw the scene on framebuffer texture for left and right eyes.

`XRScene` is an abstract class we gonna extend from.

It has some built-in logic, but I highly suggest to override it, to have full control over this part. Otherwise, you can just use built-in logic by implementing:
- `onInit()` - to initialize shader for example
- `updateEyeTexture(EyeType)` - to render your scene for each eye

Example:
```java
public class ExampleScene extends XRScene {
    private VRShaderProgram shaderProgram;
    
    public ExampleScene(XRRenderer vrRenderer) {
        super(vrRenderer);
    }

    @Override
    public void onInit() {
        // Initialize your shaders
        shaderProgram = new VRShaderProgram(getVrProvider());
        shaderProgram.bindVertexShader("vertex.vsh");
        shaderProgram.bindFragmentShader("fragment.fsh");
        shaderProgram.finishShader();
    }

    @Override
    public void updateEyeTexture(@NotNull EyeType eyeType) {
        shaderProgram.useShader();
        
        // Get the correct camera for this eye
        Matrix4f projection = eyeType == EyeType.LEFT ?
                getLeftEyeCamera().getProjectionMatrix() :
                getRightEyeCamera().getProjectionMatrix();
        Matrix4f view = eyeType == EyeType.LEFT ?
                getLeftEyeCamera().getViewMatrix() :
                getRightEyeCamera().getViewMatrix();
        
        // Render your objects here
        // ...
        
        GL30.glUseProgram(0);
    }
    
    @Override
    public void destroy() {
        // Release all resources attached to scene
    }
}
```

## Eye Cameras

`XRScene` provides built-in eye cameras (`leftEyeCamera` and `rightEyeCamera`) that are automatically updated each frame.

Each camera provides:
- `getViewMatrix()` - view transformation matrix based on HMD position
- `getProjectionMatrix()` - projection matrix based on HMD field of view

The cameras are updated in `setupMvp()` which is called at the start of each render call. You can override this method if you need custom near/far clip distances (default is 0.02f to 100f).

## Textures

AtumVR uses `XRTexture` for VR framebuffers. The renderer creates textures for both eyes automatically using swapchain images from the VR runtime.

You can access the current eye textures via:
- `vrRenderer.getTextureLeftEye()` 
- `vrRenderer.getTextureRightEye()`

Each texture has:
- `getFrameBufferId()` - OpenGL framebuffer ID to bind for rendering
- `getTextureId()` - OpenGL texture ID

The built-in `XRScene.render()` method already handles binding the correct framebuffer for each eye.
