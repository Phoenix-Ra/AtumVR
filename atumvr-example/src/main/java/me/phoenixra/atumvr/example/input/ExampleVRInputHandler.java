package me.phoenixra.atumvr.example.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.VRDeviceController;
import me.phoenixra.atumvr.core.input.device.XRDeviceController;
import me.phoenixra.atumvr.core.input.profile.XRProfileManager;
import me.phoenixra.atumvr.example.ExampleHandEnum;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.device.XRDevice;
import me.phoenixra.atumvr.core.input.device.XRDeviceHMD;
import me.phoenixra.atumvr.core.input.XRInputHandler;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.util.List;

public class ExampleVRInputHandler extends XRInputHandler {
    @Getter
    private XRProfileManager profileSetHolder;

    private final ExampleHandEnum pulsatingHand = ExampleHandEnum.MAIN;
    @Getter
    private final ExampleHandEnum scaleHand = ExampleHandEnum.OFFHAND;

    public ExampleVRInputHandler(XRProvider vrProvider) {
        super(vrProvider);
    }

    @Override
    protected @NotNull List<? extends XRActionSet> generateActionSets(@NotNull MemoryStack stack) {
        profileSetHolder = new XRProfileManager(getVrProvider());

        return profileSetHolder.getAllActionSets();
    }

    @Override
    protected @NotNull List<? extends XRDevice> generateDevices(@NotNull MemoryStack stack) {
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

        var profileSet = profileSetHolder.getActiveProfile();
        if(profileSet == null){
            return;
        }

        if(profileSet.getTriggerValue().getHandSubaction(type).isPressed()){
            getDevice(VRDeviceController.getId(type), XRDeviceController.class)
                    .triggerHapticPulse(
                            160f,
                            1.0F,
                            0.1f
                    );
        }
    }
}
