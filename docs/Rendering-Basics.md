# Rendering Basics

This guide covers the fundamentals of setting up VR rendering with AtumVR.

## Contents

1. [Setting Up the Renderer](#setting-up-the-renderer)
   - [Creating Your VRProvider](#creating-your-vrprovider)
   - [Creating Your VRRenderer](#creating-your-vrrenderer)
   - [OpenGL Context Setup](#opengl-context-setup)
2. [Creating a Scene](#creating-a-scene)
   - [Extending XRScene](#extending-xrscene)
   - [Implementing Eye Rendering](#implementing-eye-rendering)
   - [Using Eye Cameras](#using-eye-cameras)
3. [Complete Rendering Pipeline](#complete-rendering-pipeline)
4. [Full Example](#full-example)

---

## Setting Up the Renderer

### Creating Your VRProvider

The `VRProvider` is your entry point for VR. You need to extend `XRProvider` and implement required factory methods.

```java
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.XRState;
import me.phoenixra.atumvr.core.enums.XRSessionStateChange;
import me.phoenixra.atumvr.core.input.XRInputHandler;
import me.phoenixra.atumvr.core.rendering.XRRenderer;

public class MyVRProvider extends XRProvider {

    public MyVRProvider(VRLogger logger) {
        super("MyVRApp", logger);  // App name shown to user by VR runtime
    }

    @Override
    public XRState createStateHandler() {
        return new XRState(this);
    }

    @Override
    public XRRenderer createRenderer() {
        MyVRRenderer renderer = new MyVRRenderer(this);
        renderer.setupGLContext();  // IMPORTANT: Setup OpenGL context first!
        return renderer;
    }

    @Override
    public XRInputHandler createInputHandler() {
        return new XRInputHandler(this);  // Or your custom input handler
    }

    @Override
    public void onStateChanged(XRSessionStateChange state) {
        // Handle VR session state changes (STOPPING, FOCUSED, etc.)
        if (state == XRSessionStateChange.STOPPING) {
            // Handle VR session stopping
        }
    }
}
```

**Key Points:**
- The constructor calls `super(appName, logger)` - the app name is shown to VR users
- `createStateHandler()` - Creates the OpenXR state manager
- `createRenderer()` - Creates your renderer AND sets up OpenGL context
- `createInputHandler()` - Creates the input handler for controllers

### Creating Your VRRenderer

Extend `XRRenderer` to create your custom renderer. The `XRRenderer` handles most of the OpenXR frame management for you.

```java
import me.phoenixra.atumvr.api.rendering.VRScene;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.rendering.XRRenderer;
import me.phoenixra.atumvr.core.rendering.XRTexture;

public class MyVRRenderer extends XRRenderer {
    private VRScene vrScene;

    public MyVRRenderer(XRProvider provider) {
        super(provider);
        vrScene = new MyScene(this);  // Create your scene
    }

    @Override
    protected XRTexture createTexture(int width, int height, int textureId, int index) {
        // Create eye texture framebuffers
        return new XRTexture(width, height, textureId, index);
    }

    @Override
    public void onInit() {
        // Called after OpenXR is initialized
        // Initialize your scene here
        vrScene.init();
    }

    @Override
    public VRScene getCurrentScene() {
        return vrScene;
    }
}
```

**Required Methods:**
| Method | Purpose |
|--------|---------|
| `createTexture(...)` | Creates framebuffer textures for each eye |
| `onInit()` | Called after VR initialization - initialize your scene here |
| `getCurrentScene()` | Returns the active VRScene for rendering |

### OpenGL Context Setup

There are two ways to set up the OpenGL context:

**Option 1: Use built-in method (recommended)**

Call `setupGLContext()` inside `createRenderer()` method of your VRProvider:

```java
@Override
public XRRenderer createRenderer() {
    MyVRRenderer renderer = new MyVRRenderer(this);
    renderer.setupGLContext();  // Creates GLFW window and OpenGL context
    return renderer;
}
```

This creates a hidden GLFW window (640x480) with:
- Depth buffer (24-bit)
- Stencil buffer (8-bit)
- Depth testing enabled
- Back-face culling enabled

**Option 2: Provide your own window handle**

If you're integrating with an existing application, override `getWindowHandle()`:

```java
@Override
public long getWindowHandle() {
    return yourApplicationWindowHandle;
}
```

---

## Creating a Scene

### Extending XRScene

The `XRScene` class manages stereo rendering for both eyes. You need to extend it and implement the rendering logic.

```java
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.core.rendering.XRRenderer;
import me.phoenixra.atumvr.core.rendering.XRScene;

public class MyScene extends XRScene {

    public MyScene(XRRenderer vrRenderer) {
        super(vrRenderer);
    }

    @Override
    public void onInit() {
        // Initialize your scene resources:
        // - Load shaders
        // - Create geometry (VAOs, VBOs)
        // - Load textures
        // - Set up objects
    }

    @Override
    public void updateEyeTexture(EyeType eyeType) {
        // Render the scene for the specified eye
        // This is called twice per frame (once for each eye)
    }

    @Override
    public void destroy() {
        // Clean up your scene resources
        // - Delete shaders
        // - Delete buffers
        // - Delete textures
    }
}
```

### Implementing Eye Rendering

The `updateEyeTexture(EyeType eyeType)` method is where you do your actual rendering. It's called twice per frame - once for each eye.

```java
@Override
public void updateEyeTexture(EyeType eyeType) {
    // 1. Use your shader
    GL30.glUseProgram(shaderProgramId);
    
    // 2. Get the correct view/projection matrices for this eye
    Matrix4f projection, view;
    if (eyeType == EyeType.LEFT) {
        projection = getLeftEyeCamera().getProjectionMatrix();
        view = getLeftEyeCamera().getViewMatrix();
    } else {
        projection = getRightEyeCamera().getProjectionMatrix();
        view = getRightEyeCamera().getViewMatrix();
    }
    
    // 3. Create model matrix for your object (position, rotation, scale)
    Matrix4f modelMatrix = new Matrix4f().translate(0, 1, -2); // 2m in front, 1m up
    
    // 4. Calculate MVP matrix
    Matrix4f mvp = projection.mul(view, new Matrix4f()).mul(modelMatrix);
    
    // 5. Upload MVP to shader
    GL30.glUniformMatrix4fv(mvpLocation, false, mvp.get(new float[16]));
    
    // 6. Draw your objects
    GL30.glBindVertexArray(cubeVAO);
    GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 36);  // 36 vertices for a cube
    GL30.glBindVertexArray(0);
    
    // 7. Unbind shader
    GL30.glUseProgram(0);
}
```

### Using Eye Cameras

`XRScene` automatically creates two eye cameras (`leftEyeCamera` and `rightEyeCamera`) that provide view and projection matrices updated each frame with HMD tracking data.

```java
// Get cameras (already created in XRScene.init())
XREyeCamera leftCam = getLeftEyeCamera();
XREyeCamera rightCam = getRightEyeCamera();

// Access matrices (updated automatically each frame)
Matrix4f leftView = leftCam.getViewMatrix();
Matrix4f leftProj = leftCam.getProjectionMatrix();

Matrix4f rightView = rightCam.getViewMatrix();
Matrix4f rightProj = rightCam.getProjectionMatrix();
```

**Camera Configuration:**
- Near plane: `0.02f` (2cm) - default
- Far plane: `100f` (100m) - default
- FOV: Retrieved from VR runtime per eye (asymmetric)

To customize near/far planes, override `setupMvp()`:

```java
@Override
protected void setupMvp() {
    getLeftEyeCamera().updateProjectionMatrix(EyeType.LEFT, 0.1f, 500f);
    getLeftEyeCamera().updateViewMatrix(EyeType.LEFT);
    
    getRightEyeCamera().updateProjectionMatrix(EyeType.RIGHT, 0.1f, 500f);
    getRightEyeCamera().updateViewMatrix(EyeType.RIGHT);
}
```

---

## Complete Rendering Pipeline

Here's what happens each frame:

```
┌─────────────────────────────────────────────────────────────────┐
│                      MAIN LOOP                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  provider.syncState()    ← Poll VR events from runtime          │
│         │                                                        │
│         ▼                                                        │
│  provider.startFrame()   ← Prepare XR frame + update input      │
│         │                                                        │
│         ├── renderer.prepareFrame()                             │
│         │      ├── xrWaitFrame()   - Wait for VR runtime        │
│         │      ├── xrBeginFrame()  - Begin the frame            │
│         │      ├── xrLocateViews() - Get HMD pose               │
│         │      └── xrAcquireSwapchainImage() - Get framebuffer  │
│         │                                                        │
│         └── inputHandler.update() - Update controller input     │
│                                                                  │
│         ▼                                                        │
│  provider.render(context)  ← Render the scene                   │
│         │                                                        │
│         └── renderer.renderFrame(context)                       │
│                ├── Set OpenGL state                             │
│                │                                                 │
│                ├── scene.render(context)                        │
│                │      ├── setupMvp() - Update eye matrices      │
│                │      │                                          │
│                │      ├── For LEFT eye:                         │
│                │      │   ├── Bind left framebuffer             │
│                │      │   ├── Clear buffers                     │
│                │      │   └── updateEyeTexture(LEFT)            │
│                │      │                                          │
│                │      └── For RIGHT eye:                        │
│                │          ├── Bind right framebuffer            │
│                │          ├── Clear buffers                     │
│                │          └── updateEyeTexture(RIGHT)           │
│                │                                                 │
│                └── finishXrFrame()                              │
│                       ├── xrReleaseSwapchainImage()             │
│                       ├── Create composition layers             │
│                       └── xrEndFrame() - Submit to VR runtime   │
│                                                                  │
│         ▼                                                        │
│  provider.postRender()   ← Optional post-frame work             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Full Example

Here's a complete minimal example:

**Main Application:**
```java
public class MyVRApp {
    public static void main(String[] args) {
        // 1. Create provider with logger
        VRLogger logger = VRLogger.SIMPLE.setDebug(true);
        MyVRProvider provider = new MyVRProvider(logger);
        
        try {
            // 2. Initialize VR
            provider.initializeVR();
            
            // 3. Main render loop
            IRenderContext context = () -> 1;  // Simple delta time
            
            while (!provider.isStopping()) {
                provider.syncState();     // Poll VR events
                provider.startFrame();    // Prepare frame
                provider.render(context); // Render scene
                provider.postRender();    // Post-render work
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            // 4. Cleanup
            provider.destroy();
        }
    }
}
```

**VRProvider Implementation:**
```java
public class MyVRProvider extends XRProvider {
    private boolean stopping = false;

    public MyVRProvider(VRLogger logger) {
        super("MyVRApp", logger);
    }

    @Override
    public XRState createStateHandler() {
        return new XRState(this);
    }

    @Override
    public XRRenderer createRenderer() {
        MyVRRenderer renderer = new MyVRRenderer(this);
        renderer.setupGLContext();
        return renderer;
    }

    @Override
    public XRInputHandler createInputHandler() {
        return new XRInputHandler(this);
    }

    @Override
    public void onStateChanged(XRSessionStateChange state) {
        if (state == XRSessionStateChange.STOPPING) {
            stopping = true;
        }
    }
    
    public boolean isStopping() {
        return stopping;
    }
}
```

**VRRenderer Implementation:**
```java
public class MyVRRenderer extends XRRenderer {
    private VRScene scene;

    public MyVRRenderer(XRProvider provider) {
        super(provider);
        scene = new MyScene(this);
    }

    @Override
    protected XRTexture createTexture(int width, int height, int textureId, int index) {
        return new XRTexture(width, height, textureId, index);
    }

    @Override
    public void onInit() {
        scene.init();
    }

    @Override
    public VRScene getCurrentScene() {
        return scene;
    }
}
```

**VRScene Implementation:**
```java
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.core.rendering.XRRenderer;
import me.phoenixra.atumvr.core.rendering.XRScene;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class MyScene extends XRScene {
    private int shaderProgram;
    private int vao;
    private int mvpLocation;

    public MyScene(XRRenderer vrRenderer) {
        super(vrRenderer);
    }

    @Override
    public void onInit() {
        // Initialize shaders
        shaderProgram = createShaderProgram();
        mvpLocation = GL30.glGetUniformLocation(shaderProgram, "uMVP");
        
        // Initialize geometry
        vao = createCubeVAO();
    }

    @Override
    public void updateEyeTexture(EyeType eyeType) {
        GL30.glUseProgram(shaderProgram);
        
        // Get eye-specific matrices
        Matrix4f projection = eyeType == EyeType.LEFT 
            ? getLeftEyeCamera().getProjectionMatrix()
            : getRightEyeCamera().getProjectionMatrix();
            
        Matrix4f view = eyeType == EyeType.LEFT
            ? getLeftEyeCamera().getViewMatrix()
            : getRightEyeCamera().getViewMatrix();
        
        // Model matrix (position cube 2 meters in front and 1 meter up)
        Matrix4f model = new Matrix4f().translate(0, 1, -2);
        
        // Calculate MVP
        Matrix4f mvp = projection.mul(view, new Matrix4f()).mul(model);
        GL30.glUniformMatrix4fv(mvpLocation, false, mvp.get(new float[16]));
        
        // Draw
        GL30.glBindVertexArray(vao);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 36);
        GL30.glBindVertexArray(0);
        
        GL30.glUseProgram(0);
    }

    @Override
    public void destroy() {
        GL30.glDeleteProgram(shaderProgram);
        GL30.glDeleteVertexArrays(vao);
    }
    
    private int createShaderProgram() {
        // Create and compile vertex shader
        int vertexShader = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
        GL30.glShaderSource(vertexShader, """
            #version 330 core
            layout(location = 0) in vec3 aPos;
            uniform mat4 uMVP;
            void main() {
                gl_Position = uMVP * vec4(aPos, 1.0);
            }
            """);
        GL30.glCompileShader(vertexShader);
        
        // Create and compile fragment shader
        int fragmentShader = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
        GL30.glShaderSource(fragmentShader, """
            #version 330 core
            out vec4 FragColor;
            void main() {
                FragColor = vec4(0.2, 0.6, 1.0, 1.0);
            }
            """);
        GL30.glCompileShader(fragmentShader);
        
        // Link program
        int program = GL30.glCreateProgram();
        GL30.glAttachShader(program, vertexShader);
        GL30.glAttachShader(program, fragmentShader);
        GL30.glLinkProgram(program);
        
        // Clean up shaders (they're linked into the program now)
        GL30.glDeleteShader(vertexShader);
        GL30.glDeleteShader(fragmentShader);
        
        return program;
    }
    
    private int createCubeVAO() {
        // Simple cube vertices
        float[] vertices = {
            // Front face
            -0.5f, -0.5f,  0.5f,   0.5f, -0.5f,  0.5f,   0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,   0.5f,  0.5f,  0.5f,  -0.5f,  0.5f,  0.5f,
            // Back face
            -0.5f, -0.5f, -0.5f,   0.5f,  0.5f, -0.5f,   0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,  -0.5f,  0.5f, -0.5f,   0.5f,  0.5f, -0.5f,
            // Top face
            -0.5f,  0.5f, -0.5f,  -0.5f,  0.5f,  0.5f,   0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f, -0.5f,   0.5f,  0.5f,  0.5f,   0.5f,  0.5f, -0.5f,
            // Bottom face
            -0.5f, -0.5f, -0.5f,   0.5f, -0.5f,  0.5f,  -0.5f, -0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,   0.5f, -0.5f, -0.5f,   0.5f, -0.5f,  0.5f,
            // Right face
             0.5f, -0.5f, -0.5f,   0.5f,  0.5f,  0.5f,   0.5f, -0.5f,  0.5f,
             0.5f, -0.5f, -0.5f,   0.5f,  0.5f, -0.5f,   0.5f,  0.5f,  0.5f,
            // Left face
            -0.5f, -0.5f, -0.5f,  -0.5f, -0.5f,  0.5f,  -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,  -0.5f,  0.5f,  0.5f,  -0.5f,  0.5f, -0.5f
        };
        
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);
        
        int vbo = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW);
        
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        GL30.glEnableVertexAttribArray(0);
        
        GL30.glBindVertexArray(0);
        return vao;
    }
}
```

---

## API Reference

### XRRenderer Key Methods

| Method | Description |
|--------|-------------|
| `setupGLContext()` | Creates GLFW window and initializes OpenGL |
| `init()` | Called automatically; sets up resolution, eyes, hidden area, then calls `onInit()` |
| `onInit()` | Override to initialize your scene |
| `getCurrentScene()` | Override to return your active VRScene |
| `createTexture(...)` | Override to create framebuffer textures |
| `getResolutionWidth()` | Eye texture width from VR runtime |
| `getResolutionHeight()` | Eye texture height from VR runtime |
| `getTextureLeftEye()` | Current left eye framebuffer texture |
| `getTextureRightEye()` | Current right eye framebuffer texture |
| `getHiddenAreaVertices(EyeType)` | Hidden area mesh for stencil optimization |

### XRScene Key Methods

| Method | Description |
|--------|-------------|
| `init()` | Called automatically; creates eye cameras, then calls `onInit()` |
| `onInit()` | Override to initialize shaders, geometry, textures |
| `render(IRenderContext)` | Called automatically; binds framebuffers, calls `updateEyeTexture()` for each eye |
| `updateEyeTexture(EyeType)` | Override to implement your rendering |
| `setupMvp()` | Override to customize view/projection matrix updates |
| `getLeftEyeCamera()` | Access left eye camera (view & projection matrices) |
| `getRightEyeCamera()` | Access right eye camera (view & projection matrices) |
| `destroy()` | Override to clean up resources |

### XREyeCamera Key Methods

| Method | Description |
|--------|-------------|
| `getViewMatrix()` | Current view matrix from HMD tracking |
| `getProjectionMatrix()` | Current projection matrix from VR runtime FOV |
| `updateViewMatrix(EyeType)` | Update view matrix (called automatically) |
| `updateProjectionMatrix(EyeType, near, far)` | Update projection with custom clip planes |
