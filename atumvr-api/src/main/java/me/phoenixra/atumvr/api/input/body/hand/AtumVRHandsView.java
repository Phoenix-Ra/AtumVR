package me.phoenixra.atumvr.api.input.body.hand;

import me.phoenixra.atumvr.api.enums.ControllerType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a view of both hand skeletons
 */
public interface AtumVRHandsView {

    AtumVRHandsView EMPTY = side -> AtumVRHandView.EMPTY;


    @NotNull AtumVRHandView getHand(@NotNull ControllerType side);


    default @NotNull AtumVRHandView getLeftHand() {
        return getHand(ControllerType.LEFT);
    }

    default @NotNull AtumVRHandView getRightHand() {
        return getHand(ControllerType.RIGHT);
    }
}
