package me.phoenixra.atumvr.core.input.device;

import me.phoenixra.atumvr.core.enums.EyeType;
import me.phoenixra.atumvr.core.misc.pose.VRPose;
import me.phoenixra.atumvr.core.misc.pose.VRPoseMutable;
import me.phoenixra.atumvr.core.utils.VRUtils;
import me.phoenixra.atumvr.core.VRProvider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.XrPosef;
import org.lwjgl.openxr.XrSpace;
import org.lwjgl.openxr.XrSpaceLocation;
import org.lwjgl.openxr.XrView;
import org.lwjgl.system.MemoryStack;

/**
 * VRDevice for Head-Mounted Display (HMD)
 */
public class VRDeviceHMD extends VRDevice {
    /**
     * VR device identifier for HMD
     */
    public static final String ID = "hmd";

    private final VRPoseMutable eyeLeftPose = new VRPoseMutable();

    private final VRPoseMutable eyeRightPose = new VRPoseMutable();

    private final XrSpace space;

    public VRDeviceHMD(VRProvider vrProvider) {
        super(vrProvider, ID);
        space = vrProvider.getSession().getXrViewSpace();
    }



    @Override
    public void update() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrSpaceLocation loc = VRUtils.xrLocationFromSpace(
                    vrProvider,
                    space,
                    stack
            );

            active = loc != null;

            if (active) {
                pose.update(
                        VRUtils.normalizeXrPose(loc.pose()),
                        VRUtils.normalizeXrQuaternion(loc.pose().orientation()),
                        VRUtils.normalizeXrVector(loc.pose().position$())
                );
            }
        }

        XrPosef eyePose = getXrView(EyeType.LEFT).pose();
        eyeLeftPose.update(
                VRUtils.normalizeXrPose(eyePose),
                VRUtils.normalizeXrQuaternion(eyePose.orientation()),
                VRUtils.normalizeXrVector(eyePose.position$())
        );

        eyePose = getXrView(EyeType.RIGHT).pose();
        eyeRightPose.update(
                VRUtils.normalizeXrPose(eyePose),
                VRUtils.normalizeXrQuaternion(eyePose.orientation()),
                VRUtils.normalizeXrVector(eyePose.position$())
        );
    }

    /**
     * Get pose for specified eye
     *
     * @param eyeType the type of eye (RIGHT, LEFT)
     * @return VRPose
     */
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
