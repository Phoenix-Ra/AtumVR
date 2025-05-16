package me.phoenixra.atumvr.core.enums;

import lombok.Getter;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.init.OpenXRInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum XRInteractionProfile {
    MIXED_REALITY("/interaction_profiles/hp/mixed_reality_controller"),
    VIVE_COSMOS("/interaction_profiles/htc/vive_cosmos_controller"),
    WINDOWS_MOTION_CONTROLLER("/interaction_profiles/microsoft/motion_controller"),
    VALVE_INDEX("/interaction_profiles/valve/index_controller"),
    OCULUS("/interaction_profiles/oculus/touch_controller");
    @Getter
    private final String pathName;
    XRInteractionProfile(String pathName){
        this.pathName = pathName;
    }

    private static final HashMap<String, XRInteractionProfile> values = new HashMap<>();


    public static XRInteractionProfile fromPath(String path){
        if(values.isEmpty()){
            for(XRInteractionProfile entry : values()){
                values.put(entry.pathName,entry);
            }
        }
        return values.get(path);
    }

    public static List<XRInteractionProfile> getSupported(OpenXRProvider provider){
        List<XRInteractionProfile> list = new ArrayList<>();
        OpenXRInstance instance = provider.getState().getVrInstance();

        list.add(OCULUS);
        if(!instance.getRuntimeName().contains("Oculus")){
            list.add(XRInteractionProfile.VALVE_INDEX);
            list.add(XRInteractionProfile.WINDOWS_MOTION_CONTROLLER);
        }
        if(instance.getHandle().getCapabilities().XR_EXT_hp_mixed_reality_controller){
            list.add(XRInteractionProfile.MIXED_REALITY);
        }
        if(instance.getHandle().getCapabilities().XR_HTC_vive_cosmos_controller_interaction){
            list.add(XRInteractionProfile.VIVE_COSMOS);
        }

        return list;
    }
}
