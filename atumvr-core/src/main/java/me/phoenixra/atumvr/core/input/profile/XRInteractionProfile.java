package me.phoenixra.atumvr.core.input.profile;

import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfile;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatButtonMultiAction;
import org.jetbrains.annotations.NotNull;

public abstract class XRInteractionProfile extends XRActionSet implements VRInteractionProfile {

    public XRInteractionProfile(@NotNull XRProvider vrProvider,
                                @NotNull String name,
                                @NotNull String localizedName,
                                int priority) {
        super(vrProvider, name, localizedName, priority);
    }


}
