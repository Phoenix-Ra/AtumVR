package me.phoenixra.atumvr.devices;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.phoenixra.atumvr.devices.pose.VRDevicePose;
import me.phoenixra.atumvr.devices.pose.bones.ControllerBones;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Data @AllArgsConstructor
public class VRDevice {

    private final String modelNumber;
    private final String serialNumber;

    private final String manufacturerName;
    private final String trackingSystemName;
    private final VRDeviceRole role;
    private final VRDeviceType type;

    private VRDevicePose pose;

    @Nullable
    private ControllerBones controllerBones;


    public void updatePose(VRDevicePose pose){
        this.pose = pose;
    }


    /**
     * If device with given index is connected.
     * <br>
     * Don't know the index? Use {@link VRDevice#findDeviceIndex(MemoryStack)}
     * @param index index of a device in OpenVR framework
     * @return if connected
     */
    public static boolean isDeviceConnected(int index){
        return VRSystem.VRSystem_IsTrackedDeviceConnected(
                index
        );
    }

    /**
     * Find index of a device in OpenVR framework or -1 if not found
     * @param stack memory stack to use
     * @return index or -1
     */
    public int findDeviceIndex(MemoryStack stack){
        for (int deviceIndex = 0; deviceIndex < VR.k_unMaxTrackedDeviceCount; deviceIndex++) {
            if (VRSystem.VRSystem_IsTrackedDeviceConnected(deviceIndex)) {
                String deviceSerial = getDeviceStringProperty(stack,deviceIndex, VR.ETrackedDeviceProperty_Prop_SerialNumber_String);
                if (serialNumber.equals(deviceSerial)) {
                    return deviceIndex; // Found the device
                }
            }
        }
        return -1;
    }

    public static String getDeviceStringProperty(MemoryStack stack, int deviceIndex, int property) {
        IntBuffer error = stack.mallocInt(1);
        int capacity = VRSystem.VRSystem_GetStringTrackedDeviceProperty(deviceIndex, property, null, error);

        if (capacity > 1) {
            ByteBuffer buffer = stack.malloc(capacity);
            VRSystem.VRSystem_GetStringTrackedDeviceProperty(deviceIndex, property, buffer, error);
            if (error.get(0) == VR.ETrackedPropertyError_TrackedProp_Success) {
                return MemoryUtil.memUTF8(buffer, capacity - 1); // Exclude null terminator
            }
        }
        return "Unknown";
    }

}
