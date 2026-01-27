package me.phoenixra.atumvr.core.input.device;

import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.input.device.VRDeviceHMD;
import me.phoenixra.atumvr.api.misc.pose.VRPose;
import me.phoenixra.atumvr.api.misc.pose.VRPoseMutable;
import me.phoenixra.atumvr.core.utils.XRUtils;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.XrPosef;
import org.lwjgl.openxr.XrSpace;
import org.lwjgl.openxr.XrSpaceLocation;
import org.lwjgl.openxr.XrView;
import org.lwjgl.system.MemoryStack;


public class XRDeviceHMD extends XRDevice implements VRDeviceHMD {
    /**
     * VR device identifier for HMD
     */
    public static final String ID = "hmd";

    private final VRPoseMutable eyeLeftPose = new VRPoseMutable();

    private final VRPoseMutable eyeRightPose = new VRPoseMutable();

    private final XrSpace space;

    public XRDeviceHMD(XRProvider vrProvider) {
        super(vrProvider, ID);
        space = vrProvider.getSession().getXrViewSpace();
    }



    @Override
    public void update() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrSpaceLocation loc = XRUtils.xrLocationFromSpace(
                    vrProvider,
                    space,
                    stack
            );

            active = loc != null;

            if (active) {
                pose.update(
                        XRUtils.normalizeXrPose(loc.pose()),
                        XRUtils.normalizeXrQuaternion(loc.pose().orientation()),
                        XRUtils.normalizeXrVector(loc.pose().position$())
                );
            }
        }

        XrPosef eyePose = getXrView(EyeType.LEFT).pose();
        eyeLeftPose.update(
                XRUtils.normalizeXrPose(eyePose),
                XRUtils.normalizeXrQuaternion(eyePose.orientation()),
                XRUtils.normalizeXrVector(eyePose.position$())
        );

        eyePose = getXrView(EyeType.RIGHT).pose();
        eyeRightPose.update(
                XRUtils.normalizeXrPose(eyePose),
                XRUtils.normalizeXrQuaternion(eyePose.orientation()),
                XRUtils.normalizeXrVector(eyePose.position$())
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

    /**
     * Get XR view for specified eye
     * @param eyeType the type of eye (RIGHT, LEFT)
     * @return XrView
     */
    public XrView getXrView(EyeType eyeType){
        return vrProvider.getSession().getSwapChain()
                .getXrViewBuffer().get(eyeType.getIndex());
    }
}
