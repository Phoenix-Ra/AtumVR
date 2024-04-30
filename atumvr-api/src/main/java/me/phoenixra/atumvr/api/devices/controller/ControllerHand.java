package me.phoenixra.atumvr.api.devices.controller;



public enum ControllerHand {
    RIGHT,
    LEFT;


    public ControllerHand reversed(){
        if(this == LEFT) return RIGHT;
        else return LEFT;
    }

    public static ControllerHand fromInt(int id){
        if(id == 0) return RIGHT;
        return LEFT;
    }
}
