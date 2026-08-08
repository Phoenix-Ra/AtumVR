package me.phoenixra.atumvr.core.input.haptics;

import me.phoenixra.atumvr.api.input.haptics.AtumVRBodyHaptics;


public interface XRBodyHapticsProvider extends AtumVRBodyHaptics {


    boolean isSupported();


    default void onAttached() {
    }


    default void update() {
    }

    default void destroy() {
    }
}
