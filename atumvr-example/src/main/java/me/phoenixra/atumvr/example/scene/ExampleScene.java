package me.phoenixra.atumvr.example.scene;

import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.core.rendering.OpenXRRenderer;
import me.phoenixra.atumvr.core.rendering.OpenXRScene;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.rendering.ExampleVRRenderer;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

public class ExampleScene extends OpenXRScene {

    private VRShaderProgram shaderProgram;

    private List<ExampleCube> exampleCubes = new ArrayList<>();
    private ExampleCube floorCube;

    private float timer;
    public ExampleScene(@NotNull OpenXRRenderer vrRenderer) {
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
        exampleCubes.add(
                new ExampleCubeHand(
                        getVrProvider(),
                        new StbTexture("textures/test.png"),
                        new Vector3f(0f,0f,0f),
                        new Vector3f(0.3f,0.3f,0.3f),
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
        floorCube.render();

        GL30.glUniform1i(
                shaderProgram.getShaderVariableLocation("uNegative"),
                1
        );
        for(ExampleCube exampleCube : exampleCubes) {
            updateShaderVariables(eyeType, exampleCube.getModelMatrix());

            exampleCube.render();
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
                getLeftEyeCamera().getProjectionMatrix() :
                getRightEyeCamera().getProjectionMatrix();
        Matrix4f view = eyeType==EyeType.LEFT ?
                getLeftEyeCamera().getViewMatrix() :
                getRightEyeCamera().getViewMatrix();
        GL30.glUniformMatrix4fv(mvpLocation,
                false,
                MemoryStack.stackFloats(
                        projection.mul(view,new Matrix4f()).mul(modelMatrix)
                                .get(new float[16])
                )
        );
    }
    private void initShaders(){
        shaderProgram = new VRShaderProgram(getVrProvider());
        shaderProgram.bindVertexShader("vertex.vsh");
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

    @Override
    public @NotNull ExampleVRRenderer getVrRenderer() {
        return (ExampleVRRenderer) super.getVrRenderer();
    }

    @Override
    public ExampleVRProvider getVrProvider() {
        return getVrRenderer().getVrProvider();
    }
}
