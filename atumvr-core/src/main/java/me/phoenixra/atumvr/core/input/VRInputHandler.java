package me.phoenixra.atumvr.core.input;

import lombok.Getter;
import me.phoenixra.atumconfig.api.tuples.PairRecord;
import me.phoenixra.atumvr.core.exceptions.VRException;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.input.action.data.VRActionData;
import me.phoenixra.atumvr.core.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.VRAction;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.device.VRDevice;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;
import java.util.*;

import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Abstract base class for VR input
 */
public abstract class VRInputHandler {

    @Getter
    private final VRProvider vrProvider;


    private final HashMap<String, Long> paths = new HashMap<>();

    private final Map<String, VRActionSet> actionSets = new LinkedHashMap<>();
    private final Map<String, VRDevice> devices = new LinkedHashMap<>();


    public VRInputHandler(@NotNull VRProvider vrProvider){
        this.vrProvider = vrProvider;
    }

    // -------- SETTING UP --------

    /**
     * Generate action sets
     *
     * @param stack the memory stack to use
     * @return the list containing action sets
     */
    protected abstract @NotNull List<? extends VRActionSet> generateActionSets(@NotNull MemoryStack stack);

    /**
     * Generate VR devices
     *
     * @param stack the memory stack to use
     * @return the list containing VR devices
     */
    protected abstract @NotNull List<? extends VRDevice> generateDevices(@NotNull MemoryStack stack);


    // -------- LIFECYCLE --------

    /**
     * Initialize VR input
     */
    public void init() {

        XrSession xrSession = vrProvider.getSession().getHandle();


        try (MemoryStack stack = MemoryStack.stackPush()) {


            //LOAD ACTION SETS
            actionSets.clear();
            var loadedActionSets = generateActionSets(stack);
            loadedActionSets.forEach(VRActionSet::init);

            long[] actionSetsArray = new long[loadedActionSets.size()];
            int i = 0;
            for(VRActionSet entry : loadedActionSets){
                actionSets.put(entry.getName(), entry);
                actionSetsArray[i] = entry.getHandle().address();
                i++;
            }

            //suggest defaults before attaching action sets
            suggestDefaultBindings(stack);

            //attach action sets
            XrSessionActionSetsAttachInfo attach_info = XrSessionActionSetsAttachInfo.calloc(stack).set(
                    XR10.XR_TYPE_SESSION_ACTION_SETS_ATTACH_INFO,
                    NULL,
                    stackPointers(actionSetsArray)
            );
            vrProvider.checkXRError(
                    XR10.xrAttachSessionActionSets(xrSession, attach_info),
                    "xrAttachSessionActionSets"
            );


            //LOAD DEVICES
            devices.clear();
            for(VRDevice entry : generateDevices(stack)){
                devices.put(entry.getId(), entry);
            }

        }
    }


    /**
     * Update input data from VR session
     *
     */
    public void update() {
        XrInstance instance = vrProvider.getSession().getInstance().getHandle();
        XrSession session = vrProvider.getSession().getHandle();

        // Sync actions
        try (MemoryStack stack = MemoryStack.stackPush()) {

            XrActiveActionSet.Buffer toUpdate = XrActiveActionSet
                    .calloc(actionSets.size(), stack);
            int i = 0;
            for(VRActionSet actionSet : actionSets.values()) {
                toUpdate.get(i).set(actionSet.getHandle(), XR_NULL_PATH);
                i++;
            }

            XrActionsSyncInfo syncInfo = XrActionsSyncInfo
                    .calloc(stack)
                    .type(XR_TYPE_ACTIONS_SYNC_INFO)
                    .activeActionSets(toUpdate);
            vrProvider.checkXRError(
                    xrSyncActions(session, syncInfo),
                    "xrSyncActions"
            );


        }

        for (VRActionSet entry : actionSets.values()) {
            entry.update();
        }
        for (VRDevice entry : devices.values()) {
            entry.update();
        }

    }

    /**
     * On action data changed
     * <p>
     *     Override if you need a simple way to listen for actions data change<br>
     *     (e.g. button pressed/released, pose changed etc.)
     * </p>
     *
     * @param actionData the action data that was changed
     */
    public void onActionChanged(@NotNull VRActionData actionData){
        //your implementation
    }

    private void suggestDefaultBindings(@NotNull MemoryStack stack) {

        XrInstance xrInstance = vrProvider.getSession().getInstance().getHandle();
        List<VRInteractionProfileType> supportedProfiles = VRInteractionProfileType.getSupported(vrProvider);

        for (VRInteractionProfileType profile : supportedProfiles) {
            List<PairRecord<VRAction, String>> bindingsSet = new ArrayList<>();
            for(VRActionSet actionSet : actionSets.values()){
                var binds = actionSet.getDefaultBindings(profile);
                if(binds == null || binds.isEmpty()) continue;
                bindingsSet.addAll(binds);
            }
            if(bindingsSet.isEmpty()) continue;

            var bindings = XrActionSuggestedBinding.calloc(bindingsSet.size(), stack);

            for (int i = 0; i < bindingsSet.size(); i++) {
                var binding = bindingsSet.get(i);
                bindings.get(i).set(
                        binding.first().getHandle(),
                        convertStringToXrPath(binding.second())
                );
            }

            var suggested_binds = XrInteractionProfileSuggestedBinding.calloc(stack)
                    .set(
                            XR10.XR_TYPE_INTERACTION_PROFILE_SUGGESTED_BINDING,
                            NULL,
                            convertStringToXrPath(profile.getPathName()),
                            bindings
                    );

            vrProvider.checkXRError(
                    XR10.xrSuggestInteractionProfileBindings(
                            xrInstance,
                            suggested_binds
                    ),
                    "xrSuggestInteractionProfileBindings"
            );
        }
    };

    // -------- API --------
    /**
     * Returns all registered action sets.
     *
     * @return collection of action sets
     */
    public Collection<? extends VRActionSet> getActionSets(){
        return actionSets.values();
    }

    /**
     * Returns all registered VR devices.
     *
     * @return collection of devices
     */
    public Collection<? extends VRDevice> getDevices() {
        return devices.values();
    }

    /**
     * Gets a device by its ID.
     *
     * @param id the device ID
     * @return the device, or null if not found
     */
    public VRDevice getDevice(String id) {
        return devices.get(id);
    }

    /**
     * Gets a device by ID with type casting.
     *
     * @param id    the device ID
     * @param clazz the expected device class
     * @param <T>   the device type
     * @return the device cast to the specified type
     */
    public  <T extends VRDevice> T getDevice(String id, Class<T> clazz){
        return (T) getDevice(id);
    }

    /**
     * Registers a VR device.
     *
     * @param device the device to register
     */
    public void registerDevice(@NotNull VRDevice device) {
        devices.put(device.getId(), device);
    }


    /**
     * Converts an OpenXR path string to a path handle
     *
     * @param pathString the path string (e.g., "/user/hand/left")
     * @return the OpenXR path handle
     * @throws VRException if the path format is invalid
     */
    public long convertStringToXrPath(@NotNull String pathString) {
        return paths.computeIfAbsent(pathString, s -> {
            try (MemoryStack ignored = stackPush()) {
                LongBuffer buf = stackCallocLong(1);
                int xrResult = XR10.xrStringToPath(
                        vrProvider.getSession().getInstance().getHandle(),
                        pathString, buf
                );
                if (xrResult == XR10.XR_ERROR_PATH_FORMAT_INVALID) {
                    throw new VRException("Invalid path:\"" + pathString + "\"");
                } else {
                    vrProvider.checkXRError(xrResult, "xrStringToPath");
                }
                return buf.get();
            }
        });
    }

    // -------- DESTROY --------

    /**
     * Destroy VR input and release all resources attached
     */
    public void destroy() {
        actionSets.values().forEach(VRActionSet::destroy);
        actionSets.clear();
        devices.clear();
    }
}
