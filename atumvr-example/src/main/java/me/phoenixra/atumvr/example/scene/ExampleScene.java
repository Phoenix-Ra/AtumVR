package me.phoenixra.atumvr.example.scene;


import me.phoenixra.atumvr.api.rendering.VRRenderer;
import me.phoenixra.atumvr.api.rendering.texture.VRShaderProgram;
import me.phoenixra.atumvr.api.devices.hmd.EyeType;
import me.phoenixra.atumvr.api.scene.SimpleVRScene;
import me.phoenixra.atumvr.api.utils.MathUtils;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;


public class ExampleScene extends SimpleVRScene {

    private VRShaderProgram shaderProgram;

    private List<ExampleCube> exampleCubes = new ArrayList<>();
    private ExampleCube floorCube;

    private float timer;
    public ExampleScene(@NotNull VRRenderer vrRenderer) {
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
    public void updateEyeTexture(@NotNull EyeType eyeType) {
        timer+=0.0005f;
        shaderProgram.useShader();

        GL30.glUniform1i(
                shaderProgram.getShaderVariableLocation("uNegative"),
                0
        );
        updateShaderVariables(eyeType, floorCube.getModelMatrix());
        floorCube.draw();

        GL30.glUniform1i(
                shaderProgram.getShaderVariableLocation("uNegative"),
                1
        );
        for(ExampleCube exampleCube : exampleCubes) {
            updateShaderVariables(eyeType, exampleCube.getModelMatrix());

            exampleCube.getPositionOffset().set(
                    0,
                    MathUtils.fastSin(timer*10),
                    0
            );
            exampleCube.draw();
        }
        GL30.glUseProgram(0);
    }
    private void updateShaderVariables(EyeType eyeType, Matrix4f modelMatrix){
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
        Matrix4f projection = eyeType==EyeType.LEFT ?
                getVrCameraLeftEye().getProjectionMatrix() :
                getVrCameraRightEye().getProjectionMatrix();
        Matrix4f view = eyeType==EyeType.LEFT ?
                getVrCameraLeftEye().getViewMatrix() :
                getVrCameraRightEye().getViewMatrix();
        GL30.glUniformMatrix4fv(mvpLocation,
                true,
                MemoryStack.stackFloats(
                    modelMatrix.mul(view,new Matrix4f()).mul(projection)
                            .get(new float[16])
                )
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
                true,
                new float[16]
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
