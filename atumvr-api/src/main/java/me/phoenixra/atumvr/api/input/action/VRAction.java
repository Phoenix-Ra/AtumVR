package me.phoenixra.atumvr.api.input.action;

public interface VRAction<HANDLE, SET_HANDLE>  {

    void init(VRActionSet<SET_HANDLE> actionSet);

    void update();

    void destroy();


    HANDLE getHandle();

    VRActionSet<SET_HANDLE> getActionSet();


    String getName();

    String getLocalizedName();


}
