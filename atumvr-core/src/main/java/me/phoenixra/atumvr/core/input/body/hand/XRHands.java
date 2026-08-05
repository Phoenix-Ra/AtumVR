package me.phoenixra.atumvr.core.input.body.hand;

import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandView;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandsView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class XRHands implements AtumVRHandsView {

    private final List<AtumVRHandsView> sources = new ArrayList<>();
    private final List<AtumVRHandsView> sourcesView = Collections.unmodifiableList(sources);

    private AtumVRHandView left = AtumVRHandView.EMPTY;
    private AtumVRHandView right = AtumVRHandView.EMPTY;


    @Override
    public @NotNull AtumVRHandView getHand(@NotNull ControllerType side) {
        return side == ControllerType.LEFT ? left : right;
    }


    public void update() {
        left = resolve(ControllerType.LEFT);
        right = resolve(ControllerType.RIGHT);
    }

    private @NotNull AtumVRHandView resolve(@NotNull ControllerType side) {
        for (AtumVRHandsView source : sources) {
            AtumVRHandView hand = source.getHand(side);
            if (hand.isTracked()) {
                return hand;
            }
        }
        return AtumVRHandView.EMPTY;
    }


    public void addSource(@NotNull AtumVRHandsView source) {
        if (source == this || sources.contains(source)) {
            return;
        }
        sources.add(source);
    }

    public void removeSource(@NotNull AtumVRHandsView source) {
        if (sources.remove(source)) {
            left = AtumVRHandView.EMPTY;
            right = AtumVRHandView.EMPTY;
        }
    }

    public void clearSources() {
        sources.clear();
        left = AtumVRHandView.EMPTY;
        right = AtumVRHandView.EMPTY;
    }


    public @NotNull List<AtumVRHandsView> getSources() {
        return sourcesView;
    }
}
