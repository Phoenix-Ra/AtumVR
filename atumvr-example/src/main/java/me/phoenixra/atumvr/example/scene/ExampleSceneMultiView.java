package me.phoenixra.atumvr.example.scene;


import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.api.rendering.texture.VRShaderProgram;
import me.phoenixra.atumvr.api.scene.MultiViewVRScene;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;


public class ExampleSceneMultiView extends MultiViewVRScene {

    private VRShaderProgram shaderProgram;

    private List<ExampleCube> exampleCubes = new ArrayList<>();
    private ExampleCube floorCube;

    private float timer;
    public ExampleSceneMultiView(@NotNull VRRenderer vrRenderer) {
        super(vrRenderer);
    }

    @Override
    public void onInit() {
        initShaders();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,
                0
        );
        floorCube =  new ExampleCube(
                new StbTexture("textures/test1.png"),
                new Vector3f(0f,0f,0f),
                new Vector3f(2f,1f,2f),
                new Vector3f(0f,0f,0f)
        );
        floorCube.init();
        exampleCubes.add(
                new ExampleCube(
                        new StbTexture("textures/test.png"),
                        new Vector3f(-2f,1f,-2.5f),
                        new Vector3f(1f,1f,1f),
                        new Vector3f(0f,0f,0f)
                )
        );
        exampleCubes.add(
                new ExampleCube(
                        new StbTexture("textures/test.png"),
                        new Vector3f(2f,1f,-2.5f),
                        new Vector3f(1f,1f,1f),
                        new Vector3f(0f,0f,0f)
                )
        );
        exampleCubes.add(
                new ExampleCube(
                        new StbTexture("textures/test.png"),
                        new Vector3f(-2f,1f,2.5f),
                        new Vector3f(1f,1f,1f),
                        new Vector3f(0f,0f,0f)
                )
        );
        exampleCubes.add(
                new ExampleCube(
                        new StbTexture("textures/test.png"),
                        new Vector3f(2f,1f,2.5f),
                        new Vector3f(1f,1f,1f),
                        new Vector3f(0f,0f,0f)
                )
        );
        for(ExampleCube cube : exampleCubes){
            cube.init();
        }

        System.out.println("Successfully attached vertices to frame buffer");
    }

    @Override
    public void render() {
        timer+=0.0005f;
        shaderProgram.useShader();

        GL30.glUniform1i(
                shaderProgram.getShaderVariableLocation("uNegative"),
                0
        );


        updateShaderVariables(floorCube.getModelMatrix());
        floorCube.render();

        GL30.glUniform1i(
                shaderProgram.getShaderVariableLocation("uNegative"),
                1
        );
        for(ExampleCube exampleCube : exampleCubes) {
            updateShaderVariables(exampleCube.getModelMatrix());

            exampleCube.render();
        }
        GL30.glUseProgram(0);
    }
    private void updateShaderVariables(Matrix4f modelMatrix){
        int timerLocation = shaderProgram.getShaderVariableLocation("iTimer");
        int mvpLocation = shaderProgram.getShaderVariableLocation("uMVP");
        float timer = GL30.glGetUniformf(
                shaderProgram.getShaderProgramId(),
                timerLocation
        ) + 0.01f;
        GL30.glUniform1f(
                timerLocation,
                timer
        );
        Matrix4f[] projection = new Matrix4f[2];
        projection[0] = getVrCameraLeftEye().getProjectionMatrix();
        projection[1] = getVrCameraRightEye().getProjectionMatrix();
        Matrix4f[] view = new Matrix4f[2];
        view[0] = getVrCameraLeftEye().getViewMatrix();
        view[1] = getVrCameraRightEye().getViewMatrix();

        Matrix4f[] mvpMatrices = new Matrix4f[2];
        mvpMatrices[0] = projection[0]
                .mul(view[0],new Matrix4f())
                .mul(modelMatrix);
        mvpMatrices[1] = projection[1]
                .mul(view[1],new Matrix4f())
                .mul(modelMatrix);
        float[] mvpData = new float[32]; // Two 4x4 matrices, so 16 floats * 2 = 32
        mvpMatrices[0].get(mvpData, 0);  // Left eye MVP starts at index 0
        mvpMatrices[1].get(mvpData, 16);
        GL30.glUniformMatrix4fv(mvpLocation,
                false,
                mvpData
        );
    }
    private void initShaders(){
        shaderProgram = new VRShaderProgram(getVrRenderer().getVrApp());
        shaderProgram.bindVertexShader("vertex.fsh");
        shaderProgram.bindFragmentShader("fragment.fsh");
        shaderProgram.finishShader();

        shaderProgram.createShaderVariable("uMVP");
        shaderProgram.createShaderVariable("iTimer");
        shaderProgram.createShaderVariable("iResolution");
        shaderProgram.createShaderVariable("uNegative");

        shaderProgram.useShader();
        GL30.glUniformMatrix4fv(
                shaderProgram.getShaderVariableLocation("uMVP"),
                false,
                new float[32]
        );
        GL30.glUniform1f(
                shaderProgram.getShaderVariableLocation("iTimer"),
                0
        );
        GL30.glUniform3f(
                shaderProgram.getShaderVariableLocation("iResolution"),
                getVrRenderer().getResolutionWidth(),
                getVrRenderer().getResolutionHeight(),
                0
        );
        GL30.glUniform1i(
                shaderProgram.getShaderVariableLocation("uNegative"),
                0
        );
        GL30.glUseProgram(0);
    }


    @Override
    public void destroy() {
        //release all resources attached to scene
    }
}
