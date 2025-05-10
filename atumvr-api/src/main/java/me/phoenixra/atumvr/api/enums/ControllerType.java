package me.phoenixra.atumvr.api.enums;

public enum ControllerType {
    LEFT,
    RIGHT;

    public ControllerType reversed(){
        if(this == LEFT) return RIGHT;
        else return LEFT;
    }

    public static ControllerType fromInt(int id){
        if(id == 0) return LEFT;
        return RIGHT;
    }
}
