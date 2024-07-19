package me.phoenixra.atumvr.api.devices.controller;



public enum ControllerHand {
    MAIN,
    SECONDARY;


    public ControllerHand reversed(){
        if(this == SECONDARY) return MAIN;
        else return SECONDARY;
    }

    public static ControllerHand fromInt(int id){
        if(id == 0) return MAIN;
        return SECONDARY;
    }
}
