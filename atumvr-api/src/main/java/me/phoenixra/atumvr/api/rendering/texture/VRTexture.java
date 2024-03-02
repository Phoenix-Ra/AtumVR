package me.phoenixra.atumvr.api.rendering.texture;

import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.VR;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
@Getter
public class VRTexture {
    protected final int id;
    protected final Texture texture;

    protected final int width;
    protected final int height;

    public VRTexture(int width, int height, boolean depth){
        this.width = width;
        this.height = height;

        this.id = GL30.glGenTextures();
        texture = Texture.calloc();
        init(depth);
    }
    protected void init(boolean depth){
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexImage2D(
                GL30.GL_TEXTURE_2D,
                0,
                depth ? GL30.GL_RGBA8 : GL30.GL_RGBA,
                width,
                height,
                0,
                GL30.GL_RGBA,
                GL30.GL_INT,
                (ByteBuffer) null
        );

        texture.eColorSpace(VR.EColorSpace_ColorSpace_Gamma);
        texture.eType(VR.ETextureType_TextureType_OpenGL);
        texture.handle(id);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
    }

    public Texture applyDataToTexture(Texture texture){
        texture.eColorSpace(VR.EColorSpace_ColorSpace_Gamma);
        texture.eType(VR.ETextureType_TextureType_OpenGL);
        texture.handle(id);
        return texture;
    }


    public void destroy(){
        texture.free();
        GL30.glDeleteTextures(id);

    }
}
