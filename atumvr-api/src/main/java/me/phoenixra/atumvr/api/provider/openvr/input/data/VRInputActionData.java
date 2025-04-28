package me.phoenixra.atumvr.api.provider.openvr.input.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString @EqualsAndHashCode
public class VRInputActionData {
    @Getter
    private final String name;
    @Getter
    private final String requirement;
    @Getter
    private final String type;
    @Getter
    private final VRInputActionSetData actionSet;


    @Getter @Setter
    private long actionHandle;


    public VRInputActionData(@NotNull String name,
                             @NotNull String requirement,
                             @NotNull String type,
                             @NotNull VRInputActionSetData actionSet){
        this.name = name;
        this.requirement = requirement;
        this.type = type;
        this.actionSet = actionSet;
    }
}
