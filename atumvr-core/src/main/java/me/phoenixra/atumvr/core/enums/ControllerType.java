package me.phoenixra.atumvr.core.enums;


import lombok.Getter;

/**
 * Controller type enum
 */
@Getter
public enum ControllerType {

    /**
     * Left Controller
     */
    LEFT(0),

    /**
     * Right Controller
     */
    RIGHT(1);

    /**
     * Index of a controller
     */
    private final int index;

    ControllerType(int index){
        this.index = index;
    }

    /**
     * Get reversed controller type.
     * <p>
     *     (LEFT-> RIGHT)<br>
     *     (RIGHT-> LEFT)
     * </p>
     *
     * @return the ControllerType reversed
     */
    public ControllerType reversed(){
        if(this == LEFT) return RIGHT;
        else return LEFT;
    }

    /**
     * Get ControllerType associated with specified index
     *
     * @param index the index
     * @return the ControllerType
     */
    public static ControllerType fromIndex(int index){
        if(index == ControllerType.LEFT.getIndex()) return LEFT;
        return RIGHT;
    }


}
