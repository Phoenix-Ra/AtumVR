package me.phoenixra.atumvr.core.input.action;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.action.VRAction;


import me.phoenixra.atumvr.core.enums.XRInputActionType;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.actionset.OpenXRActionSet;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;
import org.lwjgl.openxr.XrActionSet;

import java.util.HashMap;

public abstract class OpenXRAction implements VRAction<XrAction, XrActionSet> {

    @Getter
    protected XrAction handle;

    @Getter
    protected final OpenXRActionSet actionSet;

    @Getter
    protected final String name;
    @Getter
    protected final String localizedName;

    protected final XRInputActionType actionType;

    public OpenXRAction(OpenXRActionSet actionSet,
                        String name,
                        String localizedName,
                        XRInputActionType actionType) {
        this.actionSet = actionSet;
        this.name = name;
        this.localizedName = localizedName;
        this.actionType = actionType;
    }


    public abstract HashMap<XRInteractionProfile, String> getProfiles();


    @Override
    public void destroy() {
        XR10.xrDestroyAction(handle);
    }


}
