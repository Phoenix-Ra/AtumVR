package me.phoenixra.atumvr.api.rendering;

import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.VR;

import java.nio.ByteBuffer;
@Getter
public class VRTexture {
    private final int id;
    //private final Texture texture;

    private final int width;
    private final int height;

    public VRTexture(int width, int height, boolean depth){
        this.width = width;
        this.height = height;

        this.id = GL30.glGenTextures();
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

       /* texture = Texture.calloc();

        texture.eColorSpace(VR.EColorSpace_ColorSpace_Gamma);
        texture.eType(VR.ETextureType_TextureType_OpenGL);
        texture.handle(id);*/
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
    }

    public Texture applyDataToTexture(Texture texture){

        texture.eColorSpace(VR.EColorSpace_ColorSpace_Gamma);
        texture.eType(VR.ETextureType_TextureType_OpenGL);
        texture.handle(id);
        return texture;
    }


}
