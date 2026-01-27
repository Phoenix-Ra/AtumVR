package me.phoenixra.atumvr.example.scene;

import me.phoenixra.atumvr.api.enums.ControllerType;
import me.phoenixra.atumvr.api.input.device.VRDeviceController;
import me.phoenixra.atumvr.core.input.device.XRDeviceController;
import me.phoenixra.atumvr.example.ExampleVRProvider;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public class ExampleCubeHand extends ExampleCube{
    private final ExampleVRProvider vrProvider;
    public ExampleCubeHand(ExampleVRProvider vrProvider,
                           StbTexture texture,
                           Vector3f position,
                           Vector3f scale,
                           Vector3f rotation) {
        super(texture, position, scale, rotation);
        this.vrProvider = vrProvider;

    }
    protected Matrix4f getModelMatrix(){
        ControllerType type = vrProvider.getInputHandler().getScaleHand().asType();

        Vector3f scale = this.scale;

        var profileSet = vrProvider.getInputHandler().getProfileSetHolder()
                .getActiveProfile();
        if(profileSet != null){
            if(profileSet.getTriggerValue()
                    .getHandSubaction(type).isPressed()){
                scale = scale.mul(0.2f, new Vector3f());
            }
        }

        // 1) grab the controller’s world‐space pose
        Matrix4fc handPose = vrProvider.getInputHandler()
                .getDevice(VRDeviceController.getId(type))
                .getPose().matrix();

        // 2) build a local transform: translate→rotate→scale
        Matrix4f local = new Matrix4f()
                .translate(position)              // your offset from the hand origin
                .rotateXYZ(rotation.x, rotation.y, rotation.z) // your extra orientation
                .scale(scale);                    // your size

        // 3) combine them: handPose * local
        //    (i.e. apply your offset _in_ the controller’s coordinate frame)
        return new Matrix4f(handPose).mul(local);
    }
}
