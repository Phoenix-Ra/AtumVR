package me.phoenixra.atumvr.example.texture;

import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class StbTexture {
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
                    path,
                    widthPointer, heightPointer,
                    comp,
                    4
            );
            if (image == null) {
                throw new RuntimeException("Failed to load a texture file: " + path + "\n" + STBImage.stbi_failure_reason());
            }
            this.width = widthPointer.get(0);
            this.height = heightPointer.get(0);

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
            STBImage.stbi_image_free(image);

            this.textureId = textureId;
        }
    }
}
