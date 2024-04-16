package me.phoenixra.atumvr.api.devices.controller;

import lombok.Getter;

public enum ControllerHand {
    LEFT,
    RIGHT;


    public ControllerHand reversed(){
        if(this == LEFT) return RIGHT;
        else return LEFT;
    }

    public static ControllerHand fromInt(int id){
        if(id == 0) return LEFT;
        return RIGHT;
    }
}
