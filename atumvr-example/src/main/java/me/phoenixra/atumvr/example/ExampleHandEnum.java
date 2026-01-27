package me.phoenixra.atumvr.example;


import me.phoenixra.atumvr.api.enums.ControllerType;
import org.jetbrains.annotations.NotNull;

/**
 * Convenient way of implementing support for left-handed
 * if u have a primary and secondary hands in your game.
 *
 */
public enum ExampleHandEnum {
    MAIN,
    OFFHAND;


    public @NotNull ControllerType asType(){
        if(this == MAIN) {
            return ExampleVRApp.leftHanded ? ControllerType.LEFT : ControllerType.RIGHT;
        } else{
            return ExampleVRApp.leftHanded ? ControllerType.RIGHT : ControllerType.LEFT;
        }
    }

    public @NotNull ExampleHandEnum reversed(){
        if(this == OFFHAND) return MAIN;
        else return OFFHAND;
    }

    public static @NotNull ExampleHandEnum fromInt(int id){
        if(id == 0) return MAIN;
        return OFFHAND;
    }
    public static @NotNull ExampleHandEnum fromType(ControllerType type){
        if(ExampleVRApp.leftHanded){
            return type == ControllerType.LEFT ? MAIN : OFFHAND;
        }else{
            return type == ControllerType.LEFT ? OFFHAND : MAIN;
        }
    }
}
