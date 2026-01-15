package me.phoenixra.atumvr.core.input.device;

import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.input.device.VRDeviceHMD;
import me.phoenixra.atumvr.api.misc.pose.VRPose;
import me.phoenixra.atumvr.api.misc.pose.VRPoseMutable;
import me.phoenixra.atumvr.core.XRHelper;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.XrPosef;
import org.lwjgl.openxr.XrSpace;
import org.lwjgl.openxr.XrSpaceLocation;
import org.lwjgl.openxr.XrView;
import org.lwjgl.system.MemoryStack;

public class XRDeviceHMD extends XRDevice implements VRDeviceHMD {

    private final VRPoseMutable eyeLeftPose = new VRPoseMutable();

    private final VRPoseMutable eyeRightPose = new VRPoseMutable();

    private final XrSpace space;

    public XRDeviceHMD(XRProvider provider) {
        super(provider, ID);
        space = provider.getState().getVrSession().getXrViewSpace();
    }



    @Override
    public void update() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrSpaceLocation loc = XRHelper.xrLocationFromSpace(
                    provider,
                    space,
                    stack
            );

            active = loc != null;

            if (active) {
                pose.update(
                        XRHelper.normalizeXrPose(loc.pose()),
                        XRHelper.normalizeXrQuaternion(loc.pose().orientation()),
                        XRHelper.normalizeXrVector(loc.pose().position$())
                );
            }
        }

        XrPosef eyePose = getXrView(EyeType.LEFT).pose();
        eyeLeftPose.update(
                XRHelper.normalizeXrPose(eyePose),
                XRHelper.normalizeXrQuaternion(eyePose.orientation()),
                XRHelper.normalizeXrVector(eyePose.position$())
        );

        eyePose = getXrView(EyeType.RIGHT).pose();
        eyeRightPose.update(
                XRHelper.normalizeXrPose(eyePose),
                XRHelper.normalizeXrQuaternion(eyePose.orientation()),
                XRHelper.normalizeXrVector(eyePose.position$())
        );
    }

    @Override
    public @NotNull VRPose getEyePose(@NotNull EyeType eyeType) {
        if(eyeType == EyeType.LEFT){
            return eyeLeftPose;
        }else{
            return eyeRightPose;
        }
    }

    public XrView getXrView(EyeType eyeType){
        return provider.getState().getVrSwapChain()
                .getXrViewBuffer().get(eyeType.getIndex());
    }
}
