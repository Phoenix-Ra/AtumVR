package me.phoenixra.atumvr.api.scene.camera;

import lombok.Getter;

public enum EyeType {
    LEFT(0),
    RIGHT(1);

    @Getter
    private int id;
    EyeType(int id){
       this.id = id;
    }


}
