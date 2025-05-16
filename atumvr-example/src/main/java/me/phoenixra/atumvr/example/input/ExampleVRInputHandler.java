package me.phoenixra.atumvr.example.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.VRDeviceController;
import me.phoenixra.atumvr.example.ExampleHandEnum;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.input.device.OpenXRDevice;
import me.phoenixra.atumvr.core.input.device.OpenXRDeviceController;
import me.phoenixra.atumvr.core.input.device.OpenXRDeviceHMD;
import me.phoenixra.atumvr.core.input.OpenXRInputHandler;
import me.phoenixra.atumvr.core.input.action.OpenXRActionSet;
import org.lwjgl.system.MemoryStack;

import java.util.List;

public class ExampleVRInputHandler extends OpenXRInputHandler {
    @Getter
    private ExampleActionSet actionSet;

    private final ExampleHandEnum pulsatingHand = ExampleHandEnum.MAIN;
    @Getter
    private final ExampleHandEnum scaleHand = ExampleHandEnum.OFFHAND;

    public ExampleVRInputHandler(OpenXRProvider provider) {
        super(provider);
    }

    @Override
    protected List<OpenXRActionSet> generateActionSets(MemoryStack stack) {
        actionSet = new ExampleActionSet(getVrProvider());
        return List.of(actionSet);
    }

    @Override
    protected List<OpenXRDevice> generateDevices(MemoryStack stack) {
        return List.of(
                new OpenXRDeviceHMD(getVrProvider()),
                new OpenXRDeviceController(
                        getVrProvider(),
                        ControllerType.LEFT,
                        actionSet.getAimAction(),
                        actionSet.getGripAction(),
                        actionSet.getHapticPulseAction()
                ),
                new OpenXRDeviceController(
                        getVrProvider(),
                        ControllerType.RIGHT,
                        actionSet.getAimAction(),
                        actionSet.getGripAction(),
                        actionSet.getHapticPulseAction()
                )
        );
    }

    @Override
    public void update() {
        super.update();
        ControllerType type = pulsatingHand.asType();
        if(actionSet.getTriggerValueAction().getHandSubaction(type).getCurrentState() > 0.5){
            getDevice(VRDeviceController.getDefaultId(type), VRDeviceController.class)
                    .triggerHapticPulse(
                            160f,
                            1.0F,
                            0.1f
                    );
        }
    }
}
