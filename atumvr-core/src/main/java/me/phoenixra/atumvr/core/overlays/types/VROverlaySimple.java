package me.phoenixra.atumvr.core.overlays.types;

import me.phoenixra.atumvr.api.VRCore;
import me.phoenixra.atumvr.api.misc.AtumColor;
import me.phoenixra.atumvr.api.overlays.VROverlay;
import me.phoenixra.atumvr.api.overlays.VROverlayLocation;
import me.phoenixra.atumvr.api.overlays.impl.BaseVROverlay;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class VROverlaySimple extends BaseVROverlay {

    /**
     * Simple overlay that displays the colored shape
     * <br><br>
     * By default, it draws a square.
     * <br>
     * If you want to change it use {@link VROverlay#getTexelAspect()}
     *
     * @param vrCore core
     * @param overlayKey key
     * @param vrLocation position
     * @param widthInMeters width
     * @param textureColor - color for the texture
     */

    public VROverlaySimple(@NotNull VRCore vrCore,
                           String overlayKey,
                           VROverlayLocation vrLocation,
                           float widthInMeters,
                           AtumColor textureColor){
        super(vrCore,overlayKey, vrLocation,widthInMeters);
        getColor().setVariable(textureColor,false);

    }
    @Override
    protected boolean onInit(MemoryStack stack) {
        return true;
    }

    @Override
    protected boolean onUpdate(MemoryStack stack) {
        return true;
    }

    @Override
    protected void onOverlayRemove() {

    }

    @Override
    protected int provideOpenGLTextureId(MemoryStack stack) {
        int textureId = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureId);

        // Set texture parameters (e.g., filtering)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);

        int textureWidth = 1;
        int textureHeight = 1;

        ByteBuffer buffer = ByteBuffer.allocateDirect(textureWidth * textureHeight * 4); // 4 bytes per pixel (RGBA)

        for (int i = 0; i < textureWidth * textureHeight; i++) {
            buffer.put((byte) (getColor().getVariable().getRedInt() & 0xFF)); // Red component
            buffer.put((byte) (getColor().getVariable().getGreenInt() & 0xFF));   // Green component
            buffer.put((byte) (getColor().getVariable().getBlueInt() & 0xFF));   // Blue component
            buffer.put((byte) (0xFF));
        }
        buffer.flip();
        // Upload initial texture data if necessary
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA,
                textureWidth, textureHeight, 0,
                GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);

        //clear binding to not have the texture modified by something else
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        return textureId;
    }

    @Override
    protected boolean isColorUsed() {
        return true;
    }
}
