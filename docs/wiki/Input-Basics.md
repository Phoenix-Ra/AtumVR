## Contents

1. [How input works](#how-input-works)
2. [Setting up handler](#setting-up-handler)
3. [Devices](#devices)
4. [Actions](#actions)

## How input works

AtumVR links with most of actions available in VR runtime for user's controllers. So, you don't have to understand how VR runtime works with that.

Your part is to use profile set, VRDevice and of course input handler.

The input system is based on OpenXR's action-based input model:
- **Action Sets** - groups of related actions
- **Actions** - individual inputs like button presses, triggers, joysticks
- **Devices** - physical VR devices like HMD and controllers

## Setting up handler

Create a class that extends `XRInputHandler`. You need to implement two methods:

- `generateActionSets(MemoryStack stack)` - returns list of action sets to use
- `generateDevices(MemoryStack stack)` - returns list of VR devices to track

Example:
```java
public class ExampleVRInputHandler extends XRInputHandler {
    private ProfileSetHolder profileSetHolder;

    public ExampleVRInputHandler(XRProvider provider) {
        super(provider);
    }

    @Override
    protected List<? extends XRActionSet> generateActionSets(MemoryStack stack) {
        profileSetHolder = new ProfileSetHolder(getVrProvider());
        return profileSetHolder.getAllSets();
    }

    @Override
    protected List<? extends XRDevice> generateDevices(MemoryStack stack) {
        return List.of(
            new XRDeviceHMD(getVrProvider()),
            new XRDeviceController(
                getVrProvider(),
                ControllerType.LEFT,
                profileSetHolder.getSharedSet().getHandPoseAim(),
                profileSetHolder.getSharedSet().getHandPoseGrip(),
                profileSetHolder.getSharedSet().getHapticPulse()
            ),
            new XRDeviceController(
                getVrProvider(),
                ControllerType.RIGHT,
                profileSetHolder.getSharedSet().getHandPoseAim(),
                profileSetHolder.getSharedSet().getHandPoseGrip(),
                profileSetHolder.getSharedSet().getHapticPulse()
            )
        );
    }
}
```

And in your VRProvider:
```java
@Override
public @NotNull XRInputHandler createInputHandler() {
    return new ExampleVRInputHandler(this);
}
```

## Devices

AtumVR provides these built-in device types:

### VRDeviceHMD
The head-mounted display. Access it using:
```java
VRDeviceHMD hmd = inputHandler.getDevice(VRDeviceHMD.ID, VRDeviceHMD.class);
```

Provides:
- `getPose()` - HMD position and orientation
- `isActive()` - whether HMD is being tracked

### VRDeviceController
Left and right controllers. Access them using:
```java
VRDeviceController leftController = inputHandler.getDevice(
    VRDeviceController.ID_LEFT, 
    VRDeviceController.class
);
VRDeviceController rightController = inputHandler.getDevice(
    VRDeviceController.ID_RIGHT, 
    VRDeviceController.class
);
```

Each controller provides:
- `getPose()` - aim pose (pointing direction)
- `getGripPose()` - grip pose (where you hold the controller)
- `isActive()` - whether controller is being tracked
- `isGripActive()` - whether grip pose is valid
- `getType()` - `ControllerType.LEFT` or `ControllerType.RIGHT`
- `triggerHapticPulse(...)` - trigger vibration feedback

### VRPose

All devices provide a `VRPose` that contains:
- `position()` - Vector3fc with x, y, z coordinates
- `orientation()` - Quaternionfc rotation
- `matrix()` - 4x4 transformation matrix

## Actions

Actions represent user inputs. AtumVR uses `ProfileSetHolder` to manage actions for different controller types.

### Getting button states
```java
var profileSet = profileSetHolder.getActiveProfileSet();
if(profileSet != null) {
    // Check trigger press
    if(profileSet.getTriggerValue().getHandSubaction(ControllerType.LEFT).isPressed()) {
        // Left trigger is pressed
    }
}
```

### Action data types

- `VRActionDataButton` - for buttons and triggers
  - `isActive()` - is the input available
  - `isPressed()` - is button currently pressed
  - `isButtonChanged()` - did state change this frame
  
- `VRActionDataVec2` - for joysticks and touchpads
  - `getX()`, `getY()` - axis values (-1 to 1)

### Haptic feedback
```java
VRDeviceController controller = inputHandler.getDevice(
    VRDeviceController.ID_RIGHT, 
    VRDeviceController.class
);
controller.triggerHapticPulse(0.1f); // 100ms pulse
// or with more control:
controller.triggerHapticPulse(160f, 1.0f, 0.1f); // frequency, amplitude, duration
```
