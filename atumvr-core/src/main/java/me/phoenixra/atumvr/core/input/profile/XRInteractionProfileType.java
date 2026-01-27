package me.phoenixra.atumvr.core.input.profile;

import lombok.Getter;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.profile.types.*;
import me.phoenixra.atumvr.core.session.XRInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The enum, that represents supported interaction profiles from OpenXR
 */
public enum XRInteractionProfileType {

    VALVE_INDEX("/interaction_profiles/valve/index_controller"),

    OCULUS_TOUCH("/interaction_profiles/oculus/touch_controller"),

    VIVE("/interaction_profiles/htc/vive_controller"),

    VIVE_COSMOS("/interaction_profiles/htc/vive_cosmos_controller"),

    HP_MIXED_REALITY("/interaction_profiles/hp/mixed_reality_controller"),

    WINDOWS_MOTION("/interaction_profiles/microsoft/motion_controller");

    @Getter
    private final String pathName;

    XRInteractionProfileType(String pathName){
        this.pathName = pathName;
    }



    private static final HashMap<String, XRInteractionProfileType> values = new HashMap<>();


    /**
     * Get enum from interaction profile path
     */
    public static @Nullable XRInteractionProfileType fromPath(@NotNull String path){
        if(values.isEmpty()){
            for(XRInteractionProfileType entry : values()){
                values.put(entry.pathName,entry);
            }
        }
        return values.get(path);
    }

    /**
     * Get supported interaction profile types by the user's hardware
     */
    public static @NotNull List<XRInteractionProfileType> getSupported(@NotNull XRProvider vrProvider){
        List<XRInteractionProfileType> list = new ArrayList<>();
        XRInstance instance = vrProvider.getSession().getInstance();

        list.add(OCULUS_TOUCH);
        list.add(XRInteractionProfileType.VALVE_INDEX);
        list.add(XRInteractionProfileType.WINDOWS_MOTION);
        list.add(XRInteractionProfileType.VIVE);

        if(instance.getHandle().getCapabilities().XR_EXT_hp_mixed_reality_controller){
            list.add(XRInteractionProfileType.HP_MIXED_REALITY);
        }
        if(instance.getHandle().getCapabilities().XR_HTC_vive_cosmos_controller_interaction){
            list.add(XRInteractionProfileType.VIVE_COSMOS);
        }

        return list;
    }

    /**
     * Get supported interaction profiles by the user's hardware
     */
    public static @NotNull List<XRInteractionProfile> getSupportedProfiles(@NotNull XRProvider vrProvider){
        var out = new ArrayList<XRInteractionProfile>();
        var supported = getSupported(vrProvider);
        if(supported.contains(VALVE_INDEX)) out.add(new ValveIndexProfile(vrProvider));

        if(supported.contains(OCULUS_TOUCH)) out.add(new OculusTouchProfile(vrProvider));

        if(supported.contains(WINDOWS_MOTION)) out.add(new WindowsMotionProfile(vrProvider));

        if(supported.contains(HP_MIXED_REALITY)) out.add(new HpMixedRealityProfile(vrProvider));

        if(supported.contains(VIVE)) out.add(new ViveProfile(vrProvider));

        if(supported.contains(VIVE_COSMOS)) out.add(new ViveCosmosProfile(vrProvider));

        return out;
    }
}
