package me.phoenixra.atumvr.api.input.profile.tracker;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public enum ViveTrackerRole {

    // Body Main
    WAIST("waist"),
    CHEST("chest"),

    // Body Legs
    LEFT_FOOT("left_foot"),
    RIGHT_FOOT("right_foot"),

    LEFT_ANKLE("left_ankle"),
    RIGHT_ANKLE("right_ankle"),

    LEFT_KNEE("left_knee"),
    RIGHT_KNEE("right_knee"),

    // Body Arms
    LEFT_WRIST("left_wrist"),
    RIGHT_WRIST("right_wrist"),

    LEFT_ELBOW("left_elbow"),
    RIGHT_ELBOW("right_elbow"),

    LEFT_SHOULDER("left_shoulder"),
    RIGHT_SHOULDER("right_shoulder"),

    // Non-body trackers
    HANDHELD_OBJECT("handheld_object"),
    CAMERA("camera"),
    KEYBOARD("keyboard");


    public static final String USER_PATH_PREFIX = "/user/vive_tracker_htcx/role/";
    public static final String DEVICE_ID_PREFIX = "tracker_";

    private final String key;
    private final String userPath;
    private final String deviceId;

    ViveTrackerRole(String key) {
        this.key = key;
        this.userPath = USER_PATH_PREFIX + key;
        this.deviceId = DEVICE_ID_PREFIX + key;
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
