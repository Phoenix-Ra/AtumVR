package me.phoenixra.atumvr.api.devices.controller;

import lombok.Getter;

public enum ControllerHand {
    LEFT(0),
    RIGHT(1);

    @Getter
    private int id;
    ControllerHand(int id){
        this.id = id;
    }


    public static ControllerHand fromInt(int id){
        if(id == 0) return LEFT;
        return RIGHT;
    }
}
