package me.phoenixra.atumvr.core.input;

import lombok.Getter;
import me.phoenixra.atumconfig.api.tuples.PairRecord;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.input.device.VRDevice;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.api.input.action.data.VRActionData;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.device.XRDevice;
import me.phoenixra.atumvr.api.input.profile.VRInteractionProfile;
import me.phoenixra.atumvr.core.input.profile.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.profile.types.*;
import me.phoenixra.atumvr.core.session.XRInstance;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;
import java.util.*;

import static me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType.*;
import static me.phoenixra.atumvr.api.input.profile.VRInteractionProfileType.VIVE_COSMOS;
import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Abstract base class for XR input
 */
public abstract class XRInputHandler implements VRInputHandler {

    @Getter
    private final XRProvider vrProvider;


    private final HashMap<String, Long> paths = new HashMap<>();

    private final Map<String, XRActionSet> actionSets = new LinkedHashMap<>();
    private final Map<String, XRDevice> devices = new LinkedHashMap<>();


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

    private void suggestDefaultBindings(@NotNull MemoryStack stack) {

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
                            convertStringToXrPath(profileType.getXrPath()),
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
    public  <T extends VRDevice> T getDevice(String id, Class<T> clazz){
        return (T) getDevice(id);
    }

    @Override
    public void registerDevice(@NotNull VRDevice device) {
        if(!(device instanceof XRDevice xrDevice)){
            throw new VRException("Tried to register VRDevice that is not an instance of XRDevice! Id: "+device.getId());
        }
        devices.put(device.getId(), xrDevice);
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
        actionSets.values().forEach(XRActionSet::destroy);
        actionSets.clear();
        devices.clear();
    }
}
