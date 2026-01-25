package me.phoenixra.atumvr.core.input.profile;

import lombok.Getter;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.input.VRInputHandler;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class that adds support for {@link VRInteractionProfile interaction profiles}.
 *
 * <p>
 *     Use it in {@link VRInputHandler} while generating action sets. <br>
 *     Create class instance there and call {@link #getAllActionSets()} <br>
 *     for generated actions sets return result.
 * </p>
 * <p>
 *     This class is optional, you can ignore it
 * </p>
 */
public class VRProfileManager {
    @Getter
    private final CommonActionSet sharedSet;

    private VRInteractionProfile lastActive;

    private final Map<VRInteractionProfileType, VRInteractionProfile> profileSetMap = new HashMap<>();

    public VRProfileManager(@NotNull VRProvider vrProvider){
        this(
                new CommonActionSet(vrProvider),
                VRInteractionProfileType.getSupportedProfiles(vrProvider)
        );
    }

    public VRProfileManager(@NotNull CommonActionSet sharedSet,
                            @NotNull List<VRInteractionProfile> profileSets){
        this.sharedSet = sharedSet;
        for(var entry : profileSets){
            profileSetMap.put(entry.getType(), entry);
        }
    }


    /**
     * Get active profile
     *
     * @return null if unrecognizable profile or controllers weren't connected yet
     */
    public @Nullable VRInteractionProfile getActiveProfile(){
        if(lastActive != null && lastActive.isProfileActive()){
            return lastActive;
        }
        for(var entry : profileSetMap.values()){
            if(entry.isProfileActive()) {
                lastActive = entry;
                return entry;
            }
        }
        return lastActive;
    }

    /**
     * Get profile from specified type or null if not found
     *
     * @param type the type of profile
     * @return the interaction profile or null
     */
    public @Nullable VRInteractionProfile getProfile(@NotNull VRInteractionProfileType type){
        return profileSetMap.get(type);
    }

    /**
     * Get all interaction profiles available
     *
     * @return the list of profiles
     */
    public Collection<VRInteractionProfile> getAllProfiles(){
        return profileSetMap.values();
    }

    /**
     * Get all action sets
     *
     * @return the list of action sets
     */
    public List<? extends VRActionSet> getAllActionSets(){
        var list = new ArrayList<VRActionSet>();
        list.add(sharedSet);
        list.addAll(profileSetMap.values());
        return list;
    }

    /**
     * Get Haptic Pulse action
     *
     * @return the action
     */
    public HapticPulseAction getHapticPulse(){
        return sharedSet.getHapticPulse();
    }

    /**
     * Get hand pose aim action
     *
     * @return the action
     */
    public PoseMultiAction getHandPoseAim(){
        return sharedSet.getHandPoseAim();
    }

    /**
     * Get hand pose grip action
     *
     * @return the action
     */
    public PoseMultiAction getHandPoseGrip(){
        return sharedSet.getHandPoseGrip();
    }

}
