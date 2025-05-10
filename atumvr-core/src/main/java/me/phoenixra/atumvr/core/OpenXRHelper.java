package me.phoenixra.atumvr.core;

import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;

import static org.lwjgl.openxr.XR10.xrStringToPath;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRHelper {
    public static XrSpace createReferenceSpace(OpenXRState state,
                                               int spaceType,
                                               XrPosef identityPose,
                                               MemoryStack stack) {
        XrReferenceSpaceCreateInfo spaceInfo = XrReferenceSpaceCreateInfo.calloc(stack)
                .type(XR10.XR_TYPE_REFERENCE_SPACE_CREATE_INFO)
                .next(NULL)
                .referenceSpaceType(spaceType)
                .poseInReferenceSpace(identityPose);

        XrSession handle = state.getXrSession().getHandle();
        PointerBuffer pSpace = stack.callocPointer(1);
        state.getVrProvider().checkXRError(
                XR10.xrCreateReferenceSpace(handle, spaceInfo, pSpace),
                "xrCreateReferenceSpace", "Spacetype: "+spaceType
        );

        return new XrSpace(pSpace.get(0), handle);
    }
    public static long toPath(XrInstance instance, String path, MemoryStack stack) {
        LongBuffer pb = stack.callocLong(1);
        xrStringToPath(instance, stack.UTF8(path), pb);
        return pb.get(0);
    }
}
