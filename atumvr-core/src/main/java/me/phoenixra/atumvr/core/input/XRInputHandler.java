package me.phoenixra.atumvr.core.input;

import lombok.Getter;
import me.phoenixra.atumconfig.api.tuples.PairRecord;
import me.phoenixra.atumvr.api.exceptions.AtumVRException;
import me.phoenixra.atumvr.api.input.AtumVRInputHandler;
import me.phoenixra.atumvr.api.input.device.AtumVRDevice;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.api.input.action.data.VRActionData;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.action.types.HapticPulseAction;
import me.phoenixra.atumvr.core.input.device.XRDevice;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.profile.types.*;
import me.phoenixra.atumvr.core.session.XRInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;
import java.util.*;

import static me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType.*;
import static me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType.VIVE_COSMOS;
import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

/**
 * Abstract base class for XR input
 */
public abstract class XRInputHandler implements AtumVRInputHandler {

    @Getter
    private final XRProvider vrProvider;


    private final HashMap<String, Long> paths = new HashMap<>();

    private final Map<String, XRActionSet> actionSets = new LinkedHashMap<>();
    private final Map<String, XRDevice> devices = new LinkedHashMap<>();

    // Last interaction profile logged per user path, "" means none. Suppresses repeat events
    private final Map<String, String> lastLoggedInteractionProfile = new HashMap<>();


    public XRInputHandler(@NotNull XRProvider vrProvider){
        this.vrProvider = vrProvider;
    }

    // -------- SETTING UP --------

    /**
     * Generate action sets
     *
     * @param stack the memory stack to use
     * @return the list containing action sets
     */
    protected abstract @NotNull List<? extends XRActionSet> generateActionSets(@NotNull MemoryStack stack);

    /**
     * Generate VR devices
     *
     * @param stack the memory stack to use
     * @return the list containing VR devices
     */
    protected abstract @NotNull List<? extends XRDevice> generateDevices(@NotNull MemoryStack stack);


    // -------- LIFECYCLE --------

    @Override
    public void init() {

        XrSession xrSession = vrProvider.getSession().getHandle();


        try (MemoryStack stack = MemoryStack.stackPush()) {


            //LOAD ACTION SETS
            actionSets.clear();
            var loadedActionSets = generateActionSets(stack);
            loadedActionSets.forEach(XRActionSet::init);

            long[] actionSetsArray = new long[loadedActionSets.size()];
            int i = 0;
            for(XRActionSet entry : loadedActionSets){
                actionSets.put(entry.getName(), entry);
                actionSetsArray[i] = entry.getHandle().address();
                i++;
            }

            //suggest defaults before attaching action sets
            suggestDefaultBindings();

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
            for(XRDevice entry : generateDevices(stack)){
                devices.put(entry.getId(), entry);
            }

        }
    }


    @Override
    public void update() {
        XrInstance instance = vrProvider.getSession().getInstance().getHandle();
        XrSession session = vrProvider.getSession().getHandle();

        // Sync actions
        try (MemoryStack stack = MemoryStack.stackPush()) {

            XrActiveActionSet.Buffer toUpdate = XrActiveActionSet
                    .calloc(actionSets.size(), stack);
            int i = 0;
            for(XRActionSet actionSet : actionSets.values()) {
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

        for (XRActionSet entry : actionSets.values()) {
            entry.update();
        }
        for (XRDevice entry : devices.values()) {
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

    private void suggestDefaultBindings() {

        XrInstance xrInstance = vrProvider.getSession().getInstance().getHandle();
        List<VRInteractionProfileType> supportedProfiles = getSupportedProfileTypes();

        for (VRInteractionProfileType profileType : supportedProfiles) {
            List<PairRecord<XRAction, String>> bindingsSet = new ArrayList<>();
            for(XRActionSet actionSet : actionSets.values()){
                var binds = actionSet.getDefaultBindings(profileType);
                if(binds == null || binds.isEmpty()) continue;
                bindingsSet.addAll(binds);
            }
            if(bindingsSet.isEmpty()) continue;

            // Suggest whole bindings set
            int result = trySuggestBindings(xrInstance, profileType.getXrPath(), bindingsSet);
            if (result == XR10.XR_SUCCESS) {
                continue;
            }
            if (result != XR10.XR_ERROR_PATH_UNSUPPORTED) {
                vrProvider.checkXRError(result, "xrSuggestInteractionProfileBindings", profileType.getXrPath());
                continue;
            }

            //Fallback
            List<PairRecord<XRAction, String>> supported = new ArrayList<>();
            for (PairRecord<XRAction, String> binding : bindingsSet) {
                if (trySuggestBindings(xrInstance, profileType.getXrPath(), List.of(binding)) == XR10.XR_SUCCESS) {
                    supported.add(binding);
                } else {
                    vrProvider.getLogger().logWarn(
                            "Dropping unsupported binding for " + profileType + ": " + binding.second()
                    );
                }
            }

            if (supported.isEmpty()) {
                vrProvider.getLogger().logWarn(
                        "No supported bindings for interaction profile " + profileType
                                + " (" + profileType.getXrPath() + ") - skipping"
                );
                continue;
            }

            // Re-suggest the supported subset
            vrProvider.checkXRError(
                    trySuggestBindings(xrInstance, profileType.getXrPath(), supported),
                    "xrSuggestInteractionProfileBindings", profileType.getXrPath()
            );
        }
    }


    private int trySuggestBindings(@NotNull XrInstance xrInstance,
                                   @NotNull String profilePath,
                                   @NotNull List<PairRecord<XRAction, String>> bindingsSet) {
        try (MemoryStack stack = stackPush()) {
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
                            convertStringToXrPath(profilePath),
                            bindings
                    );

            return XR10.xrSuggestInteractionProfileBindings(xrInstance, suggested_binds);
        }
    }

    // -------- API --------
    @Override
    public Collection<? extends XRActionSet> getActionSets(){
        return actionSets.values();
    }

    @Override
    public Collection<? extends XRDevice> getDevices() {
        return devices.values();
    }

    @Override
    public XRDevice getDevice(String id) {
        return devices.get(id);
    }

    @Override
    public  <T extends AtumVRDevice> T getDevice(String id, Class<T> clazz){
        return (T) getDevice(id);
    }

    @Override
    public void registerDevice(@NotNull AtumVRDevice device) {
        if(!(device instanceof XRDevice xrDevice)){
            throw new AtumVRException("Tried to register VRDevice that is not an instance of XRDevice! Id: "+device.getId());
        }
        devices.put(device.getId(), xrDevice);
    }


    /**
     * Converts an OpenXR path string to a path handle
     *
     * @param pathString the path string (e.g., "/user/hand/left")
     * @return the OpenXR path handle
     * @throws AtumVRException if the path format is invalid
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
                    throw new AtumVRException("Invalid path:\"" + pathString + "\"");
                } else {
                    vrProvider.checkXRError(xrResult, "xrStringToPath");
                }
                return buf.get();
            }
        });
    }

    /**
     * Converts an OpenXR path handle back to its path string
     *
     * @param path the OpenXR path handle
     * @return the path string, or null if the handle could not be resolved
     */
    public @Nullable String convertXrPathToString(long path) {
        XrInstance xrInstance = vrProvider.getSession().getInstance().getHandle();
        try (MemoryStack stack = stackPush()) {
            var sizeBuf = stack.callocInt(1);

            int xrResult = XR10.xrPathToString(xrInstance, path, sizeBuf, null);
            if (xrResult < 0) {
                vrProvider.checkXRError(false, xrResult, "xrPathToString", "size");
                return null;
            }

            int size = sizeBuf.get(0);
            var valueBuf = stack.calloc(size);
            xrResult = XR10.xrPathToString(xrInstance, path, sizeBuf, valueBuf);
            if (xrResult < 0) {
                vrProvider.checkXRError(false, xrResult, "xrPathToString", "value");
                return null;
            }

            return memUTF8(valueBuf, size - 1);
        }
    }


    /**
     * Get the interaction profile the runtime currently has bound to a top level user path.
     *
     * <p>
     *     Requires action sets to be attached, returns null before that
     * </p>
     *
     * @param userPath top level user path, e.g. {@link XRAction#LEFT_HAND_PATH}
     * @return the interaction profile path, or null if nothing is bound
     */
    public @Nullable String getCurrentInteractionProfilePath(@NotNull String userPath){
        try (MemoryStack stack = stackPush()) {
            var state = XrInteractionProfileState.calloc(stack)
                    .type(XR10.XR_TYPE_INTERACTION_PROFILE_STATE);

            int result = XR10.xrGetCurrentInteractionProfile(
                    vrProvider.getSession().getHandle(),
                    convertStringToXrPath(userPath),
                    state
            );
            if (result < 0) {
                vrProvider.checkXRError(false, result, "xrGetCurrentInteractionProfile", userPath);
                return null;
            }

            long profilePath = state.interactionProfile();
            return profilePath == XR10.XR_NULL_PATH
                    ? null
                    : convertXrPathToString(profilePath);
        }
    }

    /**
     * Log the interaction profile bound to each hand.
     *
     * <p>
     *     Reports profiles the runtime picked that this app suggested no bindings for -
     *     those leave every action inactive, which reads as dead controllers
     * </p>
     */
    public void logCurrentInteractionProfiles(){
        for (String userPath : List.of(XRAction.LEFT_HAND_PATH, XRAction.RIGHT_HAND_PATH)) {
            String profilePath = getCurrentInteractionProfilePath(userPath);
            String key = profilePath == null ? "" : profilePath;

            // The runtime can fire this event repeatedly, only log actual changes
            if (key.equals(lastLoggedInteractionProfile.put(userPath, key))) {
                continue;
            }

            if (profilePath == null) {
                vrProvider.getLogger().logInfo(
                        "Interaction profile for " + userPath + ": none (controller off or not bound)"
                );
                continue;
            }

            var type = VRInteractionProfileType.fromXRPath(profilePath);
            if (type == null) {
                vrProvider.getLogger().logWarn(
                        "Interaction profile for " + userPath + ": " + profilePath
                                + " - UNSUPPORTED, no bindings were suggested for it,"
                                + " input from this controller will stay inactive"
                );
                continue;
            }

            vrProvider.getLogger().logInfo(
                    "Interaction profile for " + userPath + ": " + profilePath + " (" + type + ")"
            );
        }
    }

    @Override
    public @NotNull List<VRInteractionProfileType> getSupportedProfileTypes(){
        List<VRInteractionProfileType> list = new ArrayList<>();
        XRInstance instance = vrProvider.getSession().getInstance();

        list.add(OCULUS_TOUCH);
        list.add(VALVE_INDEX);
        list.add(WINDOWS_MOTION);
        list.add(VIVE);

        if(instance.getHandle().getCapabilities().XR_EXT_hp_mixed_reality_controller){
            list.add(HP_MIXED_REALITY);
        }
        if(instance.getHandle().getCapabilities().XR_HTC_vive_cosmos_controller_interaction){
            list.add(VIVE_COSMOS);
        }
        if(instance.getHandle().getCapabilities().XR_HTCX_vive_tracker_interaction){
            list.add(VIVE_TRACKER);
        }

        return list;
    }

    /**
     * Get supported interaction profiles by the user's hardware
     */
    public @NotNull List<XRInteractionProfile> getSupportedProfiles(){
        var out = new ArrayList<XRInteractionProfile>();
        var supported = getSupportedProfileTypes();
        if(supported.contains(VALVE_INDEX)) out.add(new ValveIndexXRProfile(vrProvider));

        if(supported.contains(OCULUS_TOUCH)) out.add(new OculusTouchXRProfile(vrProvider));

        if(supported.contains(WINDOWS_MOTION)) out.add(new WindowsMotionXRProfile(vrProvider));

        if(supported.contains(HP_MIXED_REALITY)) out.add(new HpMixedRealityXRProfile(vrProvider));

        if(supported.contains(VIVE)) out.add(new ViveXRProfile(vrProvider));

        if(supported.contains(VIVE_COSMOS)) out.add(new ViveCosmosXRProfile(vrProvider));

        return out;
    }

    // -------- DESTROY --------

    @Override
    public void destroy() {
        // Cancel any vibration that is still running on the controllers
        stopActiveHaptics();

        actionSets.values().forEach(XRActionSet::destroy);
        actionSets.clear();
        devices.clear();
        paths.clear();
        lastLoggedInteractionProfile.clear();
    }

    public void stopActiveHaptics() {
        if (vrProvider.getSession().getHandle() == null) {
            return;
        }
        for (XRActionSet actionSet : actionSets.values()) {
            for (XRAction action : actionSet.getActions()) {
                if (action instanceof HapticPulseAction haptic) {
                    try {
                        haptic.stop();
                    } catch (Throwable ignored) {

                    }
                }
            }
        }
    }
}

