package me.phoenixra.atumvr.api.input.profile.tracker;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//@TODO redundant, remove
@Getter
public enum ViveTrackerRole {

    // Body Main
    WAIST("waist", AtumVRBodyJoint.WAIST),
    CHEST("chest", AtumVRBodyJoint.CHEST),

    // Body Legs
    LEFT_FOOT("left_foot", AtumVRBodyJoint.LEFT_FOOT),
    RIGHT_FOOT("right_foot", AtumVRBodyJoint.RIGHT_FOOT),

    LEFT_ANKLE("left_ankle", AtumVRBodyJoint.LEFT_ANKLE),
    RIGHT_ANKLE("right_ankle", AtumVRBodyJoint.RIGHT_ANKLE),

    LEFT_KNEE("left_knee", AtumVRBodyJoint.LEFT_KNEE),
    RIGHT_KNEE("right_knee", AtumVRBodyJoint.RIGHT_KNEE),

    // Body Arms
    LEFT_WRIST("left_wrist", AtumVRBodyJoint.LEFT_WRIST),
    RIGHT_WRIST("right_wrist", AtumVRBodyJoint.RIGHT_WRIST),

    LEFT_ELBOW("left_elbow", AtumVRBodyJoint.LEFT_ELBOW),
    RIGHT_ELBOW("right_elbow", AtumVRBodyJoint.RIGHT_ELBOW),

    LEFT_SHOULDER("left_shoulder", AtumVRBodyJoint.LEFT_SHOULDER),
    RIGHT_SHOULDER("right_shoulder", AtumVRBodyJoint.RIGHT_SHOULDER),

    // Non-body trackers
    HANDHELD_OBJECT("handheld_object", null),
    CAMERA("camera", null),
    KEYBOARD("keyboard", null);


    public static final String USER_PATH_PREFIX = "/user/vive_tracker_htcx/role/";
    public static final String DEVICE_ID_PREFIX = "tracker_";

    private final String key;
    private final String userPath;
    private final String deviceId;

    @Nullable
    private final AtumVRBodyJoint bodyJoint;

    ViveTrackerRole(String key, @Nullable AtumVRBodyJoint bodyJoint) {
        this.key = key;
        this.userPath = USER_PATH_PREFIX + key;
        this.deviceId = DEVICE_ID_PREFIX + key;
        this.bodyJoint = bodyJoint;
    }



    /**
     * The default full-body tracking set: waist, chest, feet, knees and elbows.
     */
    public static final List<ViveTrackerRole> DEFAULT_FULL_BODY = List.of(
            WAIST, CHEST,
            LEFT_FOOT, RIGHT_FOOT,
            LEFT_ANKLE, RIGHT_ANKLE,
            LEFT_KNEE, RIGHT_KNEE,
            LEFT_WRIST, RIGHT_WRIST,
            LEFT_ELBOW, RIGHT_ELBOW,
            LEFT_SHOULDER, RIGHT_SHOULDER
    );


    /**
     * Get the role for an OpenXR user path, or null if none matches.
     *
     * @param userPath the user path
     * @return the role or null
     */
    @Nullable
    public static ViveTrackerRole fromUserPath(@NotNull String userPath) {
        for (ViveTrackerRole role : values()) {
            if (role.userPath.equals(userPath)) {
                return role;
            }
        }
        return null;
    }

    /**
     * Get the role for a device id, or null if none matches.
     *
     * @param deviceId the device id
     * @return the role or null
     */
    @Nullable
    public static ViveTrackerRole fromDeviceId(@NotNull String deviceId) {
        for (ViveTrackerRole role : values()) {
            if (role.deviceId.equals(deviceId)) {
                return role;
            }
        }
        return null;
    }
}
