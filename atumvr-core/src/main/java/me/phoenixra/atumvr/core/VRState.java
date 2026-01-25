package me.phoenixra.atumvr.core;

import lombok.Getter;
import me.phoenixra.atumvr.core.enums.XREvent;
import me.phoenixra.atumvr.core.enums.XRSessionState;
import me.phoenixra.atumvr.core.session.VRInstance;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;


import static org.lwjgl.system.MemoryUtil.NULL;


/**
 * This class is responsible for tracking the current state of the VR session.
 *
 * @author PhoenixRa
 */
public class VRState {

    @Getter
    private final VRProvider vrProvider;


    /**
     * If VR session is ready (initialized) on VR runtime side
     */
    protected boolean ready = false;

    /**
     * If VR session is initialized both on VR runtime and AtumVR side
     */
    @Getter
    protected boolean initialized = false;

    /**
     * If VR session is active
     * <p>
     *     When true, the session is active and can be rendered
     * </p>
     */
    @Getter
    protected boolean active = false;

    /**
     * If VR session is focused.
     * <p>
     *     When true, the VR session is active and user is interacting with your app,
     *     otherwise user is probably in VR runtime menu
     * </p>
     * <p>
     *     When its false, highly recommended to limit the resources usage, <br>
     *     (for example render only nearby objects and disable VR hands)
     * </p>
     */
    @Getter
    protected boolean focused = false;


    public VRState(@NotNull VRProvider vrProvider){
        this.vrProvider = vrProvider;

    }

    public void init() throws Throwable{

        while (!ready){
            vrProvider.getLogger().logInfo("Waiting for OpenXR session to start...");
            pollVREvents();
        }
        vrProvider.inputHandler.init();
        vrProvider.renderer.init();

        initialized = true;
    }



    protected void pollVREvents() {
        VRInstance vrInstance = vrProvider.getSession().getInstance();
        XrEventDataBuffer eventBuffer = vrInstance.getXrEventBuffer();
        while (true) {
            eventBuffer.clear();
            eventBuffer.type(XR10.XR_TYPE_EVENT_DATA_BUFFER);
            int error = XR10.xrPollEvent(vrInstance.getHandle(), eventBuffer);
            vrProvider.checkXRError(error, "xrPollEvent", "");
            if (error != XR10.XR_SUCCESS) {
                //no more events available
                break;
            }
            var event = XrEventDataBaseHeader.create(eventBuffer.address());
            var vrEvent = XREvent.fromId(event.type());

            if (vrEvent == XREvent.SESSION_STATE_CHANGED) {
                xrStateChanged(
                        XrEventDataSessionStateChanged.create(event.address())
                );
                continue;
            }

            vrProvider.onXREventReceived(vrEvent);
        }
    }

    private void xrStateChanged(XrEventDataSessionStateChanged event) {
        var stateChange = XRSessionState.fromId(event.state());
        vrProvider.getLogger().logDebug("VR Session State changed to: "+stateChange);


        switch (stateChange) {
            case READY -> {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    XrSessionBeginInfo sessionBeginInfo = XrSessionBeginInfo.calloc(stack)
                            .type(XR10.XR_TYPE_SESSION_BEGIN_INFO)
                            .next(NULL)
                            .primaryViewConfigurationType(XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO);

                    vrProvider.checkXRError(
                            XR10.xrBeginSession(vrProvider.getSession().getHandle(), sessionBeginInfo),
                            "xrBeginSession", "XRStateChangeREADY"
                    );
                }
                ready = true;
                active = true; //for convenience
                vrProvider.getLogger().logInfo("OpenXR session is READY");

            }

            case STOPPING -> {
                ready = false;
                initialized = false;
                active = false;

            }

            case VISIBLE, FOCUSED -> active = true;

            case EXITING, IDLE, SYNCHRONIZED -> active = false;
        }

        focused = stateChange == XRSessionState.FOCUSED;

        vrProvider.onStateChanged(stateChange);
    }

    // -------- DESTROY --------

    /**
     * Destroy VR state by setting its fields to initial values
     */
    public void destroy(){
        ready = false;
        active = false;
        focused = false;
        initialized = false;
    }
}
