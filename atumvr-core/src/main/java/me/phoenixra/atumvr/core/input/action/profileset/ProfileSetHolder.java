package me.phoenixra.atumvr.core.input.action.profileset;

import lombok.Getter;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ProfileSetHolder {
    @Getter
    private final SharedActionSet sharedSet;

    private XRProfileSet lastActive;

    private final Map<XRInteractionProfile, XRProfileSet> profileSetMap = new HashMap<>();

    public ProfileSetHolder(XRProvider provider){
        this(new SharedActionSet(provider),
                        XRInteractionProfile.getSupportedProfileSets(provider)
        );
    }

    public ProfileSetHolder(@NotNull SharedActionSet sharedSet,
                            @NotNull List<XRProfileSet> profileSets){
        this.sharedSet = sharedSet;
        for(var entry : profileSets){
            profileSetMap.put(entry.getType(), entry);
        }
    }


    /**
     *
     * @return null if unrecognizable profile or controllers weren't connected yet
     */
    public @Nullable XRProfileSet getActiveProfileSet(){
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

    public @Nullable XRProfileSet getProfileSet(XRInteractionProfile type){
        return profileSetMap.get(type);
    }

    public Collection<XRProfileSet> getProfileSets(){
        return profileSetMap.values();
    }
    public List<? extends XRActionSet> getAllSets(){
        var list = new ArrayList<XRActionSet>();
        list.add(sharedSet);
        list.addAll(profileSetMap.values());
        return list;
    }

}
