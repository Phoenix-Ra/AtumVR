package me.phoenixra.atumvr.core.overlays.types;

import me.phoenixra.atumconfig.api.placeholders.context.PlaceholderContext;
import me.phoenixra.atumconfig.api.utils.StringUtils;
import me.phoenixra.atumvr.api.AtumVRCore;
import me.phoenixra.atumvr.api.overlays.VROverlayLocation;
import me.phoenixra.atumvr.api.overlays.impl.BaseVROverlay;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public class VROverlayText extends BaseVROverlay {
    private final Font font;
    private final String originText;


    private String currentText;

    private int textureWidth;
    private int textureHeight;


    private long lastTextUpdate = System.currentTimeMillis();


    private ByteBuffer textureBuffer; // Persistent buffer
    private BufferedImage image; // Reusable image
    private Graphics2D g2d;


    private IntBuffer pboIds;
    private int currentPBOIndex = 0;
    private boolean graphicsInit;

    public VROverlayText(AtumVRCore vrCore, String overlayKey,
                         VROverlayLocation vrOverlayPosition, float width,
                         Font font,
                         String text,
                         int textureWidth,
                         int textureHeight
    ) {
        super(vrCore, overlayKey, vrOverlayPosition, width);
        this.font = font;
        this.originText = text;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.currentText = "";
    }

    @Override
    protected boolean onInit(MemoryStack stack) {
        return true;
    }

    @Override
    protected boolean onUpdate(MemoryStack stack) {
        String newText = StringUtils.formatWithPlaceholders(
                getVrCore(),
                originText,
                PlaceholderContext.EMPTY
        );
        if(currentText.equals(newText)) return true;

        lastTextUpdate = System.currentTimeMillis();

        currentText = newText;

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, getCurrentTextureId());

        int pboId = pboIds.get(currentPBOIndex);
        GL30.glBindBuffer(GL30.GL_PIXEL_UNPACK_BUFFER, pboId);


        ByteBuffer buffer = GL30.glMapBuffer(GL30.GL_PIXEL_UNPACK_BUFFER,
                GL30.GL_WRITE_ONLY, (long) textureWidth * textureHeight * 4, null);
        if (buffer != null) {
            // Write data into the PBO
            ByteBuffer newImageData = createTextTexture(currentText);
            buffer.put(newImageData);
            buffer.flip();

            GL30.glUnmapBuffer(GL30.GL_PIXEL_UNPACK_BUFFER);

            // Now update the texture with the PBO data
            GL30.glTexSubImage2D(GL30.GL_TEXTURE_2D, 0, 0, 0, textureWidth, textureHeight, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, 0);

            // Prepare for the next update
            currentPBOIndex = (currentPBOIndex + 1) % 2; //2 is pbo length
        }

        GL30.glBindBuffer(GL30.GL_PIXEL_UNPACK_BUFFER, 0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        return updateVRTexture(stack);
    }


    @Override
    protected int provideOpenGLTextureId(MemoryStack stack) {
        if(!graphicsInit){

            image = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);

            g2d = image.createGraphics();

            textureBuffer = ByteBuffer.allocateDirect(textureWidth * textureHeight * 4);

            pboIds = BufferUtils.createIntBuffer(2);

            GL30.glGenBuffers(pboIds);

            int bufferSize = textureWidth * textureHeight * 4; // Assuming RGBA

            for (int i = 0; i < 2; i++) {
                GL30.glBindBuffer(GL30.GL_PIXEL_UNPACK_BUFFER, pboIds.get(i));
                GL30.glBufferData(GL30.GL_PIXEL_UNPACK_BUFFER, bufferSize, GL30.GL_STREAM_DRAW);
            }
            GL30.glBindBuffer(GL30.GL_PIXEL_UNPACK_BUFFER, 0);
            graphicsInit = true;
        }
        lastTextUpdate = System.currentTimeMillis();

        int textureId = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureId);

        // Set texture parameters (e.g., filtering)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);

        // Upload initial texture data if necessary
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA,
                textureWidth, textureHeight, 0,
                GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE,
                (ByteBuffer) null
        );

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        return textureId;
    }
    @Override
    protected void onOverlayRemove() {
        g2d.dispose();
        textureBuffer.clear();
        pboIds.clear();
        graphicsInit = false;
        currentText = "";
    }
    @Override
    protected boolean isColorUsed() {
        return true;
    }

    private ByteBuffer createTextTexture(String text){
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dTemp = tempImage.createGraphics();
        g2dTemp.setFont(font);
        FontMetrics metrics = g2dTemp.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g2dTemp.dispose();

        float scale = Math.min(textureWidth / (float)textWidth, textureHeight / (float)textHeight);

        scale = (scale > 1) ? 1 : scale;

        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, textureWidth, textureHeight);
        g2d.setComposite(AlphaComposite.SrcOver);

        // Apply scale if necessary
        if (scale < 1) {
            g2d.scale(scale, scale);
        }

        g2d.setFont(font);
        g2d.setColor(Color.WHITE);

        // Calculate new position to centralize scaled text
        int x = (textureWidth - (int)(textWidth * scale)) / 2;
        int y = ((textureHeight - (int)(textHeight * scale)) / 2) + metrics.getAscent();

        g2d.drawString(text, x / scale, y / scale); // Adjust position based on scale
        return convertToByteBuffer(image);
    }
    public ByteBuffer convertToByteBuffer(BufferedImage image) {
        textureBuffer.clear();
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());


        for (int y = image.getHeight() - 1; y >= 0; y--) { // Start from the bottom row
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                textureBuffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                textureBuffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
                textureBuffer.put((byte) (pixel & 0xFF));         // Blue component
                textureBuffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
            }
        }

        textureBuffer.flip(); // Prepare for reading
        return textureBuffer;
    }
}
