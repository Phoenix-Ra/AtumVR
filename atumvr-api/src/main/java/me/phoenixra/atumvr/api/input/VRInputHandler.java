package me.phoenixra.atumvr.api.input;

import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.input.data.*;
import me.phoenixra.atumvr.api.input.devices.VRDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;


public interface VRInputHandler {

    void init();
    void update();

    @Nullable
    VRDevice getHMD();
    @Nullable
    VRDevice getRightHand();
    @Nullable
    VRDevice getLeftHand();

    @NotNull
    List<VRInputActionSetData> getActiveActionSets();

    @NotNull
    List<VRInputActionData> getInputActionsData();


    @NotNull
    VRProvider getVrProvider();


}
