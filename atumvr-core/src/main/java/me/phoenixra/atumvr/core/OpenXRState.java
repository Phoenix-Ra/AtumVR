package me.phoenixra.atumvr.core;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRState;
import me.phoenixra.atumvr.core.enums.OpenXREvent;
import me.phoenixra.atumvr.core.enums.XRSessionStateChange;
import me.phoenixra.atumvr.core.init.OpenXRInstance;
import me.phoenixra.atumvr.core.init.OpenXRSession;
import me.phoenixra.atumvr.core.init.OpenXRSwapChain;
import me.phoenixra.atumvr.core.init.OpenXRSystem;
import me.phoenixra.atumvr.core.oscompat.OSCompatibility;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackCalloc;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRState implements VRState {
    @Getter
    private OpenXRProvider vrProvider;

    @Getter
    protected OSCompatibility osCompatibility;

    @Getter
    protected OpenXRInstance xrInstance;
    @Getter
    protected OpenXRSystem xrSystem;
    @Getter
    protected OpenXRSession xrSession;
    @Getter
    protected OpenXRSwapChain xrSwapChain;



    @Getter
    protected final List<OpenXREvent> xrEventsReceived = new ArrayList<>();


    @Getter
    protected boolean paused = true;
    @Getter
    protected boolean initialized = false;

    public OpenXRState(OpenXRProvider vrProvider){
        this.vrProvider = vrProvider;
        this.osCompatibility = OSCompatibility.detectDevice();
        xrInstance = new OpenXRInstance(this);
        xrSystem = new OpenXRSystem(this);
        xrSession = new OpenXRSession(this);
        xrSwapChain = new OpenXRSwapChain(this);
    }

    @Override
    public void init() throws Throwable{
        xrInstance.init();
        xrSystem.init();
        xrSession.init();
        /*while (paused) {
            vrProvider.getLogger().logInfo("Waiting for OpenXR session READY state...");
            pollVREvents();
        }
        xrSession.initSpace();*/

        xrSwapChain.init();

        vrProvider.getVrRenderer().init();
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
            var vrEvent = OpenXREvent.fromId(event.type());
            if (vrEvent == OpenXREvent.SESSION_STATE_CHANGED) {
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
            case READY: {
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
                this.paused = false;
                break;
            }
            case STOPPING: {
                vrProvider.checkXRError(
                        XR10.xrEndSession(xrSession.getHandle()),
                        "xrEndSession", "XRStateChangeSTOPPING"
                );

                vrProvider.destroy();
            }
            case VISIBLE, FOCUSED: {
                paused = false;
                break;
            }
            case EXITING, IDLE, SYNCHRONIZED: {
                paused = true;
                break;
            }
            case LOSS_PENDING: {
                break;
            }
            default:
                break;
        }
    }



    @Override
    public int getEyeMaxWidth() {
        return xrSwapChain.getEyeMaxWidth();
    }

    @Override
    public int getEyeMaxHeight() {
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
