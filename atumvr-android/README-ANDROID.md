# atumvr-android — Quest 3 / standalone XR runtime (scaffold)

This module is the Android-side counterpart to `atumvr-core`. It plugs an
OpenGL ES graphics backend into the new `XRGraphicsBackend` abstraction
defined in `atumvr-api`, so the existing `XRRenderer` / `XRSession` /
`XRSwapChain` code in `atumvr-core` can drive a Quest 3 standalone build.

## Status

**Scaffold only.** The Java/Kotlin glue, manifest, `XrActivity` and
`AndroidGLESGraphicsBackend` skeleton are in place; the four mobile-only
OpenXR entry points (`xrInitializeLoaderKHR`,
`xrGetOpenGLESGraphicsRequirementsKHR`,
`XrGraphicsBindingOpenGLESAndroidKHR`,
`XrSwapchainImageOpenGLESKHR`) are not implemented yet — LWJGL doesn't ship
those bindings on its desktop artifacts, so they need to be reached either
via a small JNI shim around the Meta OpenXR Mobile loader, or via a custom
LWJGL build with the `openxr-mobile` extensions enabled.

## Enabling the module

`atumvr-android` is intentionally NOT included in the root `settings.gradle`
yet. The desktop build keeps working in CI environments without the Android
toolchain. To enable it:

1. Install the Android SDK locally and set `ANDROID_HOME`.
2. Add the Android Gradle Plugin to the root `build.gradle`:

   ```groovy
   buildscript {
       dependencies {
           classpath 'com.android.tools.build:gradle:8.2.0'
       }
       repositories {
           google()
           mavenCentral()
       }
   }
   ```

3. Add Google's Maven repo under `allprojects { repositories { ... } }`.
4. Uncomment / add `include 'atumvr-android'` in `settings.gradle`.
5. Add the Meta OpenXR Mobile loader dependency in
   `atumvr-android/build.gradle`:

   ```groovy
   implementation 'org.khronos.openxr:openxr_loader_for_android:1.1.36'
   ```

6. Sync. The module should configure under the Android library plugin.

## Architecture

```
atumvr-api
└── XRGraphicsBackend       (interface)
    XRSwapchainImages       (interface)

atumvr-core (desktop, JVM)
└── DesktopOpenGLGraphicsBackend  (GLFW + KHR_opengl_enable)

atumvr-android (Quest, Android)
├── XrActivity                    (NativeActivity + EGL bring-up)
└── AndroidGLESGraphicsBackend    (KHR_opengl_es_enable + Android binding)
```

`XRProvider#createGraphicsBackend()` is overridden on the Quest provider to
return an `AndroidGLESGraphicsBackend`. `XRRenderer`, `XRSystem`,
`XRSwapChain`, and `XRInstance` already route everything graphics-API
specific through the backend, so no further changes are required in
`atumvr-core` once the Android-side methods are filled in.

## Outstanding work

1. JNI shim (or LWJGL fork) for the four `XR_KHR_opengl_es_enable` /
   `XR_KHR_android_create_instance` entry points.
2. `System.loadLibrary("openxr_loader")` and the
   `xrInitializeLoaderKHR(jvm, activity)` handshake at the top of
   `XrActivity#onCreate`.
3. Real `EGLObjectHandle#getNativeHandle()` extraction in
   `XrActivity#handleToLong` (currently a placeholder).
4. Replacement for `atumvr-core`'s `GLUtils#checkGLError` and `XRTexture`'s
   `GL30.gl*` calls — these are still desktop GL. Either:
   - move the GL calls behind a per-backend texture factory
     (`XRGraphicsBackend#createTexture`), or
   - duplicate `XRTexture` as `AndroidXRTexture` using `GLES30.gl*` and
     have the Quest renderer override `XRRenderer#createTexture`.
5. Quest-specific `XRProvider` subclass (under e.g. `atumvr-android-example`)
   that wires the activity, the backend, and the renderer.
