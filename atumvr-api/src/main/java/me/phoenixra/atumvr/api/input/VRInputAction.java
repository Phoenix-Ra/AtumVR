package me.phoenixra.atumvr.api.input;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString @EqualsAndHashCode
public class VRInputAction {
    @Getter
    private final String name;
    @Getter
    private final String requirement;
    @Getter
    private final String type;
    @Getter
    private final VRInputActionSet actionSet;


    @Getter @Setter
    private long actionHandle;


    public VRInputAction(@NotNull String name,
                         @NotNull String requirement,
                         @NotNull String type,
                         @NotNull VRInputActionSet actionSet){
        this.name = name;
        this.requirement = requirement;
        this.type = type;
        this.actionSet = actionSet;
    }
}
