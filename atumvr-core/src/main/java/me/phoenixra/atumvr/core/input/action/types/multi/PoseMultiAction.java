package me.phoenixra.atumvr.core.input.action.types.multi;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.action.data.VRActionDataPose;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfileType;
import me.phoenixra.atumvr.api.misc.pose.VRPoseRecord;
import me.phoenixra.atumvr.core.utils.XRUtils;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.XRMultiAction;
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

public class PoseMultiAction extends XRMultiAction<VRPoseRecord> {

    @Getter
    private HashMap<SubAction<VRPoseRecord>, XrSpace> xrSpace = new HashMap<>();

    @Getter
    private final List<SubActionPose> subActionsAsPose;


    public PoseMultiAction(@NotNull XRProvider vrProvider,
                           @NotNull XRActionSet actionSet,
                           @NotNull VRActionIdentifier id,
                           @NotNull String localizedName,
                           @NotNull List<SubActionPose> subActions) {
        super(vrProvider, actionSet, id, localizedName, XRInputActionType.POSE, subActions);
        subActionsAsPose = Collections.unmodifiableList(subActions);
    }

    @Override
    protected void onInit(@NotNull XRActionSet actionSet, @NotNull MemoryStack stack) {
        for (SubAction<VRPoseRecord> entry : subActions) {
            XrSession xrSession = vrProvider.getSession().getHandle();
            XrActionSpaceCreateInfo action_space_info = XrActionSpaceCreateInfo
                    .calloc(stack).set(
                            XR10.XR_TYPE_ACTION_SPACE_CREATE_INFO,
                            NULL,
                            handle,
                            entry.getPathHandle(),
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
                var loc = XRUtils.xrLocationFromSpace(
                        vrProvider, xrSpace.get(entry), stack
                );
                VRPoseRecord entryState = loc == null
                        ? VRPoseRecord.EMPTY
                        :
                        new VRPoseRecord(
                                XRUtils.normalizeXrPose(loc.pose()),
                                XRUtils.normalizeXrQuaternion(loc.pose().orientation()),
                                XRUtils.normalizeXrVector(loc.pose().position$())
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


        public SubActionPose(@NotNull VRActionIdentifier id,
                             @NotNull String path,
                             @NotNull VRPoseRecord initialState) {
            super(id, path, initialState);

        }

        @Override
        public SubActionPose putDefaultBindings(@NotNull List<XRInteractionProfileType> profiles, @Nullable String source) {
            return (SubActionPose) super.putDefaultBindings(profiles, source);
        }

        @Override
        public SubActionPose putDefaultBindings(@NotNull XRInteractionProfileType profile, @Nullable String source) {
            return (SubActionPose) super.putDefaultBindings(profile, source);
        }

        @Override
        public @NotNull VRPoseRecord getPose() {
            return currentState;
        }
    }

}
