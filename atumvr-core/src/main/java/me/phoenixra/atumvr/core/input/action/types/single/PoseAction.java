package me.phoenixra.atumvr.core.input.action.types.single;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataPose;
import me.phoenixra.atumvr.api.misc.pose.VRPoseRecord;
import me.phoenixra.atumvr.core.utils.XRUtils;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRSingleAction;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;


import static org.lwjgl.openxr.XR10.XR_NULL_PATH;
import static org.lwjgl.system.MemoryStack.stackCallocPointer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PoseAction extends XRSingleAction<VRPoseRecord> implements VRActionDataPose {



    @Getter
    private XrSpace xrSpace;

    public PoseAction(@NotNull XRProvider vrProvider,
                      @NotNull XRActionSet actionSet,
                      @NotNull VRActionIdentifier id,
                      @NotNull String localizedName) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.POSE);
        currentState = VRPoseRecord.EMPTY;

    }
    @Override
    protected void onInit(@NotNull XRActionSet actionSet, @NotNull MemoryStack stack) {
        XrSession xrSession = vrProvider.getSession().getHandle();
        XrActionSpaceCreateInfo action_space_info = XrActionSpaceCreateInfo
                .calloc(stack).set(
                        XR10.XR_TYPE_ACTION_SPACE_CREATE_INFO,
                        NULL,
                        handle,
                        XR_NULL_PATH,
                        XRUtils.getPoseIdentity(stack)
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

            var loc = XRUtils.xrLocationFromSpace(
                    vrProvider, xrSpace, stack
            );

            currentState = loc == null
                    ? VRPoseRecord.EMPTY
                    :
                    new VRPoseRecord(
                            XRUtils.normalizeXrPose(loc.pose()),
                            XRUtils.normalizeXrQuaternion(loc.pose().orientation()),
                            XRUtils.normalizeXrVector(loc.pose().position$())
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
