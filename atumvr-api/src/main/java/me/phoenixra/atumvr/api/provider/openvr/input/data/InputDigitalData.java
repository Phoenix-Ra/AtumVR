package me.phoenixra.atumvr.api.provider.openvr.input.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @AllArgsConstructor @NoArgsConstructor
public class InputDigitalData {

    /**
     * The origin that caused this action's current state
     **/
    private long activeOrigin;
    /**
     * Whether this action
     * is currently available to be bound in the active action set
     **/
    private boolean active;
    /**
     * The current state of this action;
     * will be true if currently pressed
     **/
    private boolean pressed;
    /**
     * This is true if the state
     * has changed since the last frame
     **/
    private boolean changed;
    /**
     * Time relative to now when this event happened.
     * Will be negative to indicate the past.
     **/
    private float updateTime;

}
