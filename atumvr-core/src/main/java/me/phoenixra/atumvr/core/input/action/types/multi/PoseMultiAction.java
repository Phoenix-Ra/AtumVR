package me.phoenixra.atumvr.core.input.action.types.multi;

import lombok.Getter;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataPose;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.misc.pose.VRPoseRecord;
import me.phoenixra.atumvr.core.utils.VRUtils;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.VRMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackCallocPointer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PoseMultiAction extends VRMultiAction<VRPoseRecord> {

    @Getter
    private HashMap<SubAction<VRPoseRecord>, XrSpace> xrSpace = new HashMap<>();

    @Getter
    private final List<SubActionPose> subActionsAsPose;


    public PoseMultiAction(@NotNull VRProvider vrProvider,
                           @NotNull VRActionSet actionSet,
                           @NotNull ActionIdentifier id,
                           @NotNull String localizedName,
                           @NotNull List<SubActionPose> subActions) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.POSE, subActions);
        subActionsAsPose = Collections.unmodifiableList(subActions);
    }

    @Override
    protected void onInit(@NotNull VRActionSet actionSet, @NotNull MemoryStack stack) {
        for (SubAction<VRPoseRecord> entry : subActions) {
            XrSession xrSession = vrProvider.getSession().getHandle();
            XrActionSpaceCreateInfo action_space_info = XrActionSpaceCreateInfo
                    .calloc(stack).set(
                            XR10.XR_TYPE_ACTION_SPACE_CREATE_INFO,
                            NULL,
                            handle,
                            entry.getPathHandle(),
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
            xrSpace.put(entry, new XrSpace(pp.get(0), xrSession));
        }
    }

    @Override
    public void update() {
        for (var entry : subActionsAsPose) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                var state = XrActionStatePose.calloc(stack)
                        .type(actionType.getStateId());
                getInfo.subactionPath(entry.getPathHandle());
                getInfo.action(handle);
                vrProvider.checkXRError(
                        XR10.xrGetActionStatePose(
                                vrProvider.getSession().getHandle(),
                                getInfo,
                                state
                        ),
                        "xrGetActionStateFloat"
                );
                var loc = VRUtils.xrLocationFromSpace(
                        vrProvider, xrSpace.get(entry), stack
                );
                VRPoseRecord entryState = loc == null
                        ? VRPoseRecord.EMPTY
                        :
                        new VRPoseRecord(
                                VRUtils.normalizeXrPose(loc.pose()),
                                VRUtils.normalizeXrQuaternion(loc.pose().orientation()),
                                VRUtils.normalizeXrVector(loc.pose().position$())
                        );

                entry.update(
                        entryState,
                        System.nanoTime(),
                        true,
                        state.isActive()
                );

                if(entry.isChanged()){
                    vrProvider.getInputHandler().onActionChanged(
                            entry
                    );
                }
            }
        }
    }

    public static class SubActionPose extends SubAction<VRPoseRecord> implements VRActionDataPose {


        public SubActionPose(@NotNull ActionIdentifier id,
                             @NotNull String path,
                             @NotNull VRPoseRecord initialState) {
            super(id, path, initialState);

        }

        @Override
        public SubActionPose putDefaultBindings(@NotNull List<VRInteractionProfileType> profiles, @Nullable String source) {
            return (SubActionPose) super.putDefaultBindings(profiles, source);
        }

        @Override
        public SubActionPose putDefaultBindings(@NotNull VRInteractionProfileType profile, @Nullable String source) {
            return (SubActionPose) super.putDefaultBindings(profile, source);
        }

        @Override
        public @NotNull VRPoseRecord getPose() {
            return currentState;
        }
    }

}
