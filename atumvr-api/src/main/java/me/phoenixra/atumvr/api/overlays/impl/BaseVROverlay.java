package me.phoenixra.atumvr.api.overlays.impl;


import lombok.Getter;
import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.devices.VRDevice;
import me.phoenixra.atumvr.api.misc.AtumColor;
import me.phoenixra.atumvr.api.overlays.VROverlay;
import me.phoenixra.atumvr.api.overlays.VROverlayLocation;
import me.phoenixra.atumvr.api.overlays.variable.OverlayVariableNotNull;
import me.phoenixra.atumvr.api.overlays.variable.OverlayVariableNullable;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.VR;
import org.lwjgl.system.MemoryStack;

import java.nio.LongBuffer;
import java.util.Arrays;

import static org.lwjgl.openvr.VR.EVROverlayError_VROverlayError_None;
import static org.lwjgl.openvr.VR.EVROverlayError_VROverlayError_UnknownOverlay;
import static org.lwjgl.openvr.VROverlay.*;

public abstract class BaseVROverlay implements VROverlay {

    @Getter
    private final VRCore vrCore;

    @Getter
    private long overlayHandle;

    @Getter
    private final String overlayKey;

    @Getter
    private int currentTextureId = 0;


    @Getter
    private final OverlayVariableNotNull<VROverlayLocation> overlayPosition;
    @Getter
    private final OverlayVariableNotNull<Float> width;
    @Getter
    private final OverlayVariableNotNull<Float> texelAspect;
    @Getter
    private final OverlayVariableNotNull<Float> curvature;
    @Getter
    private final OverlayVariableNotNull<Integer> sortOrder;
    @Getter
    private final OverlayVariableNotNull<AtumColor> color;
    @Getter
    private final OverlayVariableNotNull<Float> alpha;
    @Getter
    private final OverlayVariableNullable<VRDevice> attachedToDevice;



    @Getter
    private boolean initialized;

    public BaseVROverlay(VRCore vrCore,
                         String overlayKey,
                         VROverlayLocation vrLocation,
                         float width) {
        this.vrCore = vrCore;
        this.overlayKey = overlayKey;

        this.overlayPosition  =  new OverlayVariableNotNull<>(vrLocation);
        this.width            =  new OverlayVariableNotNull<>(width);
        this.texelAspect      =  new OverlayVariableNotNull<>(1.0f);
        this.curvature        =  new OverlayVariableNotNull<>(0f);

        this.sortOrder        =  new OverlayVariableNotNull<>(1);
        this.color            =  new OverlayVariableNotNull<>(AtumColor.WHITE);
        this.alpha            =  new OverlayVariableNotNull<>(1.0f);
        this.attachedToDevice =  new OverlayVariableNullable<>(null);

        this.overlayPosition.getVariable().setVrOverlay(this);
    }

    protected abstract boolean onInit(MemoryStack stack);
    protected abstract boolean onUpdate(MemoryStack stack);
    protected abstract void onOverlayRemove();
    protected abstract int provideOpenGLTextureId(MemoryStack stack);
    protected abstract boolean isColorUsed();
    @Override
    public final boolean init() {
        if (initialized) {
            return false;
        }
        //VRSettings.logger.info("Initializing overlay: "+ getOverlayKey());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            //CHECK DEVICE IF OVERLAY IS ATTACHED TO ONE
            VRDevice device = attachedToDevice.getVariable();
            int deviceIndex = 0;
            if(device!=null){
                deviceIndex = device.findDeviceIndex(stack);
                if(deviceIndex==-1){
                   /* VRSettings.logger.info("Failed to initialize overlay: " + getOverlayKey()
                            +" Cause: Attached device not found"
                    );*/
                    return false;
                }
                if(!VRDevice.isDeviceConnected(deviceIndex)){
                    /*VRSettings.logger.info("Failed to initialize overlay: " + getOverlayKey()
                            +" Cause: Attached device not connected"
                    );*/
                    return false;
                }
            }

            //CREATE OVERLAY
            LongBuffer pOverlayHandle = stack.mallocLong(1);
            int error = VROverlay_CreateOverlay(getOverlayKey(), "DreamOverlay_"+getOverlayKey(), pOverlayHandle);
            if (error != EVROverlayError_VROverlayError_None) {
                vrCore.logError("Failed to create overlay: " + getOverlayKey()
                                +" Cause: "+
                        VROverlay_GetOverlayErrorNameFromEnum(error)
                );
                return false;
            }
            overlayHandle = pOverlayHandle.get(0);

            //TEXTURE
            currentTextureId = provideOpenGLTextureId(stack);
            error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayTexture(overlayHandle,
                    createVRTexture(
                            stack,
                            currentTextureId
                    )
            );
            if(error != EVROverlayError_VROverlayError_None){
                vrCore.logError("Failed to apply texture to overlay: " + getOverlayKey()
                        + " Cause: "
                        + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                return false;
            }

            //ALPHA VALUE
            if(alpha.getVariable() != 1f) {
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayAlpha(overlayHandle,
                        alpha.getVariable()
                );
                if (error != EVROverlayError_VROverlayError_None) {
                    vrCore.logError("Failed to apply alpha value to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                    return false;
                }
            }

            //COLOR
            if(!isColorUsed()) {
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayColor(
                        overlayHandle,
                        color.getVariable().getRed(),
                        color.getVariable().getGreen(),
                        color.getVariable().getBlue()
                );
                if (error != EVROverlayError_VROverlayError_None) {
                    vrCore.logError("Failed to apply color to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                    return false;
                }
            }

            //WIDTH
            error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayWidthInMeters(overlayHandle, width.getVariable());
            if(error != EVROverlayError_VROverlayError_None){
                vrCore.logError("Failed to apply width to overlay: " + getOverlayKey()
                        + " Cause: "
                        + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                return false;
            }

            //TEXEL ASPECT
            if(texelAspect.getVariable()!=1) {
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayTexelAspect(overlayHandle, texelAspect.getVariable());
                if (error != EVROverlayError_VROverlayError_None) {
                    vrCore.logError("Failed to apply texel aspect to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                }
            }

            //SORT ORDER
            error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlaySortOrder(overlayHandle, getSortOrder().getVariable());
            if(error != EVROverlayError_VROverlayError_None){
                vrCore.logError("Failed to apply sort order to overlay: " + getOverlayKey()
                        + " Cause: "
                        + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
            }

            //POSITIONING
            HmdMatrix34 overlayTransform = HmdMatrix34.malloc(stack);
            float[] matrix = getOverlayPosition().getVariable().toMatrix();
            for(int i = 0; i<=11; i++){
                overlayTransform.m(i, matrix[i]);
            }
            if(device == null ) {
                //attach to the standing position
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayTransformAbsolute(
                        overlayHandle,
                        VR.ETrackingUniverseOrigin_TrackingUniverseStanding,
                        overlayTransform
                );
            } else {
                //attach to device
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayTransformTrackedDeviceRelative(
                        overlayHandle,
                        deviceIndex,
                        overlayTransform
                );
            }
            if(error != EVROverlayError_VROverlayError_None){
                vrCore.logError("Failed to apply transform to overlay: " + getOverlayKey()
                        + " Cause: "
                        + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                return false;
            }

            //CURVATURE
            if(curvature.getVariable() != 0) {
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayCurvature(
                        overlayHandle,
                        curvature.getVariable()
                );
                if(error != EVROverlayError_VROverlayError_None){
                    vrCore.logError("Failed to apply curvature to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                    return false;
                }
            }

            //INITIALIZE CHILD
            boolean success = onInit(stack);
            if(!success){
                vrCore.logError("Failed to initialize the overlay: "+ getOverlayKey()
                        + " Cause: The child class has failed to initialize"
                );
                return false;
            }

            //SHOW THE OVERLAY
            error = org.lwjgl.openvr.VROverlay.VROverlay_ShowOverlay(overlayHandle);

            if(error != EVROverlayError_VROverlayError_None){
                vrCore.logError("Failed to show overlay: "+ getOverlayKey()
                        + " Cause: "
                        + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                return false;
            }
        }catch (Throwable throwable){
            throw new RuntimeException(throwable);
        }
        //VRSettings.logger.info("Successfully initialized an overlay: "+ getOverlayKey());
        initialized = true;
        return true;
    }

    @Override
    public final boolean update(boolean force) {
        if (!initialized) {
            init();
            return false;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int error;
            //CHECK THE OVERLAY FOR EXISTENCE
            error = VROverlay_FindOverlay(getOverlayKey(),stack.mallocLong(1));
            if(error ==  EVROverlayError_VROverlayError_UnknownOverlay){
                //OVERLAY DOES NOT EXISTS
                initialized = false;
                return false;
            }else if(error != EVROverlayError_VROverlayError_None){
                //UNEXPECTED ERROR
                vrCore.logError("Failed check an existence of an overlay: " + getOverlayKey()
                        + " Cause: "
                        + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                return false;
            }
            //CHECK DEVICE IF OVERLAY IS ATTACHED TO ONE
            VRDevice device = attachedToDevice.getVariable();
            int deviceIndex = 0;
            if(device!=null){
                deviceIndex = device.findDeviceIndex(stack);
                if(deviceIndex==-1){
                    removeOverlay();
                    return false;
                }
                if(!VRDevice.isDeviceConnected(deviceIndex)){
                    removeOverlay();
                    return false;
                }
            }

            //------OVERLAY EXISTS AND DEVICE IS CONNECTED

            //POSITIONING UPDATE
            if(getOverlayPosition().isUpdated() || attachedToDevice.isUpdated()){
                HmdMatrix34 overlayTransform = HmdMatrix34.malloc(stack);
                float[] matrix = getOverlayPosition().getVariable().toMatrix();
                for(int i = 0; i<=11; i++){
                    overlayTransform.m(i, matrix[i]);
                }
                if(device == null ) {
                    error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayTransformAbsolute(
                            overlayHandle,
                            VR.ETrackingUniverseOrigin_TrackingUniverseStanding,
                            overlayTransform
                    );
                } else {
                    error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayTransformTrackedDeviceRelative(
                            overlayHandle,
                            deviceIndex,
                            overlayTransform
                    );
                }

                if(error != EVROverlayError_VROverlayError_None){
                    vrCore.logError("Failed to update transform for overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                    return false;
                }
                getOverlayPosition().setUpdated(false);
                getAttachedToDevice().setUpdated(false);
            }

            //SORT ORDER UPDATE
            if(getSortOrder().isUpdated()){
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlaySortOrder(overlayHandle, getSortOrder().getVariable());
                if(error != EVROverlayError_VROverlayError_None){
                    vrCore.logError("Failed to update sort order for overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                }
                getSortOrder().setUpdated(false);
            }

            //WIDTH UPDATE
            if(getWidth().isUpdated()){
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayWidthInMeters(overlayHandle, width.getVariable());

                if(error != EVROverlayError_VROverlayError_None){
                    vrCore.logError("Failed to apply width to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                    return false;
                }
                getWidth().setUpdated(false);
            }

            //TEXEL ASPECT UPDATE
            if(getTexelAspect().isUpdated()){
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayTexelAspect(overlayHandle, texelAspect.getVariable());
                if (error != EVROverlayError_VROverlayError_None) {
                    vrCore.logError("Failed to apply texel aspect to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                }
                getTexelAspect().setUpdated(false);
            }

            //COLOR UPDATE
            if(!isColorUsed() && getColor().isUpdated()) {
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayColor(
                        overlayHandle,
                        color.getVariable().getRed(),
                        color.getVariable().getGreen(),
                        color.getVariable().getBlue()
                );
                if (error != EVROverlayError_VROverlayError_None) {
                    vrCore.logError("Failed to apply color to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                    return false;
                }
                getColor().setUpdated(false);
            }

            //ALPHA UPDATE
            if(alpha.isUpdated()){
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayAlpha(overlayHandle,
                        alpha.getVariable()
                );
                if (error != EVROverlayError_VROverlayError_None) {
                    vrCore.logError("Failed to update alpha value to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                    return false;
                }
                getAlpha().setUpdated(false);
            }

            //CURVATURE UPDATE
            if(getCurvature().isUpdated()){
                error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayCurvature(
                        overlayHandle,
                        curvature.getVariable()
                );
                if(error != EVROverlayError_VROverlayError_None){
                    vrCore.logError("Failed to apply curvature to overlay: " + getOverlayKey()
                            + " Cause: "
                            + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
                    return false;
                }
                getCurvature().setUpdated(false);
            }

            //CHILD UPDATE
            boolean success = onUpdate(stack);
            if(!success){
                vrCore.logError("Failed to update the overlay: "+ getOverlayKey()
                        + " Cause: The child class has failed to update"
                );
                return false;
            }

        }catch (Throwable throwable){
            throw new RuntimeException(throwable);
        }
        return true;
    }

    @Override
    public void remove() {
        if(!initialized) return;
        removeOverlay();
    }

    private void removeOverlay(){
        try {
            VROverlay_DestroyOverlay(overlayHandle);
            GL30.glDeleteTextures(currentTextureId);
            initialized = false;
            currentTextureId = 0;
            onOverlayRemove();
        }catch (Throwable throwable){
            vrCore.logError("Exception on removal of an overlay:\n "
                    + Arrays.toString(throwable.getStackTrace())
            );
        }
    }
    protected Texture createVRTexture(MemoryStack stack, int textureId){
        Texture texture = Texture.malloc(stack);
        texture.handle(textureId);
        texture.eType(VR.ETextureType_TextureType_OpenGL);
        texture.eColorSpace(VR.EColorSpace_ColorSpace_Auto);
        return texture;
    }

    protected final boolean updateVRTexture(MemoryStack stack){
        if(!initialized) return false;
        //clear the native memory to not cause memory leaks
        int error = org.lwjgl.openvr.VROverlay.VROverlay_ClearOverlayTexture(overlayHandle);
        if(error != EVROverlayError_VROverlayError_None){
            vrCore.logError("Failed to clear texture for overlay: " + getOverlayKey()
                    + " Cause: "
                    + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
            return false;
        }

        error = org.lwjgl.openvr.VROverlay.VROverlay_SetOverlayTexture(overlayHandle,
                createVRTexture(
                        stack,
                        currentTextureId
                )
        );
        if(error != EVROverlayError_VROverlayError_None){
            vrCore.logError("Failed to update texture for overlay: " + getOverlayKey()
                    + " Cause: "
                    + org.lwjgl.openvr.VROverlay.VROverlay_GetOverlayErrorNameFromEnum(error));
            return false;
        }
        return true;
    }



}
