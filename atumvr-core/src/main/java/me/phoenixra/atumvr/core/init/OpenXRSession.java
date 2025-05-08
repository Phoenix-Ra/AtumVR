package me.phoenixra.atumvr.core.init;

import lombok.Getter;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.OpenXRState;
import org.lwjgl.PointerBuffer;
import org.lwjgl.openxr.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenXRSession {
    private final OpenXRState xrState;

    @Getter
    protected XrSession handle;

    @Getter
    protected XrSpace xrAppSpace;
    @Getter
    protected XrSpace xrViewSpace;

    public OpenXRSession(OpenXRState xrState){
        this.xrState = xrState;

    }


    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            OpenXRProvider provider = this.xrState.getVrProvider();
            OpenXRInstance xrInstance  = xrState.getXrInstance();
            long systemId = xrState.getXrSystem().getSystemId();

            // 3) Create an XR session with proper GL binding
            Struct<?> graphicsBind = xrState.getOsCompatibility().checkGraphics(
                    stack,
                    xrInstance.getHandle(),
                    systemId,
                    provider.getVrRenderer().getWindowHandle()
            );
            XrSessionCreateInfo sessionInfo = XrSessionCreateInfo.calloc(stack)
                    .type(XR10.XR_TYPE_SESSION_CREATE_INFO)
                    .next(graphicsBind.address())
                    .systemId(systemId)
                    .createFlags(0);

            PointerBuffer sessionBuf = stack.callocPointer(1);
            provider.checkXRError(
                    XR10.xrCreateSession(xrInstance.getHandle(), sessionInfo, sessionBuf),
                    "xrCreateSession", "creating session"
            );
            handle = new XrSession(sessionBuf.get(0), xrInstance.getHandle());

            initSpace();
            initDisplayRefreshRate();
        }
    }

    public void initSpace() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Identity pose for all reference spaces
            XrPosef identity = XrPosef.calloc(stack)
                    .set(
                            XrQuaternionf.calloc(stack).set(0, 0, 0, 1),
                            XrVector3f.calloc(stack).set(0f, 0f, 0f)
                    );

            xrAppSpace  = createReferenceSpace(stack, XR10.XR_REFERENCE_SPACE_TYPE_STAGE, identity);
            xrViewSpace = createReferenceSpace(stack, XR10.XR_REFERENCE_SPACE_TYPE_VIEW,  identity);
        }
    }

    private void initDisplayRefreshRate() {
        if (handle.getCapabilities().XR_FB_display_refresh_rate) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer refreshRateCount = stack.callocInt(1);
                FBDisplayRefreshRate.xrEnumerateDisplayRefreshRatesFB(handle, refreshRateCount, null);
                FloatBuffer refreshRateBuffer = stack.callocFloat(refreshRateCount.get(0));
                FBDisplayRefreshRate.xrEnumerateDisplayRefreshRatesFB(handle, refreshRateCount, refreshRateBuffer);
                refreshRateBuffer.rewind();
                FBDisplayRefreshRate.xrRequestDisplayRefreshRateFB(handle, refreshRateBuffer.get(refreshRateCount.get(0) -1));
            }
        }
    }

    private XrSpace createReferenceSpace(MemoryStack stack, int spaceType, XrPosef identityPose) {
        XrReferenceSpaceCreateInfo spaceInfo = XrReferenceSpaceCreateInfo.calloc(stack)
                .type(XR10.XR_TYPE_REFERENCE_SPACE_CREATE_INFO)
                .next(NULL)
                .referenceSpaceType(spaceType)
                .poseInReferenceSpace(identityPose);

        PointerBuffer pSpace = stack.callocPointer(1);
        xrState.getVrProvider().checkXRError(
                XR10.xrCreateReferenceSpace(handle, spaceInfo, pSpace),
                "xrCreateReferenceSpace", "Spacetype: "+spaceType
        );

        return new XrSpace(pSpace.get(0), handle);
    }

    public void destroy(){
        OpenXRProvider provider = xrState.getVrProvider();
        int error;
        if (this.xrAppSpace != null) {
            error = XR10.xrDestroySpace(this.xrAppSpace);
            provider.checkXRError(error, "xrDestroySpace", "xrAppSpace");
        }
        if (this.xrViewSpace != null) {
            error = XR10.xrDestroySpace(this.xrViewSpace);
            provider.checkXRError(error, "xrDestroySpace", "xrViewSpace");
        }
        if (this.handle != null) {
            error = XR10.xrDestroySession(this.handle);
            provider.checkXRError(error, "xrDestroySession", "");
        }
    }
}
