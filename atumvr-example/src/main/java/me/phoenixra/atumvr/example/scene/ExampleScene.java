package me.phoenixra.atumvr.example.scene;

import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.enums.EyeType;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandJoint;
import me.phoenixra.atumvr.core.rendering.XRRenderer;
import me.phoenixra.atumvr.core.rendering.XRScene;
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

public class ExampleScene extends XRScene {

    private VRShaderProgram shaderProgram;

    private List<ExampleCube> exampleCubes = new ArrayList<>();
    private ExampleCube floorCube;

    private final List<ExampleMannequinPart> mannequinParts = new ArrayList<>();
    private final Vector3f mannequinOffset = new Vector3f(0f, 0f, 2.5f);
    private int lastTrackedJointCount = -1;

    private long lastTreadmillTime = Long.MIN_VALUE;
    private final Vector3f cachedTreadmillStep = new Vector3f();

    private final List<ExampleHandJointPart> handParts = new ArrayList<>();
    private int lastTrackedHandJointCount = -1;

    private float timer;
    public ExampleScene(@NotNull XRRenderer vrRenderer) {
        super(vrRenderer);
    }

    @Override
    public void onInit() {
        initShaders();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,
                0
        );
        floorCube =  new ExampleCube(
                new StbTexture("textures/test.png"),
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
                        getProvider(),
                        new StbTexture("textures/test.png"),
                        new Vector3f(0f,0f,0f),
                        new Vector3f(0.3f,0.3f,0.3f),
                        new Vector3f(0f,0f,0f)
                )
        );
        for(ExampleCube cube : exampleCubes){
            cube.init();
        }

        initMannequin();
        initHands();

        System.out.println("Successfully attached vertices to frame buffer");
    }


    private void initMannequin() {
        StbTexture mannequinTexture = new StbTexture("textures/test.png");
        // a part per joint, whichever ones the body ends up delivering
        for (AtumVRBodyJoint joint : AtumVRBodyJoint.values()) {
            ExampleMannequinPart part = new ExampleMannequinPart(
                    getProvider(),
                    joint,
                    mannequinTexture,
                    mannequinOffset
            );
            part.init();
            mannequinParts.add(part);
        }
        getProvider().getLogger().logInfo(
                "Mannequin initialized with " + mannequinParts.size() + " body part(s)"
        );
    }

    private void initHands() {
        StbTexture handTexture = new StbTexture("textures/test.png");
        for (ControllerType side : ControllerType.values()) {
            for (int i = 0; i < AtumVRHandJoint.COUNT; i++) {
                ExampleHandJointPart part = new ExampleHandJointPart(
                        getProvider(),
                        side,
                        AtumVRHandJoint.fromIndex(i),
                        handTexture
                );
                part.init();
                handParts.add(part);
            }
        }
        getProvider().getLogger().logInfo(
                "Hands initialized with " + handParts.size() + " joint part(s)"
        );
    }

    @Override
    public void renderEyeTexture(@NotNull EyeType eyeType) {
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

        for(ExampleMannequinPart part : mannequinParts){
            if(!part.isJointTracked()){
                continue;
            }
            updateShaderVariables(eyeType, part.getModelMatrix());
            part.render();
        }
        for(ExampleHandJointPart part : handParts){
            if(!part.isJointTracked()){
                continue;
            }
            updateShaderVariables(eyeType, part.getModelMatrix());
            part.render();
        }
        if(eyeType == EyeType.LEFT){
            applyTreadmillWalk();
            logTrackedJointCountIfChanged();
            logTrackedHandJointCountIfChanged();
        }

        GL30.glUseProgram(0);
    }

    private void applyTreadmillWalk(){
        long now = getProvider().getXrDisplayTime();
        float dt = lastTreadmillTime == Long.MIN_VALUE
                ? 0f
                : (now - lastTreadmillTime) * 1.0e-9f;
        lastTreadmillTime = now;
        if(dt <= 0f){
            return;
        }

        var treadmill = getProvider().getInputHandler().getVRTreadmill();
        if(treadmill.getVelocity(cachedTreadmillStep) != null){
            mannequinOffset.add(cachedTreadmillStep.mul(dt));
        }
    }

    private void logTrackedJointCountIfChanged(){
        int trackedNow = getProvider().getInputHandler().getVRBody()
                .getTrackedJoints().size();
        if(trackedNow != lastTrackedJointCount){
            lastTrackedJointCount = trackedNow;
            getProvider().getLogger().logInfo(
                    "Tracked body joints: " + trackedNow + " / " + mannequinParts.size()
            );
        }
    }

    private void logTrackedHandJointCountIfChanged(){
        var hands = getProvider().getInputHandler().getVRHands();
        int trackedNow = hands.getLeftHand().getTrackedJoints().size()
                + hands.getRightHand().getTrackedJoints().size();
        if(trackedNow != lastTrackedHandJointCount){
            lastTrackedHandJointCount = trackedNow;
            getProvider().getLogger().logInfo(
                    "Tracked hand joints: " + trackedNow + " / " + handParts.size()
            );
        }
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
        shaderProgram = new VRShaderProgram(getProvider());
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
                getRenderer().getResolutionWidth(),
                getRenderer().getResolutionHeight(),
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
        for(ExampleMannequinPart part : mannequinParts){
            part.destroy();
        }
        mannequinParts.clear();
        for(ExampleHandJointPart part : handParts){
            part.destroy();
        }
        handParts.clear();
    }

    @Override
    public @NotNull ExampleVRRenderer getRenderer() {
        return (ExampleVRRenderer) super.getRenderer();
    }


    public ExampleVRProvider getProvider() {
        return getRenderer().getVrProvider();
    }
}
