package me.phoenixra.atumvr.api.provider.openvr.devices.pose;

import me.phoenixra.atumvr.api.provider.openvr.devices.VRDevice;
import me.phoenixra.atumvr.api.provider.openvr.devices.pose.impl.*;
import me.phoenixra.atumvr.api.provider.openvr.devices.pose.impl.*;
import me.phoenixra.atumvr.api.provider.openvr.devices.pose.impl.*;
import org.jetbrains.annotations.NotNull;


@FunctionalInterface
public interface DevicePoseMatch {
    boolean isMatching(@NotNull VRDevice vrDevice);

    DevicePoseMatch MATCHER_CONTROLLER_UP = new DevicePoseControllerUp();
    DevicePoseMatch MATCHER_CONTROLLER_DOWN = new DevicePoseControllerDown();

    DevicePoseMatch MATCHER_CONTROLLER_LEFT = new DevicePoseControllerLeft();
    DevicePoseMatch MATCHER_CONTROLLER_RIGHT = new DevicePoseControllerRight();

    DevicePoseMatch MATCHER_CONTROLLER_FORWARD = new DevicePoseControllerForward();


    static String getDevicePose(VRDevice device){
        if(MATCHER_CONTROLLER_UP.isMatching(device)){
            return "CONTROLLER UP";
        }else if(MATCHER_CONTROLLER_DOWN.isMatching(device)){
            return "CONTROLLER DOWN";
        }else if(MATCHER_CONTROLLER_LEFT.isMatching(device)){
            return "CONTROLLER LEFT";
        }else if(MATCHER_CONTROLLER_RIGHT.isMatching(device)){
            return "CONTROLLER RIGHT";
        }else if(MATCHER_CONTROLLER_FORWARD.isMatching(device)){
            return "CONTROLLER FORWARD";
        }
        return "UNKNOWN";

    }





}
