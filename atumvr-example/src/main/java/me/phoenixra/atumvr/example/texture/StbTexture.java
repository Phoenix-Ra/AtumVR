package me.phoenixra.atumvr.example.texture;

import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class StbTexture {
    private static final String DATA_DIR = "data/";

    private static final int FALLBACK_SIZE = 64;
    private static final int FALLBACK_CELL = 8;

    @Getter
    private int textureId;
    @Getter
    private int width;
    @Getter
    private int height;

    public StbTexture(String path){
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthPointer = stack.mallocInt(1);
            IntBuffer heightPointer = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Use STBImage to load the texture
            ByteBuffer image = STBImage.stbi_load(
                    DATA_DIR + path,
                    widthPointer, heightPointer,
                    comp,
                    4
            );
            boolean loaded = image != null;
            ByteBuffer fallback = null;
            if (loaded) {
                this.width = widthPointer.get(0);
                this.height = heightPointer.get(0);
            } else {
                System.err.println("Failed to load a texture file: " + path
                        + " (" + STBImage.stbi_failure_reason() + "), using fallback");
                this.width = FALLBACK_SIZE;
                this.height = FALLBACK_SIZE;
                fallback = createFallbackImage();
                image = fallback;
            }

            // Generate a new OpenGL texture
            int textureId = GL30.glGenTextures();
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureId);

            // Upload the texture data
            GL30.glTexImage2D(
                    GL30.GL_TEXTURE_2D,
                    0,
                    GL30.GL_RGBA,
                    width, height,
                    0,
                    GL30.GL_RGBA,
                    GL30.GL_UNSIGNED_BYTE,
                    image
            );

            // Set texture parameters
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
            GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);

            // Free the image memory
            if (loaded) {
                STBImage.stbi_image_free(image);
            } else {
                MemoryUtil.memFree(fallback);
            }

            this.textureId = textureId;
        }
    }


    private static ByteBuffer createFallbackImage() {
        ByteBuffer buffer = MemoryUtil.memAlloc(FALLBACK_SIZE * FALLBACK_SIZE * 4);
        for (int y = 0; y < FALLBACK_SIZE; y++) {
            for (int x = 0; x < FALLBACK_SIZE; x++) {
                boolean magenta = ((x / FALLBACK_CELL) + (y / FALLBACK_CELL)) % 2 == 0;
                buffer.put((byte) (magenta ? 255 : 0)); // R
                buffer.put((byte) 0);                    // G
                buffer.put((byte) (magenta ? 255 : 0)); // B
                buffer.put((byte) 255);                  // A
            }
        }
        buffer.flip();
        return buffer;
    }
}