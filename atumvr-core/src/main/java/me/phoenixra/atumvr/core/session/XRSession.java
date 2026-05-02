package me.phoenixra.atumvr.core.session;

import lombok.Getter;
import me.phoenixra.atumvr.api.AtumVRSession;
import me.phoenixra.atumvr.core.utils.XRUtils;
import me.phoenixra.atumvr.core.XRProvider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * XR session handler (low-level OpenXR stuff)
 */
public class XRSession implements AtumVRSession {
    private final XRProvider vrProvider;

    @Getter
    protected XrSession handle;

    @Getter
    protected XrSpace xrAppSpace;

    @Getter
    protected XrSpace xrViewSpace;



    @Getter
    protected XRInstance instance;

    @Getter
    protected XRSwapChain swapChain;

    @Getter
    protected XRSystem system;

    public XRSession(@NotNull XRProvider vrProvider){
        this.vrProvider = vrProvider;
        instance = new XRInstance(vrProvider);
        system = new XRSystem(vrProvider);
        swapChain = new XRSwapChain(vrProvider);
    }


    public void init() {
        instance.init();
        system.init();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            initSession(stack);
            initSpaces(stack);
            initDisplayRefreshRate(stack);
        }
        swapChain.init();
    }


    private void initSession(MemoryStack stack){

        long systemId = system.getSystemId();

        Struct<?> graphicsBind = system.createGraphicsBinding(
                stack,
                instance.getHandle(),
                systemId,
                vrProvider.getRenderer().getWindowHandle()
        );
        var sessionInfo = XrSessionCreateInfo.calloc(stack)
                .type(XR10.XR_TYPE_SESSION_CREATE_INFO)
                .next(graphicsBind.address())
                .systemId(systemId)
                .createFlags(0);

        PointerBuffer sessionBuf = stack.callocPointer(1);
        vrProvider.checkXRError(
                XR10.xrCreateSession(instance.getHandle(), sessionInfo, sessionBuf),
                "xrCreateSession", "Failed to create session"
        );
        handle = new XrSession(sessionBuf.get(0), instance.getHandle());

    }

    private void initSpaces(MemoryStack stack) {
        // Identity pose for all reference spaces
        XrPosef identity = XrPosef.calloc(stack)
                .set(
                        XrQuaternionf.calloc(stack).set(0, 0, 0, 1),
                        XrVector3f.calloc(stack).set(0f, 0f, 0f)
                );

        xrAppSpace  = XRUtils.createReferenceSpace(
                vrProvider,
                XR10.XR_REFERENCE_SPACE_TYPE_STAGE,
                identity,
                stack
        );

        identity = XrPosef.calloc(stack)
                .set(
                        XrQuaternionf.calloc(stack).set(0, 0, 0, 1),
                        XrVector3f.calloc(stack).set(0f, 0f, 0f)
                );
        xrViewSpace = XRUtils.createReferenceSpace(
                vrProvider,
                XR10.XR_REFERENCE_SPACE_TYPE_VIEW,
                identity,
                stack
        );
    }



    //@TODO test it
    private void initDisplayRefreshRate(MemoryStack stack) {
        if (handle.getCapabilities().XR_FB_display_refresh_rate) {
            IntBuffer refreshRateCount = stack.callocInt(1);
            vrProvider.checkXRError(
                    FBDisplayRefreshRate.xrEnumerateDisplayRefreshRatesFB(
                            handle, refreshRateCount, null
                    ),
                    "xrEnumerateDisplayRefreshRatesFB",
                    "first call"
            );
            FloatBuffer refreshRateBuffer = stack.callocFloat(refreshRateCount.get(0));
            vrProvider.checkXRError(
                    FBDisplayRefreshRate.xrEnumerateDisplayRefreshRatesFB(
                            handle, refreshRateCount, refreshRateBuffer
                    ),
                    "xrEnumerateDisplayRefreshRatesFB",
                    "second call"
            );
            refreshRateBuffer.rewind();
            vrProvider.checkXRError(
                    FBDisplayRefreshRate.xrRequestDisplayRefreshRateFB(
                            handle, refreshRateBuffer.get(refreshRateCount.get(0) -1)
                    ),
                    "xrRequestDisplayRefreshRateFB"
            );
        }
    }

    public void destroy(){

        requestExitAndDrain();

        try {
            swapChain.destroy();
        } catch (Throwable ignored) {}

        destroySpaceQuietly(this.xrAppSpace, "xrAppSpace");
        this.xrAppSpace = null;

        destroySpaceQuietly(this.xrViewSpace, "xrViewSpace");
        this.xrViewSpace = null;

        if (this.handle != null) {
            try {
                vrProvider.checkXRError(
                        false,
                        XR10.xrDestroySession(this.handle),
                        "xrDestroySession",
                        "session"
                );
            } catch (Throwable ignored) {}
            this.handle = null;
        }

        try { system.destroy(); }   catch (Throwable ignored) {}
        try { instance.destroy(); } catch (Throwable ignored) {}
    }

    private void destroySpaceQuietly(XrSpace space, String label) {
        if (space == null) return;
        try {
            vrProvider.checkXRError(
                    false,
                    XR10.xrDestroySpace(space),
                    "xrDestroySpace",
                    label
            );
        } catch (Throwable ignored) {}
    }


    private void requestExitAndDrain() {
        if (handle == null || instance == null || instance.getHandle() == null) {
            return;
        }
        try {
            XR10.xrRequestExitSession(handle);
        } catch (Throwable ignored) {
            // Session may already be in IDLE / EXITING — that's fine.
        }

        XrEventDataBuffer eventBuffer = instance.getXrEventBuffer();
        boolean ended = false;
        long deadline = System.currentTimeMillis() + 500L;

        while (System.currentTimeMillis() < deadline) {
            eventBuffer.clear();
            eventBuffer.type(XR10.XR_TYPE_EVENT_DATA_BUFFER);

            int err;
            try {
                err = XR10.xrPollEvent(instance.getHandle(), eventBuffer);
            } catch (Throwable t) {
                break;
            }
            if (err != XR10.XR_SUCCESS) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            var header = XrEventDataBaseHeader.create(eventBuffer.address());
            if (header.type() != XR10.XR_TYPE_EVENT_DATA_SESSION_STATE_CHANGED) {
                continue;
            }

            var ev = XrEventDataSessionStateChanged.create(header.address());
            int state = ev.state();

            if (!ended && state == XR10.XR_SESSION_STATE_STOPPING) {
                try {
                    XR10.xrEndSession(handle);
                } catch (Throwable ignored) {}
                ended = true;
            }
            if (state == XR10.XR_SESSION_STATE_EXITING
                    || state == XR10.XR_SESSION_STATE_LOSS_PENDING
                    || state == XR10.XR_SESSION_STATE_IDLE) {
                // Safe to destroy from here.
                if (!ended) {
                    // STOPPING may have been missed (already in IDLE/EXITING).
                    try { XR10.xrEndSession(handle); } catch (Throwable ignored) {}
                    ended = true;
                }
                break;
            }
        }
    }
}