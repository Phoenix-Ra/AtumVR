package me.phoenixra.atumvr.core.input.action.types.multi;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.ActionIdentifier;
import me.phoenixra.atumvr.api.misc.pose.VRPoseRecord;
import me.phoenixra.atumvr.core.XRHelper;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRMultiAction;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.system.MemoryStack.stackCallocPointer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PoseMultiAction extends XRMultiAction<VRPoseRecord> {

    @Getter
    private HashMap<SubAction<VRPoseRecord>, XrSpace> xrSpace = new HashMap<>();

    public PoseMultiAction(XRProvider provider,
                           XRActionSet actionSet,
                           ActionIdentifier id,
                           String localizedName,
                           List<SubAction<VRPoseRecord>> subActions) {
        super(provider, actionSet, id, localizedName, XRInputActionType.POSE, subActions);
    }

    @Override
    protected void onInit(XRActionSet actionSet, MemoryStack stack) {
        for (SubAction<VRPoseRecord> entry : subActions) {
            XrSession xrSession = provider.getState().getVrSession().getHandle();
            XrActionSpaceCreateInfo action_space_info = XrActionSpaceCreateInfo
                    .calloc(stack).set(
                            XR10.XR_TYPE_ACTION_SPACE_CREATE_INFO,
                            NULL,
                            handle,
                            entry.getPathHandle(),
                            XRHelper.getPoseIdentity(stack)
                    );
            PointerBuffer pp = stackCallocPointer(1);
            provider.checkXRError(
                    XR10.xrCreateActionSpace(
                            xrSession,
                            action_space_info, pp
                    ),
                    "xrCreateActionSpace"
            );
            xrSpace.put(entry, new XrSpace(pp.get(0), xrSession));
        }
    }

    @Override
    public void update(@Nullable Consumer<String> listener) {
        for (SubAction<VRPoseRecord> entry : subActions) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                var state = XrActionStatePose.calloc(stack)
                        .type(actionType.getStateId());
                getInfo.subactionPath(entry.getPathHandle());
                getInfo.action(handle);
                provider.checkXRError(
                        XR10.xrGetActionStatePose(
                                provider.getState().getVrSession().getHandle(),
                                getInfo,
                                state
                        ),
                        "xrGetActionStateFloat"
                );
                var loc = XRHelper.xrLocationFromSpace(
                        provider, xrSpace.get(entry), stack
                );
                VRPoseRecord entryState = loc == null
                        ? VRPoseRecord.EMPTY
                        :
                        new VRPoseRecord(
                                XRHelper.normalizeXrPose(loc.pose()),
                                XRHelper.normalizeXrQuaternion(loc.pose().orientation()),
                                XRHelper.normalizeXrVector(loc.pose().position$())
                        );

                entry.update(
                        entryState,
                        System.nanoTime(),
                        true,
                        state.isActive()
                );
            }
        }
    }


}
