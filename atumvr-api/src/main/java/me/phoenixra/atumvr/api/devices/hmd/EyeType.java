package me.phoenixra.atumvr.api.devices.hmd;

import lombok.Getter;

public enum EyeType {
    LEFT(0),
    RIGHT(1);

    @Getter
    private int id;
    EyeType(int id){
       this.id = id;
    }

    public static EyeType fromInt(int id){
        if(id == 0) return LEFT;
        return RIGHT;
    }
}
