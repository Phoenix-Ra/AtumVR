package me.phoenixra.atumvr.example.scene;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandJoint;
import me.phoenixra.atumvr.api.input.body.hand.AtumVRHandView;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.joml.Matrix4f;
import org.joml.Vector3f;


public class ExampleHandJointPart extends ExampleCube {

    private static final float FALLBACK_SIZE = 0.012f;

    private final ExampleVRProvider vrProvider;

    @Getter
    private final ControllerType side;
    @Getter
    private final AtumVRHandJoint joint;

    public ExampleHandJointPart(ExampleVRProvider vrProvider,
                                ControllerType side,
                                AtumVRHandJoint joint,
                                StbTexture texture) {
        super(texture,
                new Vector3f(0f, 0f, 0f),
                new Vector3f(FALLBACK_SIZE, FALLBACK_SIZE, FALLBACK_SIZE),
                new Vector3f(0f, 0f, 0f));
        this.vrProvider = vrProvider;
        this.side = side;
        this.joint = joint;
    }

    public boolean isJointTracked() {
        return getHand().isJointTracked(joint);
    }

    @Override
    protected Matrix4f getModelMatrix() {
        AtumVRHandView hand = getHand();
        AtumVRPose jointPose = hand.getJointPose(joint);
        if (jointPose == null) {
            return new Matrix4f().translate(0f, -1000f, 0f);
        }

        float radius = hand.getJointRadius(joint);
        float size = radius > 0f ? radius * 2f : FALLBACK_SIZE;

        // rendered at the real hand location, no world offset
        return new Matrix4f(jointPose.matrix()).scale(size);
    }

    private AtumVRHandView getHand() {
        return vrProvider.getInputHandler().getVRHands().getHand(side);
    }
}
