package me.phoenixra.atumvr.core;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRState;
import me.phoenixra.atumvr.core.enums.XREvent;
import me.phoenixra.atumvr.core.enums.XRSessionStateChange;
import me.phoenixra.atumvr.core.init.XRInstance;
import me.phoenixra.atumvr.core.init.XRSession;
import me.phoenixra.atumvr.core.init.XRSwapChain;
import me.phoenixra.atumvr.core.init.XRSystem;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.NULL;

@Getter
public class XRState implements VRState {
    private final XRProvider vrProvider;

    protected XRInstance vrInstance;
    protected XRSystem vrSystem;
    protected XRSession vrSession;
    protected XRSwapChain vrSwapChain;



    protected final List<XREvent> xrEventsReceived = new ArrayList<>();



    protected boolean ready = false;
    protected boolean active = false;
    protected boolean focused = false;

    protected boolean initialized = false;

    public XRState(XRProvider vrProvider){
        this.vrProvider = vrProvider;

    }

    public void init() throws Throwable{
        vrInstance = new XRInstance(this);
        vrSystem = new XRSystem(this);
        vrSession = new XRSession(this);
        vrSwapChain = new XRSwapChain(this);

        vrInstance.init();
        vrSystem.init();
        vrSession.init();

        vrSwapChain.init();

        while (!ready){
            vrProvider.getLogger().logInfo("Waiting for OpenXR session to start...");
            pollVREvents();
        }
        vrProvider.inputHandler.init();
        vrProvider.renderer.init();

        initialized = true;
    }



    protected void pollVREvents() {
        xrEventsReceived.clear();
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
            }else {
                xrEventsReceived.add(vrEvent);
            }
        }
    }
    private void xrStateChanged(XrEventDataSessionStateChanged event) {
        var stateChange = XRSessionStateChange.fromId(event.state());
        vrProvider.getLogger().logDebug("VR Session State changed to: "+stateChange);


        switch (stateChange) {
            case READY -> {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    XrSessionBeginInfo sessionBeginInfo = XrSessionBeginInfo.calloc(stack)
                            .type(XR10.XR_TYPE_SESSION_BEGIN_INFO)
                            .next(NULL)
                            .primaryViewConfigurationType(XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO);

                    vrProvider.checkXRError(
                            XR10.xrBeginSession(vrSession.getHandle(), sessionBeginInfo),
                            "xrBeginSession", "XRStateChangeREADY"
                    );
                }
                ready = true;
                active = true; //for convenience
                vrProvider.getLogger().logInfo("OpenXR session is READY");

            }

            case STOPPING -> {
                ready = false;
                active = false;
            }

            case VISIBLE, FOCUSED -> active = true;

            case EXITING, IDLE, SYNCHRONIZED -> active = false;
        }

        focused = stateChange == XRSessionStateChange.FOCUSED;

        vrProvider.onStateChanged(stateChange);
    }



    public int getEyeTexWidth() {
        return vrSwapChain.getEyeMaxWidth();
    }

    public int getEyeTexHeight() {
        return vrSwapChain.getEyeMaxHeight();
    }




    public void destroy(){

        if(vrSwapChain != null){
            vrSwapChain.destroy();
        }
        if(vrSession != null){
            vrSession.destroy();
        }
        if(vrSystem != null){
            vrSystem.destroy();
        }
        if(vrInstance != null){
            vrInstance.destroy();
        }
        xrEventsReceived.clear();

        ready = false;
        active = false;
        focused = false;
        initialized = false;
    }
}
