package me.phoenixra.atumvr.core.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.DeviceRegisterInfo;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.input.devices.VRDevice;
import me.phoenixra.atumvr.core.OpenXRHelper;
import me.phoenixra.atumvr.core.OpenXRProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.MemoryStack.stackPointers;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRInputHandler implements VRInputHandler<OpenXRDevice> {
    @Getter
    private final OpenXRProvider vrProvider;

    private final Map<String, OpenXRDevice> devices = new LinkedHashMap<>();


    @Getter
    private OpenXRDeviceHMD hmd;
    @Getter
    private OpenXRDeviceController leftController;
    @Getter
    private OpenXRDeviceController rightController;

    private XrActionSet actionSet;
    public OpenXRInputHandler(OpenXRProvider provider){
        this.vrProvider = provider;
    }
    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrInstance xrInstance = vrProvider.getVrState().getXrInstance().getHandle();
            XrSession xrSession = vrProvider.getVrState().getXrSession().getHandle();

            // 1) Create action set
            XrActionSetCreateInfo setInfo = XrActionSetCreateInfo
                    .calloc(stack)
                    .type(XR_TYPE_ACTION_SET_CREATE_INFO)
                    .actionSetName(stack.UTF8("gameplay"))
                    .localizedActionSetName(stack.UTF8("Gameplay"))
                    .priority(0);
            PointerBuffer pSet = stack.mallocPointer(1);
            vrProvider.checkXRError(
                    xrCreateActionSet(xrInstance, setInfo, pSet),
                    "xrCreateActionSet"
            );
            actionSet = new XrActionSet(pSet.get(0), xrInstance);

            // 2) Prepare subaction paths
            long leftPath  = OpenXRHelper.toPath(xrInstance,"/user/hand/left", stack);
            long rightPath = OpenXRHelper.toPath(xrInstance,"/user/hand/right", stack);


            // 3) Create grip-pose action
            XrActionCreateInfo gripInfo = XrActionCreateInfo
                    .calloc(stack)
                    .type(XR_TYPE_ACTION_CREATE_INFO)
                    .actionType(XR_ACTION_TYPE_POSE_INPUT)
                    .actionName(stack.UTF8("grip_pose"))
                    .localizedActionName(stack.UTF8("Grip Pose"))
                    .countSubactionPaths(2)
                    .subactionPaths(stack.longs(leftPath, rightPath));
            PointerBuffer pGrip = stack.mallocPointer(1);
            vrProvider.checkXRError(
                    xrCreateAction(actionSet, gripInfo, pGrip),
                    "xrCreateAction", "grip_pose"
            );
            XrAction gripPoseAction = new XrAction(pGrip.get(0), actionSet);

            // 4) Create aim-pose action
            XrActionCreateInfo aimInfo = XrActionCreateInfo
                    .calloc(stack)
                    .type(XR_TYPE_ACTION_CREATE_INFO)
                    .actionType(XR_ACTION_TYPE_POSE_INPUT)
                    .actionName(stack.UTF8("aim_pose"))
                    .localizedActionName(stack.UTF8("Aim Pose"))
                    .countSubactionPaths(2)
                    .subactionPaths(stack.longs(leftPath, rightPath));
            PointerBuffer pAim = stack.mallocPointer(1);
            vrProvider.checkXRError(
                    xrCreateAction(actionSet, aimInfo, pAim),
                    "xrCreateAction", "aim_pose"
            );
            XrAction aimPoseAction = new XrAction(pAim.get(0), actionSet);


            // 5) Suggest default bindings via interaction profiles
            suggestDefaultBindings(gripPoseAction, aimPoseAction, stack);

            // Attach the action set we just made to the session
            XrSessionActionSetsAttachInfo attach_info = XrSessionActionSetsAttachInfo.calloc(stack).set(
                    XR10.XR_TYPE_SESSION_ACTION_SETS_ATTACH_INFO,
                    NULL,
                    stackPointers(actionSet.address())
            );
            vrProvider.checkXRError(
                    XR10.xrAttachSessionActionSets(xrSession, attach_info),
                    "xrAttachSessionActionSets"
            );


            // Register devices: HMD, LeftHand, RightHand
            hmd = new OpenXRDeviceHMD(vrProvider);
            hmd.initSpace(stack);
            registerDevice(hmd);
            leftController = new OpenXRDeviceController(
                    vrProvider, ControllerType.LEFT,
                    aimPoseAction, gripPoseAction, leftPath
            );
            leftController.initSpace(stack);
            registerDevice(leftController);

            rightController = new OpenXRDeviceController(
                    vrProvider, ControllerType.RIGHT,
                    aimPoseAction, gripPoseAction, rightPath
            );
            rightController.initSpace(stack);
            registerDevice(rightController);

        }


    }

    @Override
    public void update() {
        XrSession session = vrProvider.getVrState().getXrSession().getHandle();
        long predictedTime = vrProvider.getXrDisplayTime();
        // Sync actions
        try (MemoryStack stack = MemoryStack.stackPush()) {
            XrActiveActionSet.Buffer aSet = XrActiveActionSet
                    .calloc(1, stack);
            aSet.get(0).set(actionSet, XR_NULL_PATH);

            XrActionsSyncInfo syncInfo = XrActionsSyncInfo
                    .calloc(stack)
                    .type(XR_TYPE_ACTIONS_SYNC_INFO)
                    .activeActionSets(aSet);
            vrProvider.checkXRError(
                    xrSyncActions(session, syncInfo),
                    "xrSyncActions"
            );
        }
        // Update devices
        for (OpenXRDevice d : devices.values()) {
            d.update(predictedTime);
        }
    }


    private void suggestDefaultBindings(XrAction gripPoseAction,
                                        XrAction aimPoseAction,
                                        MemoryStack stack) {

        XrInstance xrInstance = vrProvider.getVrState().getXrInstance().getHandle();
        String[] profiles = new String[]{
                "/interaction_profiles/valve/index_controller"
        };
        for (String profile : profiles) {
            long profilePath = OpenXRHelper.toPath(xrInstance, profile, stack);
            // Uniform binding paths for grip and aim poses across profiles
            XrActionSuggestedBinding.Buffer bindings = XrActionSuggestedBinding
                    .calloc(4, stack);
            bindings.get(0)
                    .set(
                            gripPoseAction,
                            OpenXRHelper.toPath(xrInstance, "/user/hand/left/input/grip/pose", stack)
                    );
            bindings.get(1)
                    .set(
                            aimPoseAction,
                            OpenXRHelper.toPath(xrInstance, "/user/hand/left/input/aim/pose", stack)
                    );
            bindings.get(2)
                    .set(
                            gripPoseAction,
                            OpenXRHelper.toPath(xrInstance, "/user/hand/right/input/grip/pose", stack)
                    );
            bindings.get(3)
                    .set(
                            aimPoseAction,
                            OpenXRHelper.toPath(xrInstance, "/user/hand/right/input/aim/pose", stack)
                    );


            XrInteractionProfileSuggestedBinding suggest = XrInteractionProfileSuggestedBinding
                    .calloc(stack)
                    .next(0)
                    .type(XR_TYPE_INTERACTION_PROFILE_SUGGESTED_BINDING)
                    .interactionProfile(profilePath)
                    .suggestedBindings(bindings);
            vrProvider.checkXRError(
                    xrSuggestInteractionProfileBindings(
                            xrInstance,
                            suggest
                    ),
                    "xrSuggestInteractionProfileBindings", profile
            );
        }
    };

    @Override
    public Collection<OpenXRDevice> getDevices() {
        return devices.values();
    }

    @Override
    public OpenXRDevice getDevice(String id) {
        return devices.get(id);
    }

    @Override
    public void registerDevice(OpenXRDevice device) {
        devices.put(device.getId(),device);
    }



}
