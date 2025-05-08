package me.phoenixra.atumvr.api.input.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data @AllArgsConstructor @NoArgsConstructor
public class InputAnalogData {

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
     * If changed
     **/
    private boolean changed;

    /**
     * Time relative to now when this event happened.
     * Will be negative to indicate the past
     **/
    private float updateTime;

    /**
     * The current state of this action;
     * will be delta updates for mouse actions
     **/
    private float x;

    /**
     * The current state of this action;
     * will be delta updates for mouse actions
     **/
    private float y;

    /**
     * The current state of this action;
     * will be delta updates for mouse actions
     **/
    private float z;

    /**
     * deltas since the previous call
     **/
    private float deltaX;

    /**
     * deltas since the previous call
     **/
    private float deltaY;

    /**
     * deltas since the previous call
     **/
    private float deltaZ;

}
