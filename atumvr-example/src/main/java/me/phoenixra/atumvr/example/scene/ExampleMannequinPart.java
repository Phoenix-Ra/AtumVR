package me.phoenixra.atumvr.example.scene;

import lombok.Getter;
import me.phoenixra.atumvr.api.input.profile.tracker.ViveTrackerRole;
import me.phoenixra.atumvr.core.input.device.XRDeviceTracker;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;


public class ExampleMannequinPart extends ExampleCube {

    private final ExampleVRProvider vrProvider;

    @Getter
    private final ViveTrackerRole role;

    private final Vector3f worldOffset;

    @Nullable
    private final XRDeviceTracker tracker;

    public ExampleMannequinPart(ExampleVRProvider vrProvider,
                                ViveTrackerRole role,
                                StbTexture texture,
                                Vector3f worldOffset) {
        super(texture,
                new Vector3f(0f, 0f, 0f),
                defaultScaleFor(role),
                new Vector3f(0f, 0f, 0f));
        this.vrProvider = vrProvider;
        this.role = role;
        this.worldOffset = worldOffset;
        this.tracker = vrProvider.getInputHandler()
                .getDevice(role.getDeviceId(), XRDeviceTracker.class);
    }

    public boolean isTrackerActive() {
        return tracker != null && tracker.isActive();
    }

    @Override
    protected Matrix4f getModelMatrix() {
        if (tracker == null) {
            return new Matrix4f().translate(0f, -1000f, 0f);
        }

        Matrix4fc trackerPose = tracker.getPose().matrix();

        Matrix4f local = new Matrix4f()
                .translate(position)
                .rotateXYZ(rotation.x, rotation.y, rotation.z)
                .scale(scale);

        // worldOffset * trackerPose * local
        return new Matrix4f()
                .translate(worldOffset)
                .mul(trackerPose)
                .mul(local);
    }

    public static Vector3f defaultScaleFor(ViveTrackerRole role) {
        return switch (role) {
            case WAIST -> new Vector3f(0.32f, 0.20f, 0.24f);
            case CHEST -> new Vector3f(0.36f, 0.30f, 0.24f);
            case LEFT_FOOT, RIGHT_FOOT -> new Vector3f(0.12f, 0.10f, 0.30f);
            case LEFT_KNEE, RIGHT_KNEE -> new Vector3f(0.16f, 0.22f, 0.16f);
            case LEFT_ELBOW, RIGHT_ELBOW -> new Vector3f(0.14f, 0.20f, 0.14f);
            case LEFT_SHOULDER, RIGHT_SHOULDER -> new Vector3f(0.16f, 0.16f, 0.16f);
            case LEFT_WRIST, RIGHT_WRIST -> new Vector3f(0.10f, 0.10f, 0.18f);
            case LEFT_ANKLE, RIGHT_ANKLE -> new Vector3f(0.12f, 0.12f, 0.22f);
            case KEYBOARD -> new Vector3f(0.40f, 0.05f, 0.15f);
            case CAMERA -> new Vector3f(0.10f, 0.08f, 0.12f);
            case HANDHELD_OBJECT -> new Vector3f(0.10f, 0.10f, 0.10f);
        };
    }
}
