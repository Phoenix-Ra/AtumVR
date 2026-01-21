## Contents

1. [How library is organized](#how-library-is-organized)
2. [Entry point](#entry-point)
3. [Lifecycle](#lifecycle)

## How library is organized

AtumVR is split into three Gradle modules:

- **api** — Public-facing interfaces and structure. Also includes utility/helper classes used across the project.
- **core** — The actual VR implementation and runtime logic.
- **example** — A reference project to help you get started. You **should not** depend on this module.

## Entry point

**VRProvider** is an entry point for your application to manage VR

Use one of these abstract classes from **core** module:

- `XRProvider` - OpenXR
- `OpenVRProvider` - OpenVR (planned in future releases)

you will require to implement methods that create handlers for rendering, input and state.

For OpenXR use these abstract classes for handlers:

- `XRRenderer`
- `XRInputHandler`
- `XRState`

Feel free to override any of their logic if needed

More details and integration examples are covered in the next pages of the **Start** section.

## Lifecycle

### VRState

VR Provider has an associated VRState object:

- `XRState` - OpenXR
- `OpenVRState` - OpenVR (planned in future releases)

`VRState` represents the current VR session status (_initialized_, _active_, _focused_).  
You'll use it often, especially in applications that support both VR and non-VR modes.

### Setting up lifecycle

1. **Initialize the provider**  
   Your `VRProvider` must be initialized before doing anything VR-related.

2. **Sync state at the start of the app loop**  
   When VRState indicates "_initialized_" is true, call `syncState()` at the **very beginning** of your main loop.

3. **When VRState indicates "_active_" is true, call these methods in the following order:**

   1. `syncState()` - at the beginning of main loop
   2. `startFrame()` - at the beginning of main loop
   3. `render()` - somewhere during rendering stage in your main loop (where its convenient for your case)
   4. `postRender()` - up to you, its optional

### Notes about `startFrame()`

`startFrame()` will **pause your thread** until the next VR frame is available from the VR runtime.  
In other words, it effectively controls the refresh rate. If your application already has its own frame timing / limiter, you should **disable it while in VR mode** to avoid fighting the VR runtime.

### Focus handling

In addition to `active`, there is a separate flag: `focused`.

When true, it means the user is interacting with your app in VR

If false, it may mean:

- VR is not active
- The user is interacting with the VR runtime menu or something else not related to your app in VR.

It is **highly recommended** to limit rendering when `focused` is `false` (for example, don't render VR hands/controllers while unfocused).

### Destroying the VR session

It is pretty simple, just call the method `destroy()` in VRProvider **after postRender() or before startFrame()**.

You are free to initialize the VRProvider again using the same instance.
