package me.phoenixra.atumvr.api.input.action.data;

import me.phoenixra.atumvr.api.misc.pose.AtumVRPoseRecord;
import org.jetbrains.annotations.NotNull;


/**
 * VR action data for pose type
 */
public interface VRActionDataPose extends VRActionData{

    /**
     * Get Pose
     *
     * @return the pose record
     */
    @NotNull AtumVRPoseRecord getPose();
}
