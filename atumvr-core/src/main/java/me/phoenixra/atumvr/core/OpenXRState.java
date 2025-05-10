package me.phoenixra.atumvr.core;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRState;
import me.phoenixra.atumvr.core.enums.XREvent;
import me.phoenixra.atumvr.core.enums.XRSessionStateChange;
import me.phoenixra.atumvr.core.init.OpenXRInstance;
import me.phoenixra.atumvr.core.init.OpenXRSession;
import me.phoenixra.atumvr.core.init.OpenXRSwapChain;
import me.phoenixra.atumvr.core.init.OpenXRSystem;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRState implements VRState {
    @Getter
    private OpenXRProvider vrProvider;

    @Getter
    protected OpenXRInstance xrInstance;
    @Getter
    protected OpenXRSystem xrSystem;
    @Getter
    protected OpenXRSession xrSession;
    @Getter
    protected OpenXRSwapChain xrSwapChain;



    @Getter
    protected final List<XREvent> xrEventsReceived = new ArrayList<>();


    @Getter
    protected boolean paused = false;
    @Getter
    protected boolean running = false;

    @Getter
    protected boolean initialized = false;

    public OpenXRState(OpenXRProvider vrProvider){
        this.vrProvider = vrProvider;

    }

    @Override
    public void init() throws Throwable{
        this.xrInstance = new OpenXRInstance(this);
        this.xrSystem = new OpenXRSystem(this);
        this.xrSession = new OpenXRSession(this);
        this.xrSwapChain = new OpenXRSwapChain(this);

        xrInstance.init();
        xrSystem.init();
        xrSession.init();

        xrSwapChain.init();

        vrProvider.inputHandler.init();
        vrProvider.vrRenderer.init();

        initialized = true;
    }



    protected void pollVREvents() {
        xrEventsReceived.clear();
        XrEventDataBuffer eventBuffer = xrInstance.getXrEventBuffer();
        while (true) {
            eventBuffer.clear();
            eventBuffer.type(XR10.XR_TYPE_EVENT_DATA_BUFFER);
            int error = XR10.xrPollEvent(xrInstance.getHandle(), eventBuffer);
            vrProvider.checkXRError(error, "xrPollEvent", "");
            if (error != XR10.XR_SUCCESS) {
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
        switch (stateChange) {
            case READY -> {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    XrSessionBeginInfo sessionBeginInfo = XrSessionBeginInfo.calloc(stack)
                            .type(XR10.XR_TYPE_SESSION_BEGIN_INFO)
                            .next(NULL)
                            .primaryViewConfigurationType(XR10.XR_VIEW_CONFIGURATION_TYPE_PRIMARY_STEREO);

                    vrProvider.checkXRError(
                            XR10.xrBeginSession(xrSession.getHandle(), sessionBeginInfo),
                            "xrBeginSession", "XRStateChangeREADY"
                    );
                }
                this.running = true;

            }

            case STOPPING -> {
                this.running = false;
                this.paused = false;
            }

            case VISIBLE, FOCUSED -> paused = false;

            case EXITING, IDLE, SYNCHRONIZED -> paused = true;
        }
        vrProvider.onStateChanged(stateChange);
    }



    @Override
    public int getEyeTexWidth() {
        return xrSwapChain.getEyeMaxWidth();
    }

    @Override
    public int getEyeTexHeight() {
        return xrSwapChain.getEyeMaxHeight();
    }




    public void destroy(){

        if(xrSwapChain != null){
            xrSwapChain.destroy();
        }
        if(xrSession != null){
            xrSession.destroy();
        }
        if(xrSystem != null){
            xrSystem.destroy();
        }
        if(xrInstance != null){
            xrInstance.destroy();
        }
    }
}
