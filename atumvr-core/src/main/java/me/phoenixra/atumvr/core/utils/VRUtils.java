package me.phoenixra.atumvr.core.utils;

import me.phoenixra.atumvr.core.VRProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Utilities class containing methods, that help
 * to interact with OpenXR
 */
public class VRUtils {

    public static XrPosef getPoseIdentity(@NotNull MemoryStack stack){
        return XrPosef
                .calloc(stack)
                .orientation(XrQuaternionf.calloc(stack).set(0,0,0,1))
                .position$(XrVector3f.calloc(stack).set(0,0,0));
    }

    @Nullable
    public static XrSpaceLocation xrLocationFromSpace(@NotNull VRProvider vrProvider,
                                                      @NotNull XrSpace xrSpace,
                                                      @NotNull MemoryStack stack){
        XrSpaceLocation space_location = XrSpaceLocation.calloc(stack).type(XR10.XR_TYPE_SPACE_LOCATION);

        vrProvider.checkXRError(
                XR10.xrLocateSpace(
                        xrSpace,
                        vrProvider.getSession().getXrAppSpace(),
                        vrProvider.getXrDisplayTime(),
                        space_location
                ),
                "xrLocateSpace"
        );

        if ((space_location.locationFlags() & XR10.XR_SPACE_LOCATION_POSITION_VALID_BIT) != 0 &&
                (space_location.locationFlags() & XR10.XR_SPACE_LOCATION_ORIENTATION_VALID_BIT) != 0) {

            return space_location;
        }
        return null;
    }
    public static XrSpace createReferenceSpace(@NotNull VRProvider vrProvider,
                                               int spaceType,
                                               @NotNull XrPosef identityPose,
                                               @NotNull MemoryStack stack) {
        XrReferenceSpaceCreateInfo spaceInfo = XrReferenceSpaceCreateInfo.calloc(stack)
                .type(XR10.XR_TYPE_REFERENCE_SPACE_CREATE_INFO)
                .next(NULL)
                .referenceSpaceType(spaceType)
                .poseInReferenceSpace(identityPose);

        XrSession handle = vrProvider.getSession().getHandle();
        PointerBuffer pSpace = stack.callocPointer(1);
        vrProvider.checkXRError(
                XR10.xrCreateReferenceSpace(handle, spaceInfo, pSpace),
                "xrCreateReferenceSpace", "Spacetype: "+spaceType
        );

        return new XrSpace(pSpace.get(0), handle);
    }


    public static Matrix4f normalizeXrPose(@NotNull XrPosef xrPose){

        XrQuaternionf orientation = xrPose.orientation();
        XrVector3f position = xrPose.position$();

        Quaternionf rotation = new Quaternionf(
                orientation.x(),
                orientation.y(),
                orientation.z(),
                orientation.w()
        );
        return new Matrix4f().identity()
                .translate(position.x(), position.y(), position.z())
                .rotate(rotation);
    }

    public static Quaternionf normalizeXrQuaternion(@NotNull XrQuaternionf xrQuaternion){
        return new Quaternionf(
                xrQuaternion.x(),
                xrQuaternion.y(),
                xrQuaternion.z(),
                xrQuaternion.w()
        );
    }

    public static Vector2f normalizeXrVector(@NotNull XrVector2f xrVector){
        return new Vector2f(
                xrVector.x(),
                xrVector.y()
        );
    }
    public static Vector3f normalizeXrVector(@NotNull XrVector3f xrVector){
        return new Vector3f(
                xrVector.x(),
                xrVector.y(),
                xrVector.z()
        );
    }
    public static Vector4f normalizeXrVector(@NotNull XrVector4f xrVector){
        return new Vector4f(
                xrVector.x(),
                xrVector.y(),
                xrVector.z(),
                xrVector.w()
        );
    }
}
