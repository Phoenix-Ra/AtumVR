package me.phoenixra.atumvr.example.scene;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.joml.Matrix4f;
import org.joml.Vector3f;


public class ExampleMannequinPart extends ExampleCube {

    private final ExampleVRProvider vrProvider;

    @Getter
    private final AtumVRBodyJoint joint;

    private final Vector3f worldOffset;

    public ExampleMannequinPart(ExampleVRProvider vrProvider,
                                AtumVRBodyJoint joint,
                                StbTexture texture,
                                Vector3f worldOffset) {
        super(texture,
                new Vector3f(0f, 0f, 0f),
                defaultScaleFor(joint),
                new Vector3f(0f, 0f, 0f));
        this.vrProvider = vrProvider;
        this.joint = joint;
        this.worldOffset = worldOffset;
    }

    public boolean isJointTracked() {
        return getBody().isJointTracked(joint);
    }

    @Override
    protected Matrix4f getModelMatrix() {
        AtumVRPose jointPose = getBody().getJointPose(joint);
        if (jointPose == null) {
            return new Matrix4f().translate(0f, -1000f, 0f);
        }

        Matrix4f local = new Matrix4f()
                .translate(position)
                .rotateXYZ(rotation.x, rotation.y, rotation.z)
                .scale(scale);

        // worldOffset * jointPose * local
        return new Matrix4f()
                .translate(worldOffset)
                .mul(jointPose.matrix())
                .mul(local);
    }

    private AtumVRBodyView getBody() {
        return vrProvider.getInputHandler().getVRBody();
    }

    public static Vector3f defaultScaleFor(AtumVRBodyJoint joint) {
        return switch (joint) {
            case HEAD -> new Vector3f(0.20f, 0.24f, 0.22f);
            case NECK -> new Vector3f(0.10f, 0.10f, 0.10f);
            case CHEST -> new Vector3f(0.36f, 0.30f, 0.24f);
            case WAIST -> new Vector3f(0.32f, 0.20f, 0.24f);
            case LEFT_SHOULDER, RIGHT_SHOULDER -> new Vector3f(0.16f, 0.16f, 0.16f);
            case LEFT_ELBOW, RIGHT_ELBOW -> new Vector3f(0.14f, 0.20f, 0.14f);
            case LEFT_WRIST, RIGHT_WRIST -> new Vector3f(0.10f, 0.10f, 0.18f);
            case LEFT_HAND, RIGHT_HAND -> new Vector3f(0.10f, 0.14f, 0.16f);
            case LEFT_HIP, RIGHT_HIP -> new Vector3f(0.14f, 0.14f, 0.16f);
            case LEFT_KNEE, RIGHT_KNEE -> new Vector3f(0.16f, 0.22f, 0.16f);
            case LEFT_ANKLE, RIGHT_ANKLE -> new Vector3f(0.12f, 0.12f, 0.22f);
            case LEFT_FOOT, RIGHT_FOOT -> new Vector3f(0.12f, 0.10f, 0.30f);
        };
    }
}
