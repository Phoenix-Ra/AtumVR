package me.phoenixra.atumvr.api.input;

import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.input.data.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;


public interface VRInputHandler {

    void init();
    void updateInputData();

    //queried every tick
    @NotNull
    List<VRInputActionSetData> getActiveActionSets();

    @NotNull
    List<VRInputActionData> getInputActionsData();

    @NotNull File getActionManifest();

    @NotNull
    VRProvider getVrProvider();






}
