package me.phoenixra.atumvr.core.input.profile.tracker.hand;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandJoint;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandView;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.body.hand.XRHand;
import me.phoenixra.atumvr.core.utils.XRUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.openxr.EXTHandTracking;
import org.lwjgl.openxr.EXTHandTrackingDataSource;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrHandJointLocationEXT;
import org.lwjgl.openxr.XrHandJointLocationsEXT;
import org.lwjgl.openxr.XrHandJointsLocateInfoEXT;
import org.lwjgl.openxr.XrHandTrackerCreateInfoEXT;
import org.lwjgl.openxr.XrHandTrackerEXT;
import org.lwjgl.openxr.XrHandTrackingDataSourceInfoEXT;
import org.lwjgl.openxr.XrHandTrackingDataSourceStateEXT;
import org.lwjgl.openxr.XrPosef;
import org.lwjgl.openxr.XrSystemHandTrackingPropertiesEXT;
import org.lwjgl.openxr.XrSystemProperties;
import org.lwjgl.system.MemoryStack;

import java.util.function.BiConsumer;

/**
 * Hand skeleton provider based on XR_EXT_hand_tracking.
 * Also delivers wrist/palm body joints as an {@link AtumVRBodyView}
 */
public class EXTHandTrackingProvider implements XRHandsProvider, AtumVRBodyView {

    public static final String EXTENSION_NAME =
            EXTHandTracking.XR_EXT_HAND_TRACKING_EXTENSION_NAME;
    public static final String EXTENSION_DATA_SOURCE =
            EXTHandTrackingDataSource.XR_EXT_HAND_TRACKING_DATA_SOURCE_EXTENSION_NAME;

    private static final long POSE_VALID_FLAGS =
            XR10.XR_SPACE_LOCATION_ORIENTATION_VALID_BIT
                    | XR10.XR_SPACE_LOCATION_POSITION_VALID_BIT;

    @Getter
    private final XRProvider vrProvider;

    @Getter
    private final boolean hardwareSupported;


    @Getter
    private final boolean dataSourceSupported;

    private final HandSlot left = new HandSlot(ControllerType.LEFT);
    private final HandSlot right = new HandSlot(ControllerType.RIGHT);

    private final Matrix4f cachedMatrix = new Matrix4f();
    private final Quaternionf cachedOrientation = new Quaternionf();
    private final Vector3f cachedPosition = new Vector3f();


    public EXTHandTrackingProvider(@NotNull XRProvider vrProvider) {
        this.vrProvider = vrProvider;
        var instance = vrProvider.getSession().getInstance();
        this.hardwareSupported = instance.isExtensionEnabled(EXTENSION_NAME)
                && querySystemSupport();
        this.dataSourceSupported = hardwareSupported
                && instance.isExtensionEnabled(EXTENSION_DATA_SOURCE);
    }

    private boolean querySystemSupport() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var handProps = XrSystemHandTrackingPropertiesEXT.calloc(stack)
                    .type$Default();
            var sysProps = XrSystemProperties.calloc(stack)
                    .type(XR10.XR_TYPE_SYSTEM_PROPERTIES)
                    .next(handProps.address());
            int result = XR10.xrGetSystemProperties(
                    vrProvider.getSession().getInstance().getHandle(),
                    vrProvider.getSession().getSystem().getSystemId(),
                    sysProps
            );
            return result >= 0 && handProps.supportsHandTracking();
        }
    }


    @Override
    public boolean isSupported() {
        return hardwareSupported;
    }

    @Override
    public void onAttached() {
        releaseNative();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            createHand(left, EXTHandTracking.XR_HAND_LEFT_EXT, stack);
            createHand(right, EXTHandTracking.XR_HAND_RIGHT_EXT, stack);
        }
        if (left.tracker == null && right.tracker == null) {
            return;
        }
        vrProvider.getLogger().logInfo(
                "EXT hand tracking attached, data source reporting: " + dataSourceSupported
        );
    }

    private void createHand(HandSlot slot, int xrHandSide, MemoryStack stack) {
        var createInfo = XrHandTrackerCreateInfoEXT.calloc(stack)
                .type$Default()
                .hand(xrHandSide)
                .handJointSet(EXTHandTracking.XR_HAND_JOINT_SET_DEFAULT_EXT);
        if (dataSourceSupported) {
            // accept both hand and controller source
            createInfo.next(
                    XrHandTrackingDataSourceInfoEXT.calloc(stack)
                            .type$Default()
                            .requestedDataSources(stack.ints(
                                    EXTHandTrackingDataSource.XR_HAND_TRACKING_DATA_SOURCE_UNOBSTRUCTED_EXT,
                                    EXTHandTrackingDataSource.XR_HAND_TRACKING_DATA_SOURCE_CONTROLLER_EXT
                            ))
                            .address()
            );
        }

        var pointer = stack.callocPointer(1);
        int result = EXTHandTracking.xrCreateHandTrackerEXT(
                vrProvider.getSession().getHandle(),
                createInfo,
                pointer
        );
        if (result < 0) {
            vrProvider.getLogger().logError(
                    "xrCreateHandTrackerEXT failed for " + slot.hand.getSide()
                            + ": " + vrProvider.getXRActionResult(result)
            );
            return;
        }
        slot.tracker = new XrHandTrackerEXT(pointer.get(0), vrProvider.getSession().getHandle());

        slot.jointLocations = XrHandJointLocationEXT.calloc(EXTHandTracking.XR_HAND_JOINT_COUNT_EXT);
        slot.locations = XrHandJointLocationsEXT.calloc()
                .type$Default()
                .jointLocations(slot.jointLocations);
        if (dataSourceSupported) {
            slot.dataSourceState = XrHandTrackingDataSourceStateEXT.calloc()
                    .type$Default();
            slot.locations.next(slot.dataSourceState.address());
        }
        slot.locateInfo = XrHandJointsLocateInfoEXT.calloc()
                .type$Default()
                .baseSpace(vrProvider.getSession().getXrAppSpace());
    }


    @Override
    public void update() {
        updateHand(left);
        updateHand(right);
    }

    private void updateHand(HandSlot slot) {
        XRHand hand = slot.hand;
        hand.clear();
        if (slot.tracker == null) {
            return;
        }

        slot.locateInfo.time(vrProvider.getXrDisplayTime());
        int result = EXTHandTracking.xrLocateHandJointsEXT(
                slot.tracker, slot.locateInfo, slot.locations
        );
        if (result < 0) {
            vrProvider.checkXRError(false, result, "xrLocateHandJointsEXT");
            return;
        }
        if (!slot.locations.isActive()) {
            return;
        }

        hand.setActive(true);
        if (slot.dataSourceState != null && slot.dataSourceState.isActive()) {
            hand.setDataSource(convertDataSource(slot.dataSourceState.dataSource()));
        }

        for (int i = 0; i < EXTHandTracking.XR_HAND_JOINT_COUNT_EXT; i++) {
            XrHandJointLocationEXT location = slot.jointLocations.get(i);
            if ((location.locationFlags() & POSE_VALID_FLAGS) != POSE_VALID_FLAGS) {
                continue;
            }
            XrPosef xrPose = location.pose();
            hand.updateJoint(
                    AtumVRHandJoint.fromIndex(i),
                    XRUtils.normalizeXrPose(xrPose, cachedMatrix),
                    XRUtils.normalizeXrQuaternion(xrPose.orientation(), cachedOrientation),
                    XRUtils.normalizeXrVector(xrPose.position$(), cachedPosition),
                    location.radius()
            );
        }
    }

    private static AtumVRHandView.DataSource convertDataSource(int xrDataSource) {
        return switch (xrDataSource) {
            case EXTHandTrackingDataSource.XR_HAND_TRACKING_DATA_SOURCE_UNOBSTRUCTED_EXT
                    -> AtumVRHandView.DataSource.HAND;
            case EXTHandTrackingDataSource.XR_HAND_TRACKING_DATA_SOURCE_CONTROLLER_EXT
                    -> AtumVRHandView.DataSource.CONTROLLER;
            default -> AtumVRHandView.DataSource.UNKNOWN;
        };
    }


    @Override
    public @NotNull AtumVRHandView getHand(@NotNull ControllerType side) {
        return side == ControllerType.LEFT ? left.hand : right.hand;
    }


    @Override
    public @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint) {
        return switch (joint) {
            case LEFT_WRIST -> left.hand.getJointPose(AtumVRHandJoint.WRIST);
            case LEFT_HAND -> left.hand.getJointPose(AtumVRHandJoint.PALM);
            case RIGHT_WRIST -> right.hand.getJointPose(AtumVRHandJoint.WRIST);
            case RIGHT_HAND -> right.hand.getJointPose(AtumVRHandJoint.PALM);
            default -> null;
        };
    }

    @Override
    public void collectJoints(@NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        collectBodyJoint(AtumVRBodyJoint.LEFT_WRIST, sink);
        collectBodyJoint(AtumVRBodyJoint.LEFT_HAND, sink);
        collectBodyJoint(AtumVRBodyJoint.RIGHT_WRIST, sink);
        collectBodyJoint(AtumVRBodyJoint.RIGHT_HAND, sink);
    }

    private void collectBodyJoint(@NotNull AtumVRBodyJoint joint,
                                  @NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        AtumVRPose pose = getJointPose(joint);
        if (pose != null) {
            sink.accept(joint, pose);
        }
    }


    @Override
    public void destroy() {
        releaseNative();
        left.hand.clear();
        right.hand.clear();
    }

    private void releaseNative() {
        releaseNative(left);
        releaseNative(right);
    }

    private void releaseNative(HandSlot slot) {
        if (slot.tracker != null) {
            vrProvider.checkXRError(
                    false,
                    EXTHandTracking.xrDestroyHandTrackerEXT(slot.tracker),
                    "xrDestroyHandTrackerEXT"
            );
            slot.tracker = null;
        }
        if (slot.locateInfo != null) {
            slot.locateInfo.free();
            slot.locateInfo = null;
        }
        if (slot.locations != null) {
            slot.locations.free();
            slot.locations = null;
        }
        if (slot.jointLocations != null) {
            slot.jointLocations.free();
            slot.jointLocations = null;
        }
        if (slot.dataSourceState != null) {
            slot.dataSourceState.free();
            slot.dataSourceState = null;
        }
    }


    private static final class HandSlot {
        final XRHand hand;

        XrHandTrackerEXT tracker;
        XrHandJointsLocateInfoEXT locateInfo;
        XrHandJointLocationsEXT locations;
        XrHandJointLocationEXT.Buffer jointLocations;
        XrHandTrackingDataSourceStateEXT dataSourceState;

        HandSlot(ControllerType side) {
            this.hand = new XRHand(side);
        }
    }
}
