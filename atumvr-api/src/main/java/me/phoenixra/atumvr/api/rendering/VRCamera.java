package me.phoenixra.atumvr.api.rendering;

import me.phoenixra.atumvr.api.enums.EyeType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

//@TODO make for both eyes and rename to smth better, since main thing here is the 2 eyes
public interface VRCamera {

    @NotNull Vector3f getLocation();

    @NotNull Quaternionf getRotation();


    @NotNull Matrix4f getViewMatrix();

    @NotNull Matrix4f getProjectionMatrix();



    void setLocation(Vector3f value);

    void setRotation(Quaternionf value);


    void updateViewMatrix(EyeType eyeType);

    void updateProjectionMatrix(EyeType eyeType,
                                float nearClip,
                                float farClip);
}
