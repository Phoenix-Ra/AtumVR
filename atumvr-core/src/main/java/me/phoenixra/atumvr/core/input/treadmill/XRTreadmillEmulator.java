package me.phoenixra.atumvr.core.input.treadmill;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector2fc;


public class XRTreadmillEmulator implements XRTreadmillProvider {

    @Getter @Setter
    private boolean active = true;

    private final Vector2f motion = new Vector2f();

    @Getter
    private float speed;

    private final Quaternionf bodyOrientation = new Quaternionf();
    private boolean hasOrientation;

    private float sensorYaw;
    private float yawOffset;


    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public @NotNull Vector2fc getMotion() {
        return motion;
    }

    @Override
    public @Nullable Quaternionfc getBodyOrientation() {
        return hasOrientation ? bodyOrientation : null;
    }

    @Override
    public void recenter(float playSpaceYawRadians) {
        yawOffset = playSpaceYawRadians - sensorYaw;
        refreshOrientation();
    }


    public void setGait(float strafeRight, float forward, float speedMps) {
        motion.set(strafeRight, forward);
        if (motion.lengthSquared() == 0f || speedMps <= 0f) {
            motion.zero();
            speed = 0f;
            return;
        }
        motion.normalize();
        speed = speedMps;
    }

    public void setIdle() {
        setGait(0f, 0f, 0f);
    }


    public void setSensorYaw(float yawRadians) {
        sensorYaw = yawRadians;
        hasOrientation = true;
        refreshOrientation();
    }


    public void clearBodyOrientation() {
        hasOrientation = false;
    }

    private void refreshOrientation() {
        bodyOrientation.identity().rotateY(sensorYaw + yawOffset);
    }
}
