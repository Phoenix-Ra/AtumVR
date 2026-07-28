package me.phoenixra.atumvr.core.input.profile.tracker;

import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.device.XRDevice;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;


public interface XRTrackerProvider extends AtumVRBodyView {

    boolean isSupported();

    default @NotNull Collection<? extends XRActionSet> getActionSets() {
        return List.of();
    }


    default void onAttached() {
    }


    default void update() {
    }


    default @NotNull Collection<? extends XRDevice> getDevices() {
        return List.of();
    }


    @Override
    default void collectJoints(@NotNull BiConsumer<AtumVRBodyJoint, AtumVRPose> sink) {
        for (XRDevice device : getDevices()) {
            if (device instanceof AtumVRBodyView view) {
                view.collectJoints(sink);
            }
        }
    }



    default void destroy() {
    }
}
