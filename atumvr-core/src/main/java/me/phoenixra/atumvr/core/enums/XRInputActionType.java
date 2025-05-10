package me.phoenixra.atumvr.core.enums;

import lombok.Getter;
import org.lwjgl.openxr.XR10;

import java.util.HashMap;

public enum XRInputActionType {
    BOOLEAN(XR10.XR_ACTION_TYPE_BOOLEAN_INPUT),
    FLOAT(XR10.XR_ACTION_TYPE_FLOAT_INPUT),
    VECTOR2F(XR10.XR_ACTION_TYPE_VECTOR2F_INPUT),
    POSE(XR10.XR_ACTION_TYPE_POSE_INPUT);



    @Getter
    private final int id;

    private static final HashMap<Integer, XRInputActionType> values = new HashMap<>();


    XRInputActionType(int id){
        this.id = id;
    }

    public static XRInputActionType fromId(int id){
        if(values.isEmpty()){
            for(XRInputActionType event : values()){
                values.put(event.id,event);
            }
        }
        return values.get(id);
    }
}
