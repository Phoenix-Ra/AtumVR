package me.phoenixra.atumvr.api.input.action;

import java.util.Collection;

public interface VRActionSet<HANDLE> {


    void init();

    void update();

    void destroy();


    HANDLE getHandle();


    Collection<? extends VRAction<?,?>> getActions();


    String getName();

    String getLocalizedName();

    String getPriority();
}
