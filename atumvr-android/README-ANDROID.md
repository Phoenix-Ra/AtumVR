# atumvr-android — Quest 3 / standalone XR runtime (scaffold)

This module is the Android-side counterpart to `atumvr-core`. Because the
existing core is bound to LWJGL desktop natives (which don't load on
Android), the Quest port doesn't share the OpenXR call sites in `atumvr-core`
— it owns its own native (NDK) OpenXR implementation and exposes it through
a thin Java bridge. Only `atumvr-api` (platform-neutral types: enums,
poses, controller profiles) is shared.

## Status

**Step 1 — NDK + CMake plumbing complete.** This commit lands:

- `build.gradle` configured for `com.android.library` + `externalNativeBuild`
  with CMake 3.22 and NDK r26 LTS, arm64-v8a only.
- `src/main/cpp/CMakeLists.txt` building `libatumvr_native.so`, linking
  EGL / GLESv3 / log / android. Optionally links the Meta OpenXR Mobile
  loader via prefab when `ATUMVR_WITH_OPENXR_LOADER` is on.
- `src/main/cpp/atumvr_jni.cpp` with `JNI_OnLoad`, an ABI-version probe,
  and a build-info string. No OpenXR calls yet.
- `me.phoenixra.atumvr.android.nativebridge.NativeXRBridge` Java surface
  that loads the shared library and exposes the probes for sample apps.

Still **scaffolded** (`AndroidGLESGraphicsBackend`, `XrActivity`) — these
hold the eventual GLES backend, but their `LWJGL`-typed signatures will be
refactored in a later step once `atumvr-api` is split into a
platform-neutral subset and an LWJGL-bound subset.

## Enabling the module

`atumvr-android` is intentionally NOT included in the root `settings.gradle`
yet. The desktop build keeps working in CI environments without the Android
toolchain. To enable it locally:

1. Install Android Studio (or the SDK + cmdline-tools) and set
   `ANDROID_HOME`. Accept SDK licenses (`yes | sdkmanager --licenses`).
2. Install **NDK r26.1** and **CMake 3.22** through `sdkmanager` (or the
   IDE's SDK Manager). The Gradle config pins to `26.1.10909125`; override
   `ndkVersion` in `atumvr-android/build.gradle` if your machine has a
   different NDK installed.
3. Add the Android Gradle Plugin to the root `build.gradle`:

   ```groovy
   buildscript {
       repositories { google(); mavenCentral() }
       dependencies { classpath 'com.android.tools.build:gradle:8.2.0' }
   }
   allprojects { repositories { google() } }
   ```

4. Uncomment / add `include 'atumvr-android'` in `settings.gradle`.
5. (Step 2+) Add the Meta OpenXR Mobile loader dependency:

   ```groovy
   // atumvr-android/build.gradle
   dependencies {
       implementation 'org.khronos.openxr:openxr_loader_for_android:1.1.36'
   }
   android {
       defaultConfig {
           externalNativeBuild {
               cmake {
                   arguments '-DATUMVR_WITH_OPENXR_LOADER=ON'
               }
           }
       }
   }
   ```

   The CMake module already knows how to consume this AAR via prefab.

## Verifying the native build

Once the module is enabled, the following should produce
`libatumvr_native.so` packed inside the AAR:

```bash
./gradlew :atumvr-android:assembleDebug
```

Quick smoke test from a consuming app:

```java
import me.phoenixra.atumvr.android.nativebridge.NativeXRBridge;

// In your Activity#onCreate:
NativeXRBridge.verifyAbi();
Log.i("AtumVR", NativeXRBridge.buildInfo());
// Expected logcat:
//   I/AtumVR-Native: AtumVR native loaded (variant=debug, abi=1, openxr=stub)
//   I/AtumVR:        atumvr_native debug (openxr=stub)
```

If the linker complains about missing `libopenxr_loader.so`, you're past
step 1 and into step 2 — at that point flip `ATUMVR_WITH_OPENXR_LOADER` on
and add the Meta AAR.

## Architecture

```
atumvr-api  (platform-neutral)
├── input profiles, math, enums, poses
└── (future) split out atumvr-api-openxr for LWJGL-bound types

atumvr-core (desktop, JVM)
└── DesktopOpenGLGraphicsBackend  (GLFW + KHR_opengl_enable, LWJGL)

atumvr-android (Quest, Android)
├── src/main/cpp/                    (this step)
│   ├── CMakeLists.txt
│   └── atumvr_jni.cpp               → libatumvr_native.so
├── nativebridge/NativeXRBridge      (Java surface over the native lib)
├── activity/XrActivity              (NativeActivity + EGL bring-up; scaffold)
└── rendering/backend/AndroidGLESGraphicsBackend (GLES backend; scaffold)
```

## Roadmap

1. **NDK + CMake plumbing.** ✅ (this commit)
2. **OpenXR bring-up in native.** Loader init via
   `xrInitializeLoaderKHR`, instance, system, GLES session via
   `XrGraphicsBindingOpenGLESAndroidKHR`, swapchain, frame loop.
3. **Java render-loop wiring.** `XrActivity` drives EGL + a render thread
   that calls `NativeXRBridge.beginFrame()` / `submitFrame()`; clear-color
   per eye to validate the pipeline end-to-end on Quest 3.
4. **Input / pose surface.** Expose HMD + controller poses from native into
   the existing `AtumVRInputHandler` API in `atumvr-api`.
5. **GL → GLES rendering port.** Move `XRTexture` / `GLUtils` / framebuffer
   setup behind a backend hook so the example's cubes render on-device.
6. **`atumvr-android-example`.** Manifest + launcher activity + a tiny
   `XRProvider` subclass that ties everything together for sideloading.
