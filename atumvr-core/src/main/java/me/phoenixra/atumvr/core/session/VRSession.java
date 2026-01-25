package me.phoenixra.atumvr.core.session;

import lombok.Getter;
import me.phoenixra.atumvr.core.utils.VRUtils;
import me.phoenixra.atumvr.core.VRProvider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * VR session handler (low-level OpenXR stuff)
 */
public class VRSession {
    private final VRProvider vrProvider;

    @Getter
    protected XrSession handle;

    @Getter
    protected XrSpace xrAppSpace;

    @Getter
    protected XrSpace xrViewSpace;



    @Getter
    protected VRInstance instance;

    @Getter
    protected VRSwapChain swapChain;

    @Getter
    protected VRSystem system;

    public VRSession(@NotNull VRProvider vrProvider){
        this.vrProvider = vrProvider;
        instance = new VRInstance(vrProvider);
        system = new VRSystem(vrProvider);
        swapChain = new VRSwapChain(vrProvider);
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

        xrAppSpace  = VRUtils.createReferenceSpace(
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
        xrViewSpace = VRUtils.createReferenceSpace(
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
        swapChain.destroy();
        if (this.xrAppSpace != null) {
            vrProvider.checkXRError(
                    false,
                    XR10.xrDestroySpace(this.xrAppSpace),
                    "xrDestroySpace",
                    "xrAppSpace"
            );
        }
        if (this.xrViewSpace != null) {
            vrProvider.checkXRError(
                    false,
                    XR10.xrDestroySpace(this.xrViewSpace),
                    "xrDestroySpace",
                    "xrViewSpace"
            );
        }
        if (this.handle != null) {
            vrProvider.checkXRError(
                    false,
                    XR10.xrDestroySession(this.handle),
                    "xrDestroySession",
                    "xrAppSpace"
            );
        }
        system.destroy();
        instance.destroy();
    }
}
