package me.phoenixra.atumvr.api.enums;

import lombok.Getter;

public enum EyeType {
    LEFT(0),
    RIGHT(1);

    @Getter
    private final int id;
    EyeType(int id){
       this.id = id;
    }

    public static EyeType fromInt(int id){
        if(id == 0) return LEFT;
        return RIGHT;
    }
}
