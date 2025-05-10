package me.phoenixra.atumvr.core.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerHand;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.devices.VRDeviceController;
import me.phoenixra.atumvr.core.OpenXRHelper;
import me.phoenixra.atumvr.core.OpenXRProvider;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.openxr.XR10.*;

public class OpenXRDeviceController extends OpenXRDevice implements VRDeviceController {
    @Getter
    private final ControllerType type;
    @Getter
    private final Matrix4f gripPose = new Matrix4f();
    @Getter
    private final Matrix4f aimPose = new Matrix4f();

    private boolean gripValid;
    private boolean aimValid;

    private XrSpace gripSpace;
    private XrSpace aimSpace;

    private final long subactionPath;
    private final XrAction gripPoseAction;
    private final XrAction aimPoseAction;

    public OpenXRDeviceController(OpenXRProvider provider,
                                  ControllerType controllerType,
                                  XrAction gripPoseAction,
                                  XrAction aimPoseAction,
                                  long subactionPath) {
        super(provider, "controller_"+controllerType.name().toLowerCase());
        this.gripPoseAction = gripPoseAction;
        this.aimPoseAction = aimPoseAction;
        this.subactionPath = subactionPath;
        this.type = controllerType;
    }

    @Override
    public void initSpace(MemoryStack stack) {
        XrSession xrSession = provider.getVrState().getXrSession().getHandle();

        // Create grip and aim spaces and store device
        PointerBuffer pg = stack.callocPointer(1);
        XrActionSpaceCreateInfo gsi = XrActionSpaceCreateInfo
                .calloc(stack)
                .type(XR_TYPE_ACTION_SPACE_CREATE_INFO)
                .action(gripPoseAction)
                .subactionPath(subactionPath)
                .poseInActionSpace(XrPosef
                        .calloc(stack)
                        .orientation(XrQuaternionf.create().set(0,0,0,1))
                        .position$(XrVector3f.create().set(0,0,0)));
        provider.checkXRError(
                xrCreateActionSpace(xrSession, gsi, pg),
                "xrCreateActionSpace", getId() + ".grip"
        );
        gripSpace = new XrSpace(pg.get(0), xrSession);

        PointerBuffer pa = stack.callocPointer(1);
        XrActionSpaceCreateInfo asi = XrActionSpaceCreateInfo
                .calloc(stack)
                .type(XR_TYPE_ACTION_SPACE_CREATE_INFO)
                .action(aimPoseAction)
                .subactionPath(subactionPath)
                .poseInActionSpace(XrPosef
                        .calloc(stack)
                        .orientation(XrQuaternionf.create().set(0,0,0,1))
                        .position$(XrVector3f.create().set(0,0,0)));
        provider.checkXRError(
                xrCreateActionSpace(xrSession, asi, pa),
                "xrCreateActionSpace", getId() + ".aim"
        );
        aimSpace = new XrSpace(pa.get(0), xrSession);
        space = aimSpace;

    }
    public void update(long predictedTime){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            onUpdate(predictedTime, stack);
        }
    }
    @Override
    protected void onUpdate(long predictedTime, MemoryStack stack) {
        XrSession session = provider.getVrState().getXrSession().getHandle();
        XrSpace appSpace = provider.getVrState().getXrSession().getXrAppSpace();

        XrActionStateGetInfo gi = XrActionStateGetInfo
                .calloc(stack)
                .type(XR_TYPE_ACTION_STATE_GET_INFO)
                .action(gripPoseAction)
                .subactionPath(subactionPath);
        XrActionStatePose asp = XrActionStatePose
                .calloc(stack)
                .type(XR_TYPE_ACTION_STATE_POSE);
        xrGetActionStatePose(session, gi, asp);
        gripValid = asp.isActive();

        // Aim action state
        gi = XrActionStateGetInfo
                .calloc(stack)
                .type(XR_TYPE_ACTION_STATE_GET_INFO)
                .action(aimPoseAction)
                .subactionPath(subactionPath);
        asp = XrActionStatePose
                .calloc(stack)
                .type(XR_TYPE_ACTION_STATE_POSE);
        xrGetActionStatePose(session, gi, asp);
        aimValid = asp.isActive();


        active = gripValid || aimValid;

        XrSpaceLocation loc = XrSpaceLocation
                .calloc(stack)
                .type(XR_TYPE_SPACE_LOCATION);
        if (aimValid) {
            xrLocateSpace(aimSpace, appSpace, predictedTime, loc);
            long f = loc.locationFlags();
            if ((f & XR_SPACE_LOCATION_POSITION_VALID_BIT) != 0
                    && (f & XR_SPACE_LOCATION_ORIENTATION_VALID_BIT) != 0) {
                XrPosef p = loc.pose();
                aimPose.identity()
                        .translate(p.position$().x(), p.position$().y(), p.position$().z())
                        .rotate(p.orientation().x(), p.orientation().y(), p.orientation().z(), p.orientation().w());
            }
        }
        loc = XrSpaceLocation
                .calloc(stack)
                .type(XR_TYPE_SPACE_LOCATION);
        if (gripValid) {
            xrLocateSpace(gripSpace, appSpace, predictedTime, loc);
            long f = loc.locationFlags();
            if ((f & XR_SPACE_LOCATION_POSITION_VALID_BIT) != 0
                    && (f & XR_SPACE_LOCATION_ORIENTATION_VALID_BIT) != 0) {
                XrPosef p = loc.pose();
                gripPose.identity()
                        .translate(p.position$().x(), p.position$().y(), p.position$().z())
                        .rotate(p.orientation().x(), p.orientation().y(), p.orientation().z(), p.orientation().w());
            }
        }

    }


}
