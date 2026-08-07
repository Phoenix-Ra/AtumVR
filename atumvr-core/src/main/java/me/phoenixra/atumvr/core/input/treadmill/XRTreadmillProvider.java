package me.phoenixra.atumvr.core.input.treadmill;

import me.phoenixra.atumvr.api.input.treadmill.AtumVRTreadmillView;


public interface XRTreadmillProvider extends AtumVRTreadmillView {


    boolean isSupported();


    default void onAttached() {
    }


    default void update() {
    }

    default void destroy() {
    }
}
