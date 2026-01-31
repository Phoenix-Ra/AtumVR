package me.phoenixra.atumvr.core.input.profile;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfile;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.XRInputHandler;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.action.types.multi.PoseMultiAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class that adds support for {@link VRInteractionProfile interaction profiles}.
 *
 * <p>
 *     Use it in {@link XRInputHandler} while generating action sets. <br>
 *     Create XRProfileManager instance there and call {@link #getAllActionSets()} <br>
 *     for generated actions sets return result.
 * </p>
 * <p>
 *     This class is optional, you can ignore it
 * </p>
 */
public class XRProfileManager {
    @Getter
    private final CommonActionSet commonSet;

    private XRInteractionProfile lastActive;

    private final Map<VRInteractionProfileType, XRInteractionProfile> profileSetMap = new HashMap<>();

    public XRProfileManager(@NotNull XRProvider vrProvider){
        this(
                new CommonActionSet(vrProvider),
                vrProvider.getInputHandler().getSupportedProfiles()
        );
    }

    public XRProfileManager(@NotNull CommonActionSet commonSet,
                            @NotNull List<XRInteractionProfile> profileSets){
        this.commonSet = commonSet;
        for(var entry : profileSets){
            profileSetMap.put(entry.getType(), entry);
        }
    }


    /**
     * Get active profile
     *
     * @return null if unrecognizable profile or controllers weren't connected yet
     */
    public @Nullable XRInteractionProfile getActiveProfile(){
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
    public @Nullable XRInteractionProfile getProfile(@NotNull VRInteractionProfileType type){
        return profileSetMap.get(type);
    }

    /**
     * Get all interaction profiles available
     *
     * @return the list of profiles
     */
    public Collection<XRInteractionProfile> getAllProfiles(){
        return profileSetMap.values();
    }

    /**
     * Get all action sets
     *
     * @return the list of action sets
     */
    public List<? extends XRActionSet> getAllActionSets(){
        var list = new ArrayList<XRActionSet>();
        list.add(commonSet);
        list.addAll(profileSetMap.values());
        return list;
    }

    /**
     * Get Haptic Pulse action
     *
     * @return the action
     */
    public HapticPulseAction getHapticPulse(){
        return commonSet.getHapticPulse();
    }

    /**
     * Get hand pose aim action
     *
     * @return the action
     */
    public PoseMultiAction getHandPoseAim(){
        return commonSet.getHandPoseAim();
    }

    /**
     * Get hand pose grip action
     *
     * @return the action
     */
    public PoseMultiAction getHandPoseGrip(){
        return commonSet.getHandPoseGrip();
    }

}
