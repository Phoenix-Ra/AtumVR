package me.phoenixra.atumvr.api.input.profile;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRActionIdentifier;
import me.phoenixra.atumvr.api.input.profile.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

/**
 * The enum, that represents supported interaction profiles
 */
public enum VRInteractionProfileType {

    VALVE_INDEX("/interaction_profiles/valve/index_controller"),

    OCULUS_TOUCH("/interaction_profiles/oculus/touch_controller"),

    VIVE("/interaction_profiles/htc/vive_controller"),

    VIVE_COSMOS("/interaction_profiles/htc/vive_cosmos_controller"),

    HP_MIXED_REALITY("/interaction_profiles/hp/mixed_reality_controller"),

    WINDOWS_MOTION("/interaction_profiles/microsoft/motion_controller");

    @Getter
    private final String xrPath;

    VRInteractionProfileType(String xrPath){
        this.xrPath = xrPath;
    }



    private static final HashMap<String, VRInteractionProfileType> values = new HashMap<>();


    /**
     * Get enum from interaction profile path
     */
    public static @Nullable VRInteractionProfileType fromXRPath(@NotNull String path){
        if(values.isEmpty()){
            for(VRInteractionProfileType entry : values()){
                values.put(entry.xrPath,entry);
            }
        }
        return values.get(path);
    }



    /**
     * Get action ids of specified profile type
     *
     * @param profileType the interaction profile type
     * @return the list of action ids
     */
    public static List<VRActionIdentifier> getActionIdsOf(@NotNull VRInteractionProfileType profileType){
        return switch (profileType){
            case HP_MIXED_REALITY -> HpMixedRealityProfile.ALL_ACTION_IDS;

            case OCULUS_TOUCH -> OculusTouchProfile.ALL_ACTION_IDS;

            case VALVE_INDEX -> ValveIndexProfile.ALL_ACTION_IDS;

            case VIVE_COSMOS -> ViveCosmosProfile.ALL_ACTION_IDS;

            case VIVE -> ViveProfile.ALL_ACTION_IDS;

            case WINDOWS_MOTION -> WindowsMotionProfile.ALL_ACTION_IDS;
        };
    }

    /**
     * Get action button ids of specified profile type
     *
     * @param profileType the interaction profile type
     * @return the list of action ids
     */
    public static List<VRActionIdentifier> getButtonIdsOf(@NotNull VRInteractionProfileType profileType){
        return switch (profileType){
            case HP_MIXED_REALITY -> HpMixedRealityProfile.BUTTON_IDS;

            case OCULUS_TOUCH -> OculusTouchProfile.BUTTON_IDS;

            case VALVE_INDEX -> ValveIndexProfile.BUTTON_IDS;

            case VIVE_COSMOS -> ViveCosmosProfile.BUTTON_IDS;

            case VIVE -> ViveProfile.BUTTON_IDS;

            case WINDOWS_MOTION -> WindowsMotionProfile.BUTTON_IDS;
        };
    }

    /**
     * Get action vec2 ids of specified profile type
     *
     * @param profileType the interaction profile type
     * @return the list of action ids
     */
    public static List<VRActionIdentifier> getVec2IdsOf(@NotNull VRInteractionProfileType profileType){
        return switch (profileType){
            case HP_MIXED_REALITY -> HpMixedRealityProfile.VEC2_IDS;

            case OCULUS_TOUCH -> OculusTouchProfile.VEC2_IDS;

            case VALVE_INDEX -> ValveIndexProfile.VEC2_IDS;

            case VIVE_COSMOS -> ViveCosmosProfile.VEC2_IDS;

            case VIVE -> ViveProfile.VEC2_IDS;

            case WINDOWS_MOTION -> WindowsMotionProfile.VEC2_IDS;
        };
    }
}
