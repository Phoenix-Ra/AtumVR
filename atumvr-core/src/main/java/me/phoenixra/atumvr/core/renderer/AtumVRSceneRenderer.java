package me.phoenixra.atumvr.core.renderer;

import lombok.Getter;
import me.phoenixra.atumconfig.api.utils.FileUtils;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.VRSceneRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static org.lwjgl.openvr.VR.EVRSubmitFlags_Submit_Default;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_PostPresentHandoff;
import static org.lwjgl.openvr.VRCompositor.VRCompositor_Submit;

public class AtumVRSceneRenderer implements VRSceneRenderer {
    @Getter
    private VRApp vrApp;

    private Texture textureRight;
    private Texture textureLeft;

    @Getter
    private int textureIdRightEye;
    @Getter
    private int textureIdLeftEye;

    public AtumVRSceneRenderer(VRApp vrApp){
        this.vrApp = vrApp;
    }

    @Override
    public void init() {
        initShaders();
       /* textureIdLeftEye = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureIdLeftEye);
        GL30.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL30.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, lwidth, lheight, 0, GL11.GL_RGBA, GL11.GL_INT, null);
        RenderSystem.bindTexture(i);


        textureRight = Texture.calloc();
        textureLeft = Texture.calloc();

        textureRight.eColorSpace(VR.EColorSpace_ColorSpace_Gamma);
        textureRight.eType(VR.ETextureType_TextureType_OpenGL);
        textureRight.handle(-1);

        textureLeft.eColorSpace(VR.EColorSpace_ColorSpace_Gamma);
        textureLeft.eType(VR.ETextureType_TextureType_OpenGL);
        textureLeft.handle(-1);*/
    }

    @Override
    public void updateFrame() {

    }


    private void initShaders(){
        try {
            //load from resource
            for (String path : FileUtils.getAllPathsInResourceFolder(getVrApp().getVrCore(), "shaders")) {
                try {
                    File file = new File(getVrApp().getVrCore().getDataFolder(), path);
                    if (!file.getName().contains(".")) {
                        file.mkdir();
                        continue;
                    }
                    InputStream stream = getVrApp().getVrCore().getClass().getResourceAsStream(path);
                    getVrApp().getVrCore().logInfo("Loading default shader file " + path);
                    if (stream == null) continue;
                    Files.copy(stream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    getVrApp().getVrCore().logError(Arrays.toString(e.getStackTrace()));
                }
            }

            String vert = new String(Files.readAllBytes(Paths.get(
                    getVrApp().getVrCore().getDataFolder().getPath() + "/shaders/vertex.fsh")
            ));
            String frag = new String(Files.readAllBytes(Paths.get(
                    getVrApp().getVrCore().getDataFolder().getPath() + "/shaders/fragment.fsh")
            ));

            getVrApp().getVrCore().logInfo("1");
            int program = GL30.glCreateProgram();
            getVrApp().getVrCore().logInfo("2");
            int vertID = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
            getVrApp().getVrCore().logInfo("3");
            int fragID = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);

            getVrApp().getVrCore().logInfo("4");
            GL30.glShaderSource(vertID, vert);
            getVrApp().getVrCore().logInfo("5");
            GL30.glShaderSource(fragID, frag);

            getVrApp().getVrCore().logInfo("6");
            GL30.glCompileShader(vertID);
            getVrApp().getVrCore().logInfo("7");
            if (GL30.glGetShaderi(vertID, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
                System.err.println("Failed to compile vertex shader!");
                System.err.println(GL30.glGetShaderInfoLog(vertID));
                return;
            }

            getVrApp().getVrCore().logInfo("8");
            GL30.glCompileShader(fragID);
            getVrApp().getVrCore().logInfo("9");
            if (GL30.glGetShaderi(fragID, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
                System.err.println("Failed to compile fragment shader!");
                System.err.println(GL30.glGetShaderInfoLog(fragID));
                return;
            }

            getVrApp().getVrCore().logInfo("10");
            GL30.glAttachShader(program, vertID);
            getVrApp().getVrCore().logInfo("11");
            GL30.glAttachShader(program, fragID);

            getVrApp().getVrCore().logInfo("12");
            GL30.glLinkProgram(program);
            getVrApp().getVrCore().logInfo("13");
            GL30.glValidateProgram(program);

            System.out.println("Successfully created shader program");
        }catch (Throwable throwable){
            throw new RuntimeException(throwable);
        }
    }

    private void renderEye(int eye) {
      /*  HmdMatrix44 projectionMatrix = VRSystem.VRSystem_GetProjectionMatrix(eye, nearClip, farClip);
        HmdMatrix34 eyePose = VRSystem.VRSystem_GetEyeToHeadTransform(eye);*/
    }

    public void endFrame() {
        if (OpenVR.VRCompositor.Submit != 0) {
            int i = VRCompositor_Submit(0, textureLeft, null, EVRSubmitFlags_Submit_Default);
            int j = VRCompositor_Submit(1, textureRight, null, EVRSubmitFlags_Submit_Default);
            VRCompositor_PostPresentHandoff();

            if (i + j > 0) {
                throw new RuntimeException("Compositor Error: Texture submission error");
            }
        }
    }
}
