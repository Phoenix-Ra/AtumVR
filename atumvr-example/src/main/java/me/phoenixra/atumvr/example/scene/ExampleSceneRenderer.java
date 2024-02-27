package me.phoenixra.atumvr.example.scene;


import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.rendering.VRShaderProgram;
import me.phoenixra.atumvr.api.scene.EyeType;
import me.phoenixra.atumvr.api.scene.impl.BaseVRSceneRenderer;
import me.phoenixra.atumvr.api.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;


public class ExampleSceneRenderer extends BaseVRSceneRenderer {

    private VRShaderProgram shaderProgram;

    private ExampleCube exampleCube;
    private float timer;
    public ExampleSceneRenderer(@NotNull VRApp vrApp) {
        super(vrApp);
    }

    @Override
    public void onInit() {
        initShaders();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,
                0
        );
        exampleCube = new ExampleCube(
                new Vector3f(-1f,1f,-1.5f),
                new Vector3f(1f,1f,1f),
                new Vector3f(20f,40f,0f)
        );
        exampleCube.init();

        System.out.println("Successfully attached vertices to frame buffer");
    }

    @Override
    public void updateEyeTexture(@NotNull EyeType eyeType) {
        timer+=0.0005f;
        shaderProgram.useShader();
        updateShaderVariables(eyeType, exampleCube.getModelMatrix());

        exampleCube.getRotation().set(
                timer*360,
                timer*360,
                0
        );
        exampleCube.draw();
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
        shaderProgram = new VRShaderProgram(getVrApp());
        shaderProgram.bindVertexShader("vertex.fsh");
        shaderProgram.bindFragmentShader("fragment.fsh");
        shaderProgram.finishShader();

        shaderProgram.createShaderVariable("uMVP");
        shaderProgram.createShaderVariable("iTimer");
        shaderProgram.createShaderVariable("iResolution");

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
                getResolutionWidth(),
                getResolutionHeight(),
                0
        );
        GL30.glUseProgram(0);
    }


}
