package me.phoenixra.atumvr.core.input.profile.tracker;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPoseMutable;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.utils.XRUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.openxr.FBBodyTracking;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrBodyJointLocationFB;
import org.lwjgl.openxr.XrBodyJointLocationsFB;
import org.lwjgl.openxr.XrBodyJointsLocateInfoFB;
import org.lwjgl.openxr.XrBodyTrackerCreateInfoFB;
import org.lwjgl.openxr.XrBodyTrackerFB;
import org.lwjgl.openxr.XrPosef;
import org.lwjgl.openxr.XrSystemBodyTrackingPropertiesFB;
import org.lwjgl.openxr.XrSystemProperties;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;


public class FBBodyTrackingProvider implements XRTrackerProvider {

    public static final String EXTENSION_NAME = FBBodyTracking.XR_FB_BODY_TRACKING_EXTENSION_NAME;
    public static final String EXTENSION_FULL_BODY_META = "XR_META_body_tracking_full_body";
    public static final String EXTENSION_FIDELITY_META = "XR_META_body_tracking_fidelity";

    // XR_META_body_tracking_full_body values, absent from LWJGL 3.3.6 bindings
    private static final int XR_BODY_JOINT_SET_FULL_BODY_META = 1000274000;
    private static final int FULL_BODY_JOINT_COUNT_META = 84;
    private static final int JOINT_LEFT_UPPER_LEG_META = 70;
    private static final int JOINT_LEFT_LOWER_LEG_META = 71;
    private static final int JOINT_LEFT_FOOT_ANKLE_META = 73;
    private static final int JOINT_LEFT_FOOT_BALL_META = 76;
    private static final int JOINT_RIGHT_UPPER_LEG_META = 77;
    private static final int JOINT_RIGHT_LOWER_LEG_META = 78;
    private static final int JOINT_RIGHT_FOOT_ANKLE_META = 80;
    private static final int JOINT_RIGHT_FOOT_BALL_META = 83;

    // XR_META_body_tracking_fidelity values, absent from LWJGL 3.3.6 bindings
    private static final int XR_BODY_TRACKING_FIDELITY_HIGH_META = 2;

    private static final long POSE_VALID_FLAGS =
            XR10.XR_SPACE_LOCATION_ORIENTATION_VALID_BIT
                    | XR10.XR_SPACE_LOCATION_POSITION_VALID_BIT;

    private static final AtumVRBodyJoint[] JOINT_MAP =
            new AtumVRBodyJoint[FULL_BODY_JOINT_COUNT_META];

    static {
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_HIPS_FB] = AtumVRBodyJoint.WAIST;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_CHEST_FB] = AtumVRBodyJoint.CHEST;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_NECK_FB] = AtumVRBodyJoint.NECK;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_HEAD_FB] = AtumVRBodyJoint.HEAD;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_LEFT_SHOULDER_FB] = AtumVRBodyJoint.LEFT_SHOULDER;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_RIGHT_SHOULDER_FB] = AtumVRBodyJoint.RIGHT_SHOULDER;
        // ARM_LOWER joints sit at the elbows, HAND_PALM at the palms
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_LEFT_ARM_LOWER_FB] = AtumVRBodyJoint.LEFT_ELBOW;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_RIGHT_ARM_LOWER_FB] = AtumVRBodyJoint.RIGHT_ELBOW;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_LEFT_HAND_WRIST_FB] = AtumVRBodyJoint.LEFT_WRIST;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_RIGHT_HAND_WRIST_FB] = AtumVRBodyJoint.RIGHT_WRIST;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_LEFT_HAND_PALM_FB] = AtumVRBodyJoint.LEFT_HAND;
        JOINT_MAP[FBBodyTracking.XR_BODY_JOINT_RIGHT_HAND_PALM_FB] = AtumVRBodyJoint.RIGHT_HAND;
        // UPPER_LEG joints sit at the hips, LOWER_LEG at the knees
        JOINT_MAP[JOINT_LEFT_UPPER_LEG_META] = AtumVRBodyJoint.LEFT_HIP;
        JOINT_MAP[JOINT_LEFT_LOWER_LEG_META] = AtumVRBodyJoint.LEFT_KNEE;
        JOINT_MAP[JOINT_LEFT_FOOT_ANKLE_META] = AtumVRBodyJoint.LEFT_ANKLE;
        JOINT_MAP[JOINT_LEFT_FOOT_BALL_META] = AtumVRBodyJoint.LEFT_FOOT;
        JOINT_MAP[JOINT_RIGHT_UPPER_LEG_META] = AtumVRBodyJoint.RIGHT_HIP;
        JOINT_MAP[JOINT_RIGHT_LOWER_LEG_META] = AtumVRBodyJoint.RIGHT_KNEE;
        JOINT_MAP[JOINT_RIGHT_FOOT_ANKLE_META] = AtumVRBodyJoint.RIGHT_ANKLE;
        JOINT_MAP[JOINT_RIGHT_FOOT_BALL_META] = AtumVRBodyJoint.RIGHT_FOOT;
    }

    @Getter
    private final XRProvider vrProvider;

    @Getter
    private final boolean hardwareSupported;

    /**
     * If the tracker delivers leg joints
     */
    @Getter
    private boolean fullBody;

    private final EnumMap<AtumVRBodyJoint, AtumVRPoseMutable> poses =
            new EnumMap<>(AtumVRBodyJoint.class);
    private final EnumSet<AtumVRBodyJoint> tracked = EnumSet.noneOf(AtumVRBodyJoint.class);
    private final Set<AtumVRBodyJoint> trackedView = Collections.unmodifiableSet(tracked);

    private XrBodyTrackerFB tracker;
    private int jointCount;
    private XrBodyJointsLocateInfoFB locateInfo;
    private XrBodyJointLocationsFB locations;
    private XrBodyJointLocationFB.Buffer jointLocations;

    private final Matrix4f cachedMatrix = new Matrix4f();
    private final Quaternionf cachedOrientation = new Quaternionf();
    private final Vector3f cachedPosition = new Vector3f();


    public FBBodyTrackingProvider(@NotNull XRProvider vrProvider) {
        this.vrProvider = vrProvider;
        var instance = vrProvider.getSession().getInstance();
        this.hardwareSupported = instance.isExtensionEnabled(EXTENSION_NAME)
                && querySystemSupport();
        this.fullBody = hardwareSupported
                && instance.isExtensionEnabled(EXTENSION_FULL_BODY_META);

        for (AtumVRBodyJoint joint : JOINT_MAP) {
            if (joint != null) {
                poses.put(joint, new AtumVRPoseMutable());
            }
        }
    }

    private boolean querySystemSupport() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var bodyProps = XrSystemBodyTrackingPropertiesFB.calloc(stack)
                    .type$Default();
            var sysProps = XrSystemProperties.calloc(stack)
                    .type(XR10.XR_TYPE_SYSTEM_PROPERTIES)
                    .next(bodyProps.address());
            int result = XR10.xrGetSystemProperties(
                    vrProvider.getSession().getInstance().getHandle(),
                    vrProvider.getSession().getSystem().getSystemId(),
                    sysProps
            );
            return result >= 0 && bodyProps.supportsBodyTracking();
        }
    }


    @Override
    public boolean isSupported() {
        return hardwareSupported;
    }

    @Override
    public void onAttached() {
        releaseNative();

        jointCount = fullBody
                ? FULL_BODY_JOINT_COUNT_META
                : FBBodyTracking.XR_BODY_JOINT_COUNT_FB;
        int jointSet = fullBody
                ? XR_BODY_JOINT_SET_FULL_BODY_META
                : FBBodyTracking.XR_BODY_JOINT_SET_DEFAULT_FB;

        if (!createTracker(jointSet) && fullBody) {
            fullBody = false;
            jointCount = FBBodyTracking.XR_BODY_JOINT_COUNT_FB;
            createTracker(FBBodyTracking.XR_BODY_JOINT_SET_DEFAULT_FB);
        }
        if (tracker == null) {
            return;
        }

        jointLocations = XrBodyJointLocationFB.calloc(jointCount);
        locations = XrBodyJointLocationsFB.calloc()
                .type$Default()
                .jointLocations(jointLocations);
        locateInfo = XrBodyJointsLocateInfoFB.calloc()
                .type$Default()
                .baseSpace(vrProvider.getSession().getXrAppSpace());

        requestHighFidelity();

        vrProvider.getLogger().logInfo(
                "FB body tracking attached, full body: " + fullBody
        );
    }

    private boolean createTracker(int jointSet) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var createInfo = XrBodyTrackerCreateInfoFB.calloc(stack)
                    .type$Default()
                    .bodyJointSet(jointSet);
            var pointer = stack.callocPointer(1);
            int result = FBBodyTracking.xrCreateBodyTrackerFB(
                    vrProvider.getSession().getHandle(),
                    createInfo,
                    pointer
            );
            if (result < 0) {
                vrProvider.getLogger().logError(
                        "xrCreateBodyTrackerFB failed for joint set " + jointSet
                                + ": " + vrProvider.getXRActionResult(result)
                );
                return false;
            }
            tracker = new XrBodyTrackerFB(pointer.get(0), vrProvider.getSession().getHandle());
            return true;
        }
    }

    /**
     * Ask the runtime for IOBT instead of the three-point estimate.
     * No LWJGL 3.3.6 binding for this call, invoked through the function pointer
     */
    private void requestHighFidelity() {
        if (!vrProvider.getSession().getInstance().isExtensionEnabled(EXTENSION_FIDELITY_META)) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var fnPointer = stack.callocPointer(1);
            int result = XR10.xrGetInstanceProcAddr(
                    vrProvider.getSession().getInstance().getHandle(),
                    stack.UTF8("xrRequestBodyTrackingFidelityMETA"),
                    fnPointer
            );
            if (result < 0 || fnPointer.get(0) == 0L) {
                return;
            }
            result = JNI.callPI(
                    tracker.address(),
                    XR_BODY_TRACKING_FIDELITY_HIGH_META,
                    fnPointer.get(0)
            );
            vrProvider.checkXRError(false, result, "xrRequestBodyTrackingFidelityMETA");
        }
    }


    @Override
    public void update() {
        if (tracker == null) {
            return;
        }
        tracked.clear();

        locateInfo.time(vrProvider.getXrDisplayTime());
        int result = FBBodyTracking.xrLocateBodyJointsFB(tracker, locateInfo, locations);
        if (result < 0) {
            vrProvider.checkXRError(false, result, "xrLocateBodyJointsFB");
            return;
        }
        if (!locations.isActive()) {
            return;
        }

        for (int i = 0; i < jointCount; i++) {
            AtumVRBodyJoint joint = JOINT_MAP[i];
            if (joint == null) {
                continue;
            }
            XrBodyJointLocationFB location = jointLocations.get(i);
            if ((location.locationFlags() & POSE_VALID_FLAGS) != POSE_VALID_FLAGS) {
                continue;
            }
            XrPosef xrPose = location.pose();
            poses.get(joint).update(
                    XRUtils.normalizeXrPose(xrPose, cachedMatrix),
                    XRUtils.normalizeXrQuaternion(xrPose.orientation(), cachedOrientation),
                    XRUtils.normalizeXrVector(xrPose.position$(), cachedPosition)
            );
            tracked.add(joint);
        }
    }


    @Override
    public @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint) {
        return tracked.contains(joint) ? poses.get(joint) : null;
    }

    @Override
    public void collectJoints(@NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        for (AtumVRBodyJoint joint : tracked) {
            sink.accept(joint, poses.get(joint));
        }
    }

    @Override
    public @NotNull Set<AtumVRBodyJoint> getTrackedJoints() {
        return trackedView;
    }


    @Override
    public void destroy() {
        releaseNative();
        tracked.clear();
    }

    private void releaseNative() {
        if (tracker != null) {
            vrProvider.checkXRError(
                    false,
                    FBBodyTracking.xrDestroyBodyTrackerFB(tracker),
                    "xrDestroyBodyTrackerFB"
            );
            tracker = null;
        }
        if (locateInfo != null) {
            locateInfo.free();
            locateInfo = null;
        }
        if (locations != null) {
            locations.free();
            locations = null;
        }
        if (jointLocations != null) {
            jointLocations.free();
            jointLocations = null;
        }
    }
}
