package me.phoenixra.atumvr.core.input;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumconfig.api.tuples.PairRecord;
import me.phoenixra.atumvr.api.exceptions.VRException;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.api.input.device.VRDevice;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.enums.XRInteractionProfile;
import me.phoenixra.atumvr.core.input.action.XRAction;
import me.phoenixra.atumvr.core.input.action.XRActionSet;
import me.phoenixra.atumvr.core.input.device.XRDevice;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;
import java.util.*;
import java.util.function.Consumer;

import static org.lwjgl.openxr.XR10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public abstract class XRInputHandler implements VRInputHandler {
    @Getter
    private final XRProvider vrProvider;


    private final HashMap<String, Long> paths = new HashMap<>();

    private final Map<String, XRActionSet> actionSets = new LinkedHashMap<>();
    private final Map<String, XRDevice> devices = new LinkedHashMap<>();

    @Getter @Setter
    private Consumer<String> actionListener;

    public XRInputHandler(XRProvider provider){
        this.vrProvider = provider;
    }

    protected abstract List<? extends XRActionSet> generateActionSets(MemoryStack stack);
    protected abstract List<? extends XRDevice> generateDevices(MemoryStack stack);


    @Override
    public void init() {

        XrSession xrSession = vrProvider.getState().getVrSession().getHandle();


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
        XrInstance instance = vrProvider.getState().getVrInstance().getHandle();
        XrSession session = vrProvider.getState().getVrSession().getHandle();

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
            entry.update(actionListener);
        }
        for (XRDevice entry : devices.values()) {
            entry.update();
        }

    }


    private void suggestDefaultBindings(MemoryStack stack) {

        XrInstance xrInstance = vrProvider.getState().getVrInstance().getHandle();
        List<XRInteractionProfile> supportedProfiles = XRInteractionProfile.getSupported(vrProvider);

        for (XRInteractionProfile profile : supportedProfiles) {
            List<PairRecord<XRAction, String>> bindingsSet = new ArrayList<>();
            for(XRActionSet actionSet : actionSets.values()){
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
                        getPath(binding.second())
                );
            }

            var suggested_binds = XrInteractionProfileSuggestedBinding.calloc(stack)
                    .set(
                            XR10.XR_TYPE_INTERACTION_PROFILE_SUGGESTED_BINDING,
                            NULL,
                            getPath(profile.getPathName()),
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
    public void registerDevice(VRDevice device) {
        if(!(device instanceof XRDevice xrDevice)){
            throw new VRException("Cannot register device that is not instanceof OpenXRDevice");
        }
        devices.put(device.getId(),xrDevice);
    }


    public long getPath(String pathString) {
        return paths.computeIfAbsent(pathString, s -> {
            try (MemoryStack ignored = stackPush()) {
                LongBuffer buf = stackCallocLong(1);
                int xrResult = XR10.xrStringToPath(
                        vrProvider.getState().getVrInstance().getHandle(),
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
    public void destroy() {
        actionSets.values().forEach(XRActionSet::destroy);
        actionSets.clear();
        devices.clear();
    }
}
