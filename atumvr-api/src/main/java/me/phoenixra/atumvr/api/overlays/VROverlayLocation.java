package me.phoenixra.atumvr.api.overlays;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.api.misc.VRLocation;
import org.joml.Quaternionf;
import org.lwjgl.openvr.HmdMatrix34;

public class VROverlayLocation extends VRLocation {

    @Getter @Setter
    private VROverlay vrOverlay;
    public VROverlayLocation(Quaternionf quaternionf, float offsetX, float offsetY, float offsetZ) {
        super(quaternionf, offsetX, offsetY, offsetZ);
    }

    public VROverlayLocation(float scalar, float axisX, float axisY, float axisZ, float posX, float posY, float posZ) {
        super(scalar, axisX, axisY, axisZ, posX, posY, posZ);
    }

    public VROverlayLocation(float yaw, float pitch, float roll, float posX, float posY, float posZ) {
        super(yaw, pitch, roll, posX, posY, posZ);
    }

    public VROverlayLocation(HmdMatrix34 matrix) {
        super(matrix);
    }

    @Override
    public void setFromVrMatrix(HmdMatrix34 matrix) {
        super.setFromVrMatrix(matrix);
        if(vrOverlay != null){
            vrOverlay.getOverlayPosition().setUpdated(true);
        }
    }

    @Override
    public void setRotation(Quaternionf rotation) {
        super.setRotation(rotation);
        if(vrOverlay != null){
            vrOverlay.getOverlayPosition().setUpdated(true);
        }
    }

    @Override
    public void setRotationFromEuler(float yaw, float pitch, float roll) {
        super.setRotationFromEuler(yaw, pitch, roll);
        if(vrOverlay != null){
            vrOverlay.getOverlayPosition().setUpdated(true);
        }
    }

    @Override
    public void setPosX(float posX) {
        super.setPosX(posX);
        if(vrOverlay != null){
            vrOverlay.getOverlayPosition().setUpdated(true);
        }
    }

    @Override
    public void setPosY(float posY) {
        super.setPosY(posY);
        if(vrOverlay != null){
            vrOverlay.getOverlayPosition().setUpdated(true);
        }
    }

    @Override
    public void setPosZ(float posZ) {
        super.setPosZ(posZ);
        if(vrOverlay != null){
            vrOverlay.getOverlayPosition().setUpdated(true);
        }
    }
}
