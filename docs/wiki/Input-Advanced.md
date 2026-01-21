## Contents

1. [Custom Action Sets](#custom-action-sets)
2. [Profile Sets](#profile-sets)
3. [Creating Custom Actions](#creating-custom-actions)
4. [Action Listeners](#action-listeners)

## Custom Action Sets

If the built-in `ProfileSetHolder` doesn't fit your needs, you can create custom action sets.

### Creating an action set

Extend `XRActionSet`:

```java
public class CustomActionSet extends XRActionSet {
    private FloatButtonMultiAction triggerAction;
    private Vec2MultiAction joystickAction;
    
    public CustomActionSet(XRProvider provider) {
        super(provider, 
            "custom_actions",      // internal name
            "Custom Actions",       // display name
            0                       // priority
        );
    }
    
    @Override
    protected List<XRAction> loadActions(XRProvider provider) {
        triggerAction = new FloatButtonMultiAction(
            provider, this,
            new ActionIdentifier("custom", "trigger"),
            "Trigger"
        );
        
        joystickAction = new Vec2MultiAction(
            provider, this,
            new ActionIdentifier("custom", "joystick"),
            "Joystick"
        );
        
        return List.of(triggerAction, joystickAction);
    }
    
    public FloatButtonMultiAction getTriggerAction() {
        return triggerAction;
    }
    
    public Vec2MultiAction getJoystickAction() {
        return joystickAction;
    }
}
```

### Action types

AtumVR provides these action types:

- **FloatButtonMultiAction** - analog triggers (0.0 to 1.0 with button threshold)
- **BoolButtonMultiAction** - simple boolean buttons
- **Vec2MultiAction** - 2D inputs like joysticks
- **PoseMultiAction** - position/rotation data
- **HapticPulseAction** - output action for vibration

The "Multi" variants support both left and right hand subactions.

## Profile Sets

Different VR controllers have different button layouts. Profile sets handle these differences.

### Using ProfileSetHolder

`ProfileSetHolder` manages action sets for different controller profiles:

```java
ProfileSetHolder profileSetHolder = new ProfileSetHolder(vrProvider);

// Get the currently active profile set (based on connected controllers)
XRProfileSet activeProfile = profileSetHolder.getActiveProfileSet();

// Get specific profile set
XRProfileSet oculusProfile = profileSetHolder.getProfileSet(
    XRInteractionProfile.OCULUS_TOUCH
);
```

### Supported profiles

AtumVR includes built-in support for:

- Oculus Touch controllers
- Valve Index controllers  
- HTC Vive controllers
- HP Mixed Reality controllers
- And more...

The `ProfileSetHolder` automatically detects which controllers are connected and uses the appropriate profile.

### Shared actions

Some actions are common across all controllers (like hand poses). These are in the `SharedActionSet`:

```java
SharedActionSet shared = profileSetHolder.getSharedSet();
PoseMultiAction aimPose = shared.getHandPoseAim();
PoseMultiAction gripPose = shared.getHandPoseGrip();
HapticPulseAction haptic = shared.getHapticPulse();
```

## Creating Custom Actions

### Single-hand action

For actions that only apply to one hand:

```java
public class CustomSingleAction extends XRSingleAction<VRActionDataButton> {
    public CustomSingleAction(XRProvider provider, XRActionSet actionSet) {
        super(provider, actionSet,
            new ActionIdentifier("custom", "action"),
            "Custom Action",
            XRInputActionType.BOOLEAN
        );
    }
    
    @Override
    public String getDefaultBindings(XRInteractionProfile profile) {
        return switch (profile) {
            case OCULUS_TOUCH -> "/user/hand/left/input/x/click";
            case INDEX_CONTROLLER -> "/user/hand/left/input/a/click";
            default -> null;
        };
    }
}
```

### Multi-hand action

For actions that apply to both hands:

```java
public class CustomMultiAction extends XRMultiAction<VRActionDataButton> {
    public CustomMultiAction(XRProvider provider, XRActionSet actionSet) {
        super(provider, actionSet,
            new ActionIdentifier("custom", "grab"),
            "Grab",
            XRInputActionType.BOOLEAN
        );
    }
    
    @Override
    protected SubAction<VRActionDataButton> createSubAction(ControllerType type) {
        return new BoolButtonSubAction(type) {
            @Override
            public String getDefaultBindings(XRInteractionProfile profile) {
                String hand = type == ControllerType.LEFT ? "left" : "right";
                return switch (profile) {
                    case OCULUS_TOUCH -> "/user/hand/" + hand + "/input/squeeze/value";
                    case INDEX_CONTROLLER -> "/user/hand/" + hand + "/input/squeeze/force";
                    default -> null;
                };
            }
        };
    }
}
```

## Action Listeners

You can register a listener to be notified when any action changes:

```java
inputHandler.setActionListener(actionName -> {
    System.out.println("Action triggered: " + actionName);
});
```

### Custom update logic

Override the `update()` method in your input handler for custom logic:

```java
@Override
public void update() {
    super.update(); // Important: call super first
    
    var profileSet = profileSetHolder.getActiveProfileSet();
    if (profileSet == null) return;
    
    // Check for specific input combinations
    if (profileSet.getTriggerValue().getHandSubaction(ControllerType.LEFT).isPressed() &&
        profileSet.getTriggerValue().getHandSubaction(ControllerType.RIGHT).isPressed()) {
        // Both triggers pressed
        onBothTriggersPressed();
    }
}
```

### Action change detection

To detect when a button state changes (not just when it's pressed):

```java
VRActionDataButton button = profileSet.getButton("a_button");
if (button.isButtonChanged()) {
    if (button.isPressed()) {
        // Button was just pressed
    } else {
        // Button was just released
    }
}
```
