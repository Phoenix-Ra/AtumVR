package me.phoenixra.atumvr.core.devices;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.devices.VRDevice;
import me.phoenixra.atumvr.api.devices.VRDeviceRole;
import me.phoenixra.atumvr.api.devices.VRDeviceType;
import me.phoenixra.atumvr.api.devices.VRDevicesManager;
import me.phoenixra.atumvr.api.devices.pose.TrackingState;
import me.phoenixra.atumvr.api.devices.pose.VRDevicePose;
import me.phoenixra.atumvr.api.devices.pose.bones.ControllerBones;
import me.phoenixra.atumvr.api.misc.VRLocation;
import me.phoenixra.atumvr.api.utils.VRUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AtumVRDevicesManager implements VRDevicesManager {

    @Getter
    private final VRCore vrCore;
    private final Map<String, VRDevice> devices = new ConcurrentHashMap<>();

    private final Map<String, List<Consumer<VRDevice>>> onDisconnected = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<VRDevice>>> onConnected = new ConcurrentHashMap<>();


    @Getter @Setter
    private boolean waitPoses = true;
    public AtumVRDevicesManager(@NotNull VRCore core){
        this.vrCore = core;
    }
    @Override
    public void update() {
        HashMap<String,VRDevice> newDevices = new HashMap<>();
        HashMap<String,VRDevice> devicesUpdate = new HashMap<>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            //update the poses
            if(waitPoses) {
                VRCompositor.VRCompositor_WaitGetPoses(
                        TrackedDevicePose.malloc(VR.k_unMaxTrackedDeviceCount),
                        null
                );
            }

            for (int deviceIndex = 0; deviceIndex < VR.k_unMaxTrackedDeviceCount; deviceIndex++) {
                boolean isConnected = VRSystem.VRSystem_IsTrackedDeviceConnected(deviceIndex);
                String serialNumber = VRDevice.getDeviceStringProperty(stack,
                        deviceIndex,
                        VR.ETrackedDeviceProperty_Prop_SerialNumber_String
                );
                if (isConnected) {
                    String modelNumber = VRDevice.getDeviceStringProperty(stack,
                            deviceIndex,
                            VR.ETrackedDeviceProperty_Prop_ModelNumber_String
                    );
                    String manufacturerName = VRDevice.getDeviceStringProperty(stack,
                            deviceIndex,
                            VR.ETrackedDeviceProperty_Prop_ManufacturerName_String
                    );
                    String trackingSystemName = VRDevice.getDeviceStringProperty(stack,
                            deviceIndex,
                            VR.ETrackedDeviceProperty_Prop_TrackingSystemName_String
                    );
                    int roleHint = VRSystem.VRSystem_GetInt32TrackedDeviceProperty(
                            deviceIndex,
                            VR.ETrackedDeviceProperty_Prop_ControllerRoleHint_Int32,
                            null
                    );
                    VRDeviceRole role = VRDeviceRole.parseFromInt(roleHint);

                    int deviceType = VRSystem.VRSystem_GetTrackedDeviceClass(deviceIndex);

                    //----POSE
                    TrackedDevicePose pose = TrackedDevicePose.malloc(stack);
                    VRCompositor.VRCompositor_GetLastPoseForTrackedDeviceIndex(
                            deviceIndex, null, pose
                    );

                    VRLocation location = new VRLocation(
                            pose.mDeviceToAbsoluteTracking()
                    );

                    // Velocity
                    float[] velocity = new float[3];
                    velocity[0] = pose.vVelocity().v(0);
                    velocity[1] = pose.vVelocity().v(1);
                    velocity[2] = pose.vVelocity().v(2);

                    // Angular velocity
                    float[] angularVelocity = new float[3];
                    angularVelocity[0] = pose.vAngularVelocity().v(0);
                    angularVelocity[1] = pose.vAngularVelocity().v(1);
                    angularVelocity[2] = pose.vAngularVelocity().v(2);

                    // Tracking state
                    VRDevicePose devicePose = new VRDevicePose(
                            pose.bPoseIsValid(),
                            TrackingState.parseFromInt(pose.eTrackingResult()),
                            location,
                            VRUtils.convertVrMatrix( pose.mDeviceToAbsoluteTracking()),
                            velocity,
                            angularVelocity
                    );

                    //@TODO remake the input
                    //CONTROLLER BONES
                    ControllerBones controllerBones = null;
                    //--READY
                    VRDevice device = new VRDevice(
                            deviceIndex,
                            modelNumber,
                            serialNumber,
                            manufacturerName,
                            trackingSystemName,
                            role,
                            VRDeviceType.parseFromInt(deviceType),
                            devicePose,
                            controllerBones
                    );
                    devicesUpdate.put(
                            serialNumber,
                            device
                    );

                    VRDevice device1 = devices.get(device.getSerialNumber());

                    if(device1 == null){
                        newDevices.put(
                                serialNumber,
                                device
                        );
                    }else{
                        device1.setDeviceIndex(deviceIndex);
                        device1.updatePose(devicePose);
                    }
                }
            }

            for(VRDevice vrDevice : newDevices.values()){

                devices.put(vrDevice.getSerialNumber(),vrDevice);
                vrCore.logInfo("Device connected: " + vrDevice.getSerialNumber()
                        + " [  Type: " + vrDevice.getType() +" Role: " + vrDevice.getRole()+ " ]");

                List<Consumer<VRDevice>> list = onConnected.get(vrDevice.getSerialNumber());
                if(list==null) continue;
                list.forEach(it->it.accept(vrDevice));

            }

            Set<String> disconnectedSerials = new HashSet<>(devices.keySet());
            disconnectedSerials.removeAll(devicesUpdate.keySet());
            for (String serial : disconnectedSerials) {
                VRDevice vrDevice = devices.remove(serial);
                if (vrDevice != null) { // Ensure the device was indeed previously tracked
                    vrCore.logInfo("Device disconnected: " + vrDevice.getSerialNumber()
                            + " [  Type: " + vrDevice.getType() +" Role: " + vrDevice.getRole()+ " ]");

                    List<Consumer<VRDevice>> list = onDisconnected.get(serial);
                    if (list != null) {
                        list.forEach(consumer -> consumer.accept(vrDevice));
                    }
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addObserverOnDeviceConnected(
            String serialNum,
            Consumer<VRDevice> deviceConsumer
    ) {
        List<Consumer<VRDevice>> list = onConnected.get(serialNum);

        if(list == null){
            list = new ArrayList<>();
        }

        list.add(deviceConsumer);
        onConnected.put(serialNum, list);
    }
    @Override
    public void addObserverOnDeviceDisconnected(
            String serialNum,
            Consumer<VRDevice> deviceConsumer
    ) {
        List<Consumer<VRDevice>> list = onDisconnected.get(serialNum);

        if(list == null){
            list = new ArrayList<>();
        }

        list.add(deviceConsumer);
        onDisconnected.put(serialNum, list);
    }

    @Override
    public void removeObserverOnDeviceConnected(
            String serialNum,
            Consumer<VRDevice> deviceConsumer
    ) {
        List<Consumer<VRDevice>> list = onConnected.get(serialNum);
        if(list==null) return;

        list.remove(deviceConsumer);
        if(list.isEmpty()) {
            onConnected.remove(serialNum);
            return;
        }
        onConnected.put(serialNum, list);
    }

    @Override
    public void removeObserverOnDeviceDisconnected(
            String serialNum,
            Consumer<VRDevice> deviceConsumer
    ) {
        List<Consumer<VRDevice>> list = onDisconnected.get(serialNum);
        if(list==null) return;

        list.remove(deviceConsumer);
        if(list.isEmpty()) {
            onDisconnected.remove(serialNum);
            return;
        }
        onDisconnected.put(serialNum, list);
    }

    @Override
    public @NotNull List<VRDevice> getAvailableDevices() {
        return new ArrayList<>(devices.values());
    }

    @Override
    public @NotNull List<VRDevice> getDevicesByRole(@NotNull VRDeviceRole role) {
        return devices.values().stream().filter(it->it.getRole() == role).collect(Collectors.toList());
    }

    @Override
    public @NotNull List<VRDevice> getDevicesByType(@NotNull VRDeviceType type) {
        return devices.values().stream().filter(it->it.getType() == type).collect(Collectors.toList());
    }


    @Override
    public @Nullable VRDevice getDeviceBySerial(@NotNull String serial) {
        return devices.get(serial);
    }

    @Override
    public @Nullable VRDevice getDeviceByIndex(int index) {
        for(VRDevice device : devices.values()){
            if(device.getDeviceIndex() == index) {
                return device;
            }
        }
        return null;
    }

    @Override
    public @Nullable VRDevice getHMD() {
        return devices.values().stream().filter(it->it.getType() == VRDeviceType.HMD)
                .findAny().orElse(null);
    }

    @Override
    public @Nullable VRDevice getRightHand() {
        return devices.values().stream().filter(it->it.getRole() == VRDeviceRole.RIGHT_HAND)
                .findAny().orElse(null);
    }

    @Override
    public @Nullable VRDevice getLeftHand() {
        return devices.values().stream().filter(it->it.getRole() == VRDeviceRole.LEFT_HAND)
                .findAny().orElse(null);
    }
}