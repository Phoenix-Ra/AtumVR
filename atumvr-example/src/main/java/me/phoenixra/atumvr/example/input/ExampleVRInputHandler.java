package me.phoenixra.atumvr.example.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.VRDeviceController;
import me.phoenixra.atumvr.core.input.action.profileset.ProfileSetHolder;
import me.phoenixra.atumvr.core.input.action.profileset.types.*;
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
    private ProfileSetHolder profileSetHolder;

    private final ExampleHandEnum pulsatingHand = ExampleHandEnum.MAIN;
    @Getter
    private final ExampleHandEnum scaleHand = ExampleHandEnum.OFFHAND;

    public ExampleVRInputHandler(OpenXRProvider provider) {
        super(provider);
    }

    @Override
    protected List<? extends OpenXRActionSet> generateActionSets(MemoryStack stack) {
        profileSetHolder = new ProfileSetHolder(getVrProvider());

        return profileSetHolder.getAllSets();
    }

    @Override
    protected List<? extends OpenXRDevice> generateDevices(MemoryStack stack) {
        return List.of(
                new OpenXRDeviceHMD(getVrProvider()),
                new OpenXRDeviceController(
                        getVrProvider(),
                        ControllerType.LEFT,
                        profileSetHolder.getSharedSet().getHandPoseAim(),
                        profileSetHolder.getSharedSet().getHandPoseGrip(),
                        profileSetHolder.getSharedSet().getHapticPulse()
                ),
                new OpenXRDeviceController(
                        getVrProvider(),
                        ControllerType.RIGHT,
                        profileSetHolder.getSharedSet().getHandPoseAim(),
                        profileSetHolder.getSharedSet().getHandPoseGrip(),
                        profileSetHolder.getSharedSet().getHapticPulse()
                )
        );
    }

    @Override
    public void update() {
        super.update();
        ControllerType type = pulsatingHand.asType();

        var profileSet = profileSetHolder.getActiveProfileSet();
        if(profileSet == null){
            return;
        }
        if(profileSet.getTriggerValue().getButtonState(type).pressed()){
            getDevice(VRDeviceController.getDefaultId(type), VRDeviceController.class)
                    .triggerHapticPulse(
                            160f,
                            1.0F,
                            0.1f
                    );
        }
    }
}
