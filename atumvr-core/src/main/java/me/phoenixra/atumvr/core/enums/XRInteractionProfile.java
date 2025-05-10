package me.phoenixra.atumvr.core.enums;

import lombok.Getter;

import java.util.HashMap;

public enum XRInteractionProfile {
    MIXED_REALITY("/interaction_profiles/hp/mixed_reality_controller"),
    VIVE_COSMOS("/interaction_profiles/htc/vive_cosmos_controller"),
    WINDOWS_MOTION_CONTROLLER("/interaction_profiles/microsoft/motion_controller"),
    VALVE_INDEX("/interaction_profiles/valve/index_controller"),
    OCULUS("/interaction_profiles/oculus/touch_controller");
    @Getter
    private final String path;
    XRInteractionProfile(String path){
        this.path = path;
    }

    private static final HashMap<String, XRInteractionProfile> values = new HashMap<>();


    public static XRInteractionProfile fromPath(String path){
        if(values.isEmpty()){
            for(XRInteractionProfile entry : values()){
                values.put(entry.path,entry);
            }
        }
        return values.get(path);
    }
}
