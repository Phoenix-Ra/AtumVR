package me.phoenixra.atumvr.example.scene;

import me.phoenixra.atumconfig.api.utils.FileUtils;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.VRShaderProgram;
import me.phoenixra.atumvr.api.scene.EyeType;
import me.phoenixra.atumvr.api.scene.impl.BaseVRSceneRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class ExampleSceneRenderer extends BaseVRSceneRenderer {

    private VRShaderProgram shaderProgram;

    private float[] vertices = {
            -0.25f, -0.25f, 0.0f, // Bottom left
            0.25f, -0.25f, 0.0f, // Bottom right
            0.25f,  0.25f, 0.0f, // Top right

            // Second triangle
            0.25f,  0.25f, 0.0f, // Top right
            -0.25f,  0.25f, 0.0f, // Top left
            -0.25f, -0.25f, 0.0f  // Bottom left
    };
    private int vertexArraysLeftId;
    private int vertexBufferLeftId;

    private int vertexArraysRightId;
    private int vertexBufferRightId;
    public ExampleSceneRenderer(@NotNull VRApp vrApp) {
        super(vrApp);
    }

    @Override
    public void onInit() {
        initShaders();

        vertexArraysLeftId = GL30.glGenVertexArrays();
        vertexBufferLeftId = GL30.glGenBuffers();

        vertexArraysRightId = GL30.glGenVertexArrays();
        vertexBufferRightId = GL30.glGenBuffers();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,
                getFrameBufferLeftEye().getFrameBufferId()
        );
        initFrameBuffer(vertexArraysLeftId,vertexBufferLeftId);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,
                getFrameBufferRightEye().getFrameBufferId()
        );
        initFrameBuffer(vertexArraysRightId,vertexBufferRightId);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        System.out.println("Successfully attached vertices to frame buffer");
    }

    @Override
    public void updateEyeTexture(@NotNull EyeType eyeType) {
        shaderProgram.useShader();
        int timerLocation = shaderProgram.getShaderVariableLocation("timer");
        GL30.glUniform1f(
                timerLocation,
                GL30.glGetUniformf(
                        shaderProgram.getShaderProgramId(),
                        timerLocation
                )+0.01f
        );
        GL30.glBindVertexArray(eyeType == EyeType.LEFT ?
                vertexArraysLeftId : vertexArraysRightId
        );
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 6);
        GL30.glBindVertexArray(0);
        GL30.glUseProgram(0);
    }


    private void initFrameBuffer(int verticesId, int vertexBufferId){
        GL30.glBindVertexArray(verticesId);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBufferId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(0,
                3,
                GL30.GL_FLOAT,
                false,
                3 * Float.BYTES,
                0
        );
        GL30.glEnableVertexAttribArray(0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }
    private void initShaders(){
        shaderProgram = new VRShaderProgram(getVrApp());
        shaderProgram.bindVertexShader("vertex.fsh");
        shaderProgram.bindFragmentShader("fragment.fsh");
        shaderProgram.finishShader();

        shaderProgram.createShaderVariable("timer");
        shaderProgram.createShaderVariable("resolution");

        shaderProgram.useShader();
        GL30.glUniform1f(
                shaderProgram.getShaderVariableLocation("timer"),
                0
        );
        GL30.glUniform3f(
                shaderProgram.getShaderVariableLocation("resolution"),
                getResolutionWidth(),
                getResolutionHeight(),
                0
        );
        GL30.glUseProgram(0);
    }


}
