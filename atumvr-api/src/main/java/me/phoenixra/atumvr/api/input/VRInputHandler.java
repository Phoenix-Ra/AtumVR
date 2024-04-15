package me.phoenixra.atumvr.api.input;

import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.exceptions.VRInputException;
import me.phoenixra.atumvr.api.input.data.*;
import me.phoenixra.atumvr.api.utils.VRUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openvr.InputAnalogActionData;
import org.lwjgl.openvr.InputDigitalActionData;
import org.lwjgl.openvr.InputOriginInfo;
import org.lwjgl.openvr.VRInput;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openvr.VRInput.*;
import static org.lwjgl.openvr.VRInput.VRInput_GetOriginTrackedDeviceInfo;

public interface VRInputHandler {

    void init();
    void tick();

    //queried every tick
    @NotNull
    List<VRInputActionSetData> getActiveActionSets();

    @NotNull
    List<VRInputActionData> getInputActionsData();

    @NotNull File getActionManifest();




    static long getInputActionHandle(@NotNull String actionPath,
                                     @NotNull MemoryStack stack) throws VRInputException {

        LongBuffer result = stack.mallocLong(1);
        int error = VRInput.VRInput_GetActionHandle(actionPath, result);

        if (error != 0) {
            throw new VRInputException(
                    "Error getting action handle for '" + actionPath+"'",
                    error
            );
        }
        return result.get(0);
    }

    static long getInputActionSetHandle(@NotNull String actionSetPath,
                                        @NotNull MemoryStack stack) throws VRInputException{
        LongBuffer result = stack.mallocLong(1);
        int error = VRInput_GetActionSetHandle(actionSetPath, result);

        if (error != 0) {
            throw new VRInputException(
                    "Error getting action set handle for '"+actionSetPath+"'", error
            );
        }
        return result.get(0);
    }
    static long getInputSourceHandle(@NotNull String path,
                                     @NotNull MemoryStack stack) throws VRInputException{
        LongBuffer result = stack.mallocLong(1);
        int error = VRInput_GetInputSourceHandle(path, result);

        if (error != 0) {
            throw new VRInputException(
                    "Error getting input source handle for '"+path+"'", error
            );
        }
        return result.get(0);
    }


    static List<Long> getActionOrigins(String actionSet,
                                       long handle,
                                       String nameForLog,
                                       @NotNull MemoryStack stack) {
        LongBuffer result = stack.mallocLong(16);
        int i = VRInput_GetActionOrigins(
                getInputActionSetHandle(actionSet,stack),
                handle,
                result
        );

        if (i != 0) {
            throw new RuntimeException("Error getting action origins for" +
                    " '" + nameForLog + "': " + VRUtils.getInputErrorMessage(i));
        } else {
            List<Long> list = new ArrayList<>();

            while (result.remaining() > 0) {
                long j = result.get();
                if (j != 0L) {
                    list.add(j);
                }
            }

            return list;
        }
    }

    static InputDigitalData getDigitalData(long controllerHandle,
                                           long actionHandle,
                                           @NotNull String nameForLog,
                                           @NotNull MemoryStack stack) {
        InputDigitalActionData out = InputDigitalActionData.malloc(stack);
        int error = VRInput_GetDigitalActionData(actionHandle,
                out,
                InputDigitalActionData.SIZEOF,
                controllerHandle
        );

        if (error != 0) {
            throw new RuntimeException("Error reading digital data for '"
                    + nameForLog + "': " + VRUtils.getInputErrorMessage(error));
        } else {
            return new InputDigitalData(
                    out.activeOrigin(),
                    out.bActive(),
                    out.bState(),
                    out.bChanged(),
                    out.fUpdateTime()
            );
        }
    }


    static InputAnalogData getAnalogData(long controllerHandle,
                                         long actionHandle,
                                         @NotNull String nameForLog,
                                         @NotNull MemoryStack stack) {
        InputAnalogActionData out = InputAnalogActionData.malloc(stack);
        int j = VRInput_GetAnalogActionData(
                actionHandle,
                out,
                InputAnalogActionData.SIZEOF,
                controllerHandle
        );

        if (j != 0) {
            throw new RuntimeException("Error reading analog data for '"
                    + nameForLog + "': " + VRUtils.getInputErrorMessage(j));
        } else {
            return new InputAnalogData(
                    out.activeOrigin(),
                    out.bActive(),
                    false,
                    out.fUpdateTime(),
                    out.x(),
                    out.y(),
                    out.z(),
                    out.deltaX(),
                    out.deltaY(),
                    out.deltaZ()
            );
        }
    }
    static InputOriginDeviceInfo getOriginInfo(long actionHandle,
                                               @NotNull MemoryStack stack) {
        InputOriginInfo out = InputOriginInfo.malloc(stack);
        int error = VRInput_GetOriginTrackedDeviceInfo(
                actionHandle,
                out,
                InputOriginInfo.SIZEOF
        );

        if (error != 0) {
            throw new RuntimeException("Error reading origin info: " + VRUtils.getInputErrorMessage(error));
        }
        return new InputOriginDeviceInfo(
                out.devicePath(),
                out.trackedDeviceIndex(),
                out.rchRenderModelComponentNameString()
        );
    }



    @NotNull
    VRCore getVrCore();
}
