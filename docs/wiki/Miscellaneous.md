## Contents

1. [Logging](#logging)
2. [Error Handling](#error-handling)
3. [State Change Callbacks](#state-change-callbacks)
4. [OpenXR Extensions](#openxr-extensions)
5. [Tips and Best Practices](#tips-and-best-practices)

## Logging

AtumVR uses `VRLogger` interface for logging. You need to provide an implementation when creating your VRProvider.

### Creating a logger

```java
VRLogger logger = new VRLogger() {
    @Override
    public void logInfo(String message) {
        System.out.println("[AtumVR INFO] " + message);
    }
    
    @Override
    public void logWarning(String message) {
        System.out.println("[AtumVR WARN] " + message);
    }
    
    @Override
    public void logError(String message) {
        System.err.println("[AtumVR ERROR] " + message);
    }
};

ExampleVRProvider provider = new ExampleVRProvider(logger);
```

### Using the logger

Access the logger anywhere via the provider:
```java
vrProvider.getLogger().logInfo("VR initialized successfully");
```

## Error Handling

### VRException

AtumVR throws `VRException` for VR-related errors:

```java
try {
    vrProvider.initializeVR();
} catch (VRException e) {
    logger.logError("Failed to initialize VR: " + e.getMessage());
    // Handle gracefully - perhaps fall back to non-VR mode
}
```

### OpenXR error checking

For direct OpenXR calls, use the built-in error checker:

```java
vrProvider.checkXRError(xrResult, "operationName", "additional context");
```

This will throw `VRException` if the result indicates an error.

### Getting error descriptions

```java
String description = vrProvider.getXRActionResult(xrResultCode);
```

## State Change Callbacks

Override `onStateChanged()` in your VRProvider to respond to VR runtime state changes:

```java
@Override
public void onStateChanged(XRSessionStateChange state) {
    switch (state) {
        case READY:
            logger.logInfo("VR session ready");
            break;
        case FOCUSED:
            logger.logInfo("User is focused on VR app");
            onVRFocused();
            break;
        case VISIBLE:
            logger.logInfo("VR app is visible but not focused");
            break;
        case STOPPING:
            logger.logInfo("VR session stopping");
            onVRStopping();
            break;
        case EXITING:
            logger.logInfo("VR session exiting");
            break;
    }
}
```

### Common state transitions

1. **Initialization**: `IDLE` → `READY` → `SYNCHRONIZED` → `VISIBLE` → `FOCUSED`
2. **Dashboard opened**: `FOCUSED` → `VISIBLE`  
3. **App backgrounded**: `VISIBLE` → `SYNCHRONIZED`
4. **Shutdown**: `FOCUSED/VISIBLE` → `STOPPING` → `EXITING`

## OpenXR Extensions

### Default extensions

AtumVR enables these extensions by default:
- `XR_EXT_HP_MIXED_REALITY_CONTROLLER` - HP controller support
- `XR_HTC_VIVE_COSMOS_CONTROLLER` - Vive Cosmos controller support
- `XR_BD_CONTROLLER_INTERACTION` - Pico controller support

### Adding custom extensions

Override `getXRAppExtensions()` in your VRProvider:

```java
@Override
public List<String> getXRAppExtensions() {
    List<String> extensions = new ArrayList<>(super.getXRAppExtensions());
    extensions.add("XR_FB_hand_tracking");
    extensions.add("XR_EXT_eye_gaze_interaction");
    return extensions;
}
```

**Note**: Extensions must be supported by the VR runtime. Check availability before using extension features.

## Tips and Best Practices

### Performance

1. **Keep frame times low** - VR requires consistent high framerates (72-144 FPS)
2. **Use hidden area mesh** - Skip rendering to areas the user can't see
3. **Limit draw calls** - Batch geometry where possible
4. **Avoid allocations in render loop** - Pre-allocate matrices and vectors

### Comfort

1. **Never take camera control away** - Always render from the HMD pose
2. **Avoid rapid movement** - Use teleportation or smooth locomotion with vignette
3. **Maintain consistent frame rate** - Stutters cause discomfort

### Code organization

```java
// Good: Check VR state before operations
if (vrProvider.getState().isActive()) {
    vrProvider.startFrame();
    vrProvider.render(context);
}

// Good: Proper cleanup
@Override
public void destroy() {
    vrProvider.destroy(); // Releases all VR resources
}
```

### Debugging

1. Check the VR runtime's own debug/log tools (SteamVR console, Oculus Debug Tool)
2. Use `VRLogger` extensively during development
3. Test on multiple headsets if possible - behavior can vary

### Resource management

```java
// Always destroy VR resources when done
@Override
public void onClose() {
    if (vrProvider.getState().isInitialized()) {
        vrProvider.destroy();
    }
}
```

### Thread safety

- VR methods should be called from the main/render thread
- Don't call `startFrame()` or `render()` from background threads
- Input updates happen in `startFrame()` - read input after that call
