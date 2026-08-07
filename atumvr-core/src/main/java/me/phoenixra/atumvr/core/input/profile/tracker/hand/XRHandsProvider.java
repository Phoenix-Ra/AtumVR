package me.phoenixra.atumvr.core.input.profile.tracker.hand;

import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandsView;


public interface XRHandsProvider extends AtumVRHandsView {


    boolean isSupported();


    default void onAttached() {
    }

    default void update() {
    }

    default void destroy() {
    }
}
