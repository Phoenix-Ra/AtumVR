package me.phoenixra.atumvr.api.input.device;

import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;

/**
 * VRDevice for Vive Tracker
 */
public interface AtumVRDeviceViveTracker extends AtumVRDeviceTracker {

    ViveTrackerRole getRole();

}
