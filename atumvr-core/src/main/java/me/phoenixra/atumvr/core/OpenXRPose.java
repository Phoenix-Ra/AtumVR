package me.phoenixra.atumvr.core;


import lombok.Getter;


import me.phoenixra.atumvr.api.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import org.joml.Vector3f;


@Getter
public class OpenXRPose {

    private Vector3f position;

    private Vector3f direction;

    private Matrix4f poseMatrix;
    private Matrix4f rotationMatrix;
    private Matrix4f rotationMatrixInverted;

    private float yaw, pitch, roll;


    private Vector3f velocity;

    private Vector3f angularVelocity;


    private Vector3f originCached;


    public OpenXRPose() {
        position = new Vector3f(0,0,0);
        direction = new Vector3f(0,0,0);
        poseMatrix = new Matrix4f();
        rotationMatrix = new Matrix4f();
        rotationMatrixInverted = new Matrix4f();

        velocity = new Vector3f(0,0,0);
        angularVelocity = new Vector3f(0,0,0);
        originCached = new Vector3f(0,0,0);
    }

    public OpenXRPose(Vector3f origin,
                      float worldScale,
                      Matrix4f rotationMatrix,
                      Vector3f position, Vector3f direction,
                      Vector3f velocity, Vector3f angularVelocity
    ) {
        update(
                origin,
                worldScale,
                rotationMatrix,
                position,
                direction,
                velocity,angularVelocity
        );
    }




    protected void update(Vector3f origin,
                          float worldScale,
                          Matrix4f poseMatrix,
                          Vector3f position, Vector3f direction,
                          Vector3f velocity, Vector3f angularVelocity){
        this.originCached = origin;

        this.poseMatrix = poseMatrix;
        this.rotationMatrix = new Matrix4f().mul(
                MathUtils.extractRotationFromPose(poseMatrix)
                        .transpose().transpose() //@TODO does it even do something?
        );
        this.rotationMatrixInverted = this.rotationMatrix.invert(new Matrix4f());


        this.position = position
                .mul(worldScale, new Vector3f()) //to not override received position
                .add(origin.x, origin.y, origin.z);

        this.direction = new Vector3f(direction.x, direction.y, direction.z);

        this.yaw =  (float) Math.toDegrees(
                MathUtils.fastAtan2(-this.direction.x, this.direction.z)
        );
        this.pitch = (float) Math.toDegrees(
                Math.asin(this.direction.y / this.direction.length())
        );
        this.roll = (float) (
                - Math.toDegrees(MathUtils.fastAtan2(
                        this.rotationMatrix.get(1,0),
                        this.rotationMatrix.get(1,1
                        )
                ))
        );

        this.velocity = new Vector3f(velocity.x, velocity.y, velocity.z);
        this.angularVelocity = new Vector3f(angularVelocity.x, angularVelocity.y, angularVelocity.z);

    }




    protected void onOriginChanged(Vector3f origin){
        this.position.add(
                        origin.x - originCached.x,
                        origin.y - originCached.y,
                        origin.z - originCached.z
                );
    }


    public @NotNull Vector3f getCustomVector(@NotNull Vector3f vec) {
        return new Vector3f(
                vec.x,
                vec.y,
                vec.z
        ).mulDirection(rotationMatrix);
    }

    public @NotNull Vector3f reverseCustomVector(@NotNull Vector3f customVec) {
        return new Vector3f(
                customVec.x,
                customVec.y,
                customVec.z
        ).mulDirection(rotationMatrixInverted);
    }


    @Override
    public String toString() {
        return String.format(
                "VRDevicePose [position=%s, direction=%s, velocity=%s, angularVelocity=%s, yaw=%.2f°, pitch=%.2f°, roll=%.2f°]",
                getPosition(), getDirection(), velocity, angularVelocity, yaw, pitch, roll
        );
    }


}
