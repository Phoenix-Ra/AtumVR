package me.phoenixra.atumvr.example.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.VRDeviceController;
import me.phoenixra.atumvr.core.input.action.profileset.ProfileSetHolder;
import me.phoenixra.atumvr.example.ExampleHandEnum;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.device.XRDevice;
import me.phoenixra.atumvr.core.input.device.XRDeviceController;
import me.phoenixra.atumvr.core.input.device.XRDeviceHMD;
import me.phoenixra.atumvr.core.input.XRInputHandler;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import org.lwjgl.system.MemoryStack;

import java.util.List;

public class ExampleVRInputHandler extends XRInputHandler {
    @Getter
    private ProfileSetHolder profileSetHolder;

    private final ExampleHandEnum pulsatingHand = ExampleHandEnum.MAIN;
    @Getter
    private final ExampleHandEnum scaleHand = ExampleHandEnum.OFFHAND;

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

    @Override
    public void update() {
        super.update();
        ControllerType type = pulsatingHand.asType();

        var profileSet = profileSetHolder.getActiveProfileSet();
        if(profileSet == null){
            return;
        }
        if(profileSet.getTriggerValue().getHandSubaction(type).isPressed()){
            getDevice(VRDeviceController.getDefaultId(type), VRDeviceController.class)
                    .triggerHapticPulse(
                            160f,
                            1.0F,
                            0.1f
                    );
        }
    }
}
