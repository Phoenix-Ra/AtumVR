package me.phoenixra.atumvr.core.input.action.types.single;

import lombok.Getter;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataPose;
import me.phoenixra.atumvr.core.misc.pose.VRPoseRecord;
import me.phoenixra.atumvr.core.utils.VRUtils;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.VRSingleAction;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;


import static org.lwjgl.openxr.XR10.XR_NULL_PATH;
import static org.lwjgl.system.MemoryStack.stackCallocPointer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PoseAction extends VRSingleAction<VRPoseRecord> implements VRActionDataPose {



    @Getter
    private XrSpace xrSpace;

    public PoseAction(@NotNull VRProvider vrProvider,
                      @NotNull VRActionSet actionSet,
                      @NotNull ActionIdentifier id,
                      @NotNull String localizedName) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.POSE);
        currentState = VRPoseRecord.EMPTY;

    }
    @Override
    protected void onInit(@NotNull VRActionSet actionSet, @NotNull MemoryStack stack) {
        XrSession xrSession = vrProvider.getSession().getHandle();
        XrActionSpaceCreateInfo action_space_info = XrActionSpaceCreateInfo
                .calloc(stack).set(
                        XR10.XR_TYPE_ACTION_SPACE_CREATE_INFO,
                        NULL,
                        handle,
                        XR_NULL_PATH,
                        VRUtils.getPoseIdentity(stack)
                );
        PointerBuffer pp = stackCallocPointer(1);
        vrProvider.checkXRError(
                XR10.xrCreateActionSpace(
                        xrSession,
                        action_space_info, pp
                ),
                "xrCreateActionSpace"
        );
        xrSpace = new XrSpace(pp.get(0), xrSession);
    }


    @Override
    public void update() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            var state = XrActionStatePose.calloc(stack)
                    .type(actionType.getStateId());
            getInfo.action(handle);
            vrProvider.checkXRError(
                    XR10.xrGetActionStatePose(
                            vrProvider.getSession().getHandle(),
                            getInfo,
                            state
                    ),
                    "xrGetActionStateFloat"
            );
            this.changed = true;
            this.lastChangeTime = System.nanoTime();
            this.active = state.isActive();

            var loc = VRUtils.xrLocationFromSpace(
                    vrProvider, xrSpace, stack
            );

            currentState = loc == null
                    ? VRPoseRecord.EMPTY
                    :
                    new VRPoseRecord(
                            VRUtils.normalizeXrPose(loc.pose()),
                            VRUtils.normalizeXrQuaternion(loc.pose().orientation()),
                            VRUtils.normalizeXrVector(loc.pose().position$())
                    );

            if(changed){
                vrProvider.getInputHandler().onActionChanged(
                        this
                );
            }
        }
    }

    @Override
    public @NotNull VRPoseRecord getPose() {
        return currentState;
    }
}
