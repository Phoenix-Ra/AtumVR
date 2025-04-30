package me.phoenixra.atumvr.api.input;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.exceptions.VRInputException;
import me.phoenixra.atumvr.api.input.data.VRInputActionData;
import me.phoenixra.atumvr.api.input.data.VRInputActionSetData;
import me.phoenixra.atumvr.api.utils.VRUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openvr.VRActiveActionSet;
import org.lwjgl.openvr.VRInput;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.openvr.VRInput.VRInput_GetActionHandle;
import static org.lwjgl.openvr.VRInput.VRInput_SetActionManifestPath;

public abstract class VRInputHandlerDefault implements VRInputHandler {
    @Getter
    private VRProvider vrProvider;

    @Getter
    private File actionManifest;

    public VRInputHandlerDefault(VRProvider vrProvider, @Nullable File actionManifest) {
        this.vrProvider = vrProvider;
        this.actionManifest = actionManifest;
    }

    protected abstract void onInit();

    protected abstract void onUpdateInputData();

    @Override
    public void init() {
        loadActionManifest();
        //find and save action handles for input
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer result = stack.callocLong(1);
            for (VRInputActionData vrinputaction : getInputActionsData()) {
                int error = VRInput_GetActionHandle(vrinputaction.getName(), result);

                if (error != 0) {
                    throw new RuntimeException("Error getting action handle for '" + vrinputaction.getName() + "': " + VRUtils.getInputErrorMessage(error));
                }

                vrinputaction.setActionHandle(result.get(0));
            }
        }
        onInit();

    }

    @Override
    public void updateInputData() {
        //find and save action handles for input
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer result = stack.callocLong(1);
            for (VRInputActionData vrinputaction : getInputActionsData()) {
                int error = VRInput_GetActionHandle(vrinputaction.getName(), result);

                if (error != 0) {
                    throw new RuntimeException("Error getting action handle for '" + vrinputaction.getName() + "': " + VRUtils.getInputErrorMessage(error));
                }

                vrinputaction.setActionHandle(result.get(0));
            }
        }
        //Update input states
        List<VRInputActionSetData> actionSets = getActiveActionSets();
        if (!actionSets.isEmpty()) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                VRActiveActionSet.Buffer buffer = VRActiveActionSet.malloc(actionSets.size(), stack);
                for(int i = 0; i< actionSets.size(); i++){
                    buffer.get(i).set(
                            OpenVRInputHelper.getInputActionSetHandle(actionSets.get(i).getName(), stack),
                            0L, 0, 0
                    );
                }

                int k = VRInput.VRInput_UpdateActionState(buffer, VRActiveActionSet.SIZEOF);

                if (k != 0) {
                    throw new RuntimeException("Error updating action state: code " + VRUtils.getInputErrorMessage(k));
                }
            }
        }


        onUpdateInputData();
    }


    protected void loadActionManifest() {
        if (actionManifest == null) {
            return;
        }
        getVrProvider().getAttachedApp().logInfo("[LOADING] Action manifest");
        int error = VRInput_SetActionManifestPath(
                actionManifest.getAbsolutePath()
        );
        if (error != 0) {
            throw new VRInputException(
                    "Error while loading action manifest", error
            );
        }
        getVrProvider().getAttachedApp().logInfo("[SUCCESS] Action manifest");
    }
}
