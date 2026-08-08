package me.phoenixra.atumvr.example.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.input.device.AtumVRDeviceController;
import me.phoenixra.atumvr.core.input.body.XRCommonBodyView;
import me.phoenixra.atumvr.core.input.device.XRDeviceController;
import me.phoenixra.atumvr.core.input.profile.XRProfileManager;
import me.phoenixra.atumvr.core.input.profile.tracker.hand.EXTHandTrackingProvider;
import me.phoenixra.atumvr.core.input.profile.tracker.hand.XRHandsProvider;
import me.phoenixra.atumvr.core.input.profile.tracker.ViveTrackerProvider;
import me.phoenixra.atumvr.core.input.haptics.XRBodyHapticsProvider;
import me.phoenixra.atumvr.core.input.haptics.bhaptics.BHapticsProvider;
import me.phoenixra.atumvr.core.input.treadmill.XRTreadmillEmulator;
import me.phoenixra.atumvr.core.input.treadmill.XRTreadmillProvider;
import me.phoenixra.atumvr.core.input.treadmill.infinadeck.InfinadeckTreadmillProvider;
import me.phoenixra.atumvr.core.input.treadmill.kat.KATLegacyTreadmillProvider;
import me.phoenixra.atumvr.core.input.treadmill.kat.KATTreadmillProvider;
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
    private ViveTrackerProvider trackerProvider;
    @Getter
    private EXTHandTrackingProvider handTrackingProvider;
    @Getter
    private XRTreadmillEmulator treadmillEmulator;

    private static final float WALK_SPEED = 1.2f;
    private static final float TURN_RATE = 0.5f;

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
    protected @NotNull List<? extends AtumVRBodyView> generateBodyViews(@NotNull MemoryStack stack) {
        trackerProvider = new ViveTrackerProvider(getVrProvider());
        trackerProvider.setEmulated(true);

        return List.of(
                new XRCommonBodyView(getVrProvider()),
                trackerProvider
        );
    }

    @Override
    protected @NotNull List<? extends XRHandsProvider> generateHandsProviders(@NotNull MemoryStack stack) {
        handTrackingProvider = new EXTHandTrackingProvider(getVrProvider());
        return List.of(handTrackingProvider);
    }

    @Override
    protected @NotNull List<? extends XRTreadmillProvider> generateTreadmillProviders(@NotNull MemoryStack stack) {
        treadmillEmulator = new XRTreadmillEmulator();
        // real hardware first, emulator as the always-supported fallback
        return List.of(
                new KATTreadmillProvider(getVrProvider()),
                new KATLegacyTreadmillProvider(getVrProvider()),
                new InfinadeckTreadmillProvider(getVrProvider()),
                treadmillEmulator
        );
    }

    @Override
    protected @NotNull List<? extends XRBodyHapticsProvider> generateBodyHapticsProviders(@NotNull MemoryStack stack) {
        return List.of(new BHapticsProvider(getVrProvider()));
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
        return devices;
    }

    @Override
    public void update() {
        float time = (float) (getVrProvider().getXrDisplayTime() * 1.0e-9);
        treadmillEmulator.setSensorYaw(time * TURN_RATE);
        treadmillEmulator.setGait(0f, 1f, WALK_SPEED);

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
