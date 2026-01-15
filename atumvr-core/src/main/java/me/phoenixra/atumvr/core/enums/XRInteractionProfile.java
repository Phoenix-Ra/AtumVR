package me.phoenixra.atumvr.core.enums;

import lombok.Getter;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.init.XRInstance;
import me.phoenixra.atumvr.core.input.action.profileset.XRProfileSet;
import me.phoenixra.atumvr.core.input.action.profileset.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum XRInteractionProfile {
    VALVE_INDEX("/interaction_profiles/valve/index_controller"),
    OCULUS_TOUCH("/interaction_profiles/oculus/touch_controller"),
    VIVE("/interaction_profiles/htc/vive_controller"),
    VIVE_COSMOS("/interaction_profiles/htc/vive_cosmos_controller"),
    HP_MIXED_REALITY("/interaction_profiles/hp/mixed_reality_controller"),
    WINDOWS_MOTION("/interaction_profiles/microsoft/motion_controller");
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

    public static List<XRInteractionProfile> getSupported(XRProvider provider){
        List<XRInteractionProfile> list = new ArrayList<>();
        XRInstance instance = provider.getState().getVrInstance();

        list.add(OCULUS_TOUCH);
        list.add(XRInteractionProfile.VALVE_INDEX);
        list.add(XRInteractionProfile.WINDOWS_MOTION);
        list.add(XRInteractionProfile.VIVE);

        if(instance.getHandle().getCapabilities().XR_EXT_hp_mixed_reality_controller){
            list.add(XRInteractionProfile.HP_MIXED_REALITY);
        }
        if(instance.getHandle().getCapabilities().XR_HTC_vive_cosmos_controller_interaction){
            list.add(XRInteractionProfile.VIVE_COSMOS);
        }

        return list;
    }

    public static List<XRProfileSet> getSupportedProfileSets(XRProvider provider){
        var out = new ArrayList<XRProfileSet>();
        var supported = getSupported(provider);
        if(supported.contains(VALVE_INDEX)) out.add(new ValveIndexSet(provider));

        if(supported.contains(OCULUS_TOUCH)) out.add(new OculusTouchSet(provider));

        if(supported.contains(WINDOWS_MOTION)) out.add(new WindowsMotionSet(provider));

        if(supported.contains(HP_MIXED_REALITY)) out.add(new HpMixedRealitySet(provider));

        if(supported.contains(VIVE)) out.add(new ViveSet(provider));

        if(supported.contains(VIVE_COSMOS)) out.add(new ViveCosmosSet(provider));

        return out;
    }
}
