package me.phoenixra.atumvr.api.enums;

import lombok.Getter;


/**
 * Eye type enum
 */
@Getter
public enum EyeType {

    /**
     * Left Eye
     */
    LEFT(0),

    /**
     * Right Eye
     */
    RIGHT(1);

    /**
     * Index of an eye
     */
    private final int index;

    EyeType(int index){
       this.index = index;
    }

    /**
     * Get EyeType associated with specified index
     *
     * @param index the index
     * @return the EyeType
     */
    public static EyeType fromIndex(int index){
        if(index == EyeType.LEFT.index) return LEFT;
        return RIGHT;
    }
}
