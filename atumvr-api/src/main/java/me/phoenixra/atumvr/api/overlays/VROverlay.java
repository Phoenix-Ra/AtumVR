package me.phoenixra.atumvr.api.overlays;

import me.phoenixra.atumvr.api.AtumVRCore;
import me.phoenixra.atumvr.api.devices.VRDevice;
import me.phoenixra.atumvr.api.misc.AtumColor;
import me.phoenixra.atumvr.api.overlays.variable.OverlayVariableNotNull;
import me.phoenixra.atumvr.api.overlays.variable.OverlayVariableNullable;
import org.jetbrains.annotations.NotNull;

public interface VROverlay {


    /**
     * Initializing the overlay in OpenVR
     *
     * @return if succeeded
     */
    boolean init();

    /**
     * Updates the overlay to match the data of this object
     *
     * @param force if true will update ALL data, even if it wasn't changed for this object
     * @return if succeeded
     */
    boolean update(boolean force);

    /**
     * Removes overlay if it was initialized and
     * changes initialization and textureId values to default
     */
    void remove();


    /**
     * Get Overlay positioning
     *
     * @return position object
     */
    @NotNull
    OverlayVariableNotNull<VROverlayLocation> getOverlayPosition();

    /**
     * Get width of an overlay in meters.
     * <br><br>
     * <p>The height is auto-applied depending on</p>
     * <p>the aspect ratio of a texture resolution and texel aspect {@link VROverlay#getTexelAspect()}</p>
     *
     * <br>
     * <p>For example if you have texture resolution 100x200 and texel aspect 0.5 </p>
     * <p>then height will be 4 times larger than width
     * (usually, may vary bcz of the VR behaviour)</p>
     *
     * @return width of an overlay in meters
     */
    @NotNull
    OverlayVariableNotNull<Float> getWidth();

    /**
     * Get texel aspect, i.e.  width/height ratio
     * <br><br>
     * You can use it to modify the height,
     * without changing the whole texture resolution
     * <br><br>
     * Default is 1.0
     *
     * @return texel aspect
     */
    @NotNull
    OverlayVariableNotNull<Float> getTexelAspect();

    /**
     * Get curvature of an overlay.
     * It can help u to make it look like 3D.
     * <br>
     * The great example is the SteamVR desktop menu
     * <br><br>
     * Value has to be from 0 to 1.
     * <br>
     * Default is 0
     * @return curvature
     */
    @NotNull
    OverlayVariableNotNull<Float> getCurvature();


    /**
     * Get overlay color
     *
     * @return the color
     */
    @NotNull
    OverlayVariableNotNull<AtumColor> getColor();

    /**
     * Get overlay alpha value, which affects on the transparency level.
     * <br><br>
     * Value from 1.0 to 0
     * <br>
     * Default is 1.0 - no transparency
     *
     * @return alpha value
     */
    @NotNull
    OverlayVariableNotNull<Float> getAlpha();

    /**
     * Get sort order of an overlay.
     * <br>
     * The value is positive or 0
     */
    @NotNull
    OverlayVariableNotNull<Integer> getSortOrder();


    /**
     * Get current texture id
     * <br>
     * By default, it is 0, but after initialization it has a specific value
     * <br><br>
     * Use it to modify the texture, rather than create new one each update tick
     *
     * @return the key
     */
    int getCurrentTextureId();

    /**
     * Get overlay key
     *
     * @return the key
     */
    @NotNull
    String getOverlayKey();

    /**
     * Get the overlayHandle value, which can
     * be used to work with overlay
     * @return long value
     */
    long getOverlayHandle();

    /**
     * Get the device to which an overlay is attached
     * or null if not attached
     *
     * @return variable that can hold device or null
     */
    OverlayVariableNullable<VRDevice> getAttachedToDevice();


    /**
     * Is overlay initialized in OpenVR framework
     *
     * @return if initialized
     */
    boolean isInitialized();




    @NotNull
    AtumVRCore getVrCore();


}
