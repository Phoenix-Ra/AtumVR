package me.phoenixra.atumvr.example.scene;

import me.phoenixra.atumvr.core.OpenXRProvider;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ExampleCubeHand extends ExampleCube{
    private final OpenXRProvider provider;
    public ExampleCubeHand(OpenXRProvider provider,
                           StbTexture texture,
                           Vector3f position,
                           Vector3f scale,
                           Vector3f rotation) {
        super(texture, position, scale, rotation);
        this.provider = provider;

    }
    protected Matrix4f getModelMatrix(){
        return provider.getInputHandler().getLeftController().getGripPose();
    }
}
