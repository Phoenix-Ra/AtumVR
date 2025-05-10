package me.phoenixra.atumvr.core.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.devices.VRDevice;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.init.OpenXRSession;
import org.joml.Matrix4f;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.openxr.XR10.*;

public abstract class OpenXRDevice implements VRDevice {
    @Getter
    protected final OpenXRProvider provider;
    @Getter
    private final String id;
    @Getter
    private final Matrix4f pose = new Matrix4f();

    @Getter
    protected boolean active, poseValid;


    protected XrAction poseAction;
    protected XrSpace space;

    public OpenXRDevice(OpenXRProvider provider, String id){
        this.provider = provider;
        this.id = id;
    }

    public abstract void initSpace(MemoryStack stack);
    protected abstract void onUpdate(long predictedTime, MemoryStack stack);


    public void update(long predictedTime) {
        OpenXRSession session = provider.getVrState().getXrSession();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Check action-state for controller pose
            if(poseAction != null) {
                XrActionStateGetInfo gi = XrActionStateGetInfo
                        .calloc(stack)
                        .type(XR_TYPE_ACTION_STATE_GET_INFO)
                        .action(poseAction)
                        .subactionPath(space.address()); // note: subactionPath stored in space.addr if needed
                XrActionStatePose asp = XrActionStatePose
                        .calloc(stack)
                        .type(XR_TYPE_ACTION_STATE_POSE);
                xrGetActionStatePose(session.getHandle(), gi, asp);
                active = asp.isActive();
                if (!active) {
                    poseValid = false;
                    return;
                }
            }
            XrSpaceLocation loc = XrSpaceLocation
                    .calloc(stack)
                    .type(XR_TYPE_SPACE_LOCATION);
            xrLocateSpace(space, session.getXrAppSpace(), predictedTime, loc);
            long f = loc.locationFlags();
            poseValid = ((f & XR_SPACE_LOCATION_POSITION_VALID_BIT) != 0)
                    && ((f & XR_SPACE_LOCATION_ORIENTATION_VALID_BIT) != 0);
            active = poseValid;
            if (poseValid) {
                XrPosef p = loc.pose();
                pose.identity()
                        .translate(p.position$().x(), p.position$().y(), p.position$().z())
                        .rotate(p.orientation().x(), p.orientation().y(), p.orientation().z(), p.orientation().w());
            }
            onUpdate(predictedTime, stack);
        }
    }


}
