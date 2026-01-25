package me.phoenixra.atumvr.example.input;

import lombok.Getter;
import me.phoenixra.atumvr.core.input.device.VRDeviceController;
import me.phoenixra.atumvr.core.input.profile.VRProfileManager;
import me.phoenixra.atumvr.example.ExampleHandEnum;
import me.phoenixra.atumvr.core.enums.ControllerType;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.input.device.VRDevice;
import me.phoenixra.atumvr.core.input.device.VRDeviceHMD;
import me.phoenixra.atumvr.core.input.VRInputHandler;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.util.List;

public class ExampleVRInputHandler extends VRInputHandler {
    @Getter
    private VRProfileManager profileSetHolder;

    private final ExampleHandEnum pulsatingHand = ExampleHandEnum.MAIN;
    @Getter
    private final ExampleHandEnum scaleHand = ExampleHandEnum.OFFHAND;

    public ExampleVRInputHandler(VRProvider vrProvider) {
        super(vrProvider);
    }

    @Override
    protected @NotNull List<? extends VRActionSet> generateActionSets(@NotNull MemoryStack stack) {
        profileSetHolder = new VRProfileManager(getVrProvider());

        return profileSetHolder.getAllActionSets();
    }

    @Override
    protected @NotNull List<? extends VRDevice> generateDevices(@NotNull MemoryStack stack) {
        return List.of(
                new VRDeviceHMD(getVrProvider()),
                new me.phoenixra.atumvr.core.input.device.VRDeviceController(
                        getVrProvider(),
                        ControllerType.LEFT,
                        profileSetHolder.getSharedSet().getHandPoseAim(),
                        profileSetHolder.getSharedSet().getHandPoseGrip(),
                        profileSetHolder.getSharedSet().getHapticPulse()
                ),
                new me.phoenixra.atumvr.core.input.device.VRDeviceController(
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

        var profileSet = profileSetHolder.getActiveProfile();
        if(profileSet == null){
            return;
        }

        if(profileSet.getTriggerValue().getHandSubaction(type).isPressed()){
            getDevice(VRDeviceController.getId(type), VRDeviceController.class)
                    .triggerHapticPulse(
                            160f,
                            1.0F,
                            0.1f
                    );
        }
    }
}
