package me.phoenixra.atumvr.example.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceController;
import me.phoenixra.atumvr.core.input.device.XRDeviceController;
import me.phoenixra.atumvr.core.input.profile.XRProfileManager;
import me.phoenixra.atumvr.core.input.profile.tracker.ViveTrackerManager;
import me.phoenixra.atumvr.example.ExampleHandEnum;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.device.XRDevice;
import me.phoenixra.atumvr.core.input.device.XRDeviceHMD;
import me.phoenixra.atumvr.core.input.XRInputHandler;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

public class ExampleVRInputHandler extends XRInputHandler {
    @Getter
    private XRProfileManager profileSetHolder;
    @Getter
    private ViveTrackerManager trackerManager;

    private final ExampleHandEnum pulsatingHand = ExampleHandEnum.MAIN;
    @Getter
    private final ExampleHandEnum scaleHand = ExampleHandEnum.OFFHAND;

    public ExampleVRInputHandler(XRProvider vrProvider) {
        super(vrProvider);
    }

    @Override
    protected @NotNull List<? extends XRActionSet> generateActionSets(@NotNull MemoryStack stack) {
        profileSetHolder = new XRProfileManager(getVrProvider());
        trackerManager = new ViveTrackerManager(getVrProvider());

        List<XRActionSet> actionSets = new ArrayList<>(profileSetHolder.getAllActionSets());
        actionSets.addAll(trackerManager.getActionSets());
        return actionSets;
    }

    @Override
    protected @NotNull List<? extends XRDevice> generateDevices(@NotNull MemoryStack stack) {
        List<XRDevice> devices = new ArrayList<>();
        devices.add(new XRDeviceHMD(getVrProvider()));
        devices.add(new XRDeviceController(
                getVrProvider(),
                ControllerType.LEFT,
                profileSetHolder.getCommonSet().getHandPoseAim(),
                profileSetHolder.getCommonSet().getHandPoseGrip(),
                profileSetHolder.getCommonSet().getHapticPulse()
        ));
        devices.add(new XRDeviceController(
                getVrProvider(),
                ControllerType.RIGHT,
                profileSetHolder.getCommonSet().getHandPoseAim(),
                profileSetHolder.getCommonSet().getHandPoseGrip(),
                profileSetHolder.getCommonSet().getHapticPulse()
        ));
        trackerManager.setEmulated(true);
        devices.addAll(trackerManager.createDevices());
        return devices;
    }

    @Override
    public void update() {
        super.update();
        ControllerType type = pulsatingHand.asType();

        var profileSet = profileSetHolder.getActiveProfile();
        if(profileSet == null){
            return;
        }

        if(profileSet.getTriggerButton(type).isPressed()){
            getDevice(AtumVRDeviceController.getId(type), XRDeviceController.class)
                    .triggerHapticPulse(
                            160f,
                            1.0F,
                            0.1f
                    );
        }
    }
}
