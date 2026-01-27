package me.phoenixra.atumvr.api.input.action;

import lombok.Data;
import me.phoenixra.atumvr.api.enums.ControllerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * VR Action identifier
 */
@Data
public class VRActionIdentifier {

    /**The id value*/
    private final String value;

    /**The controller type the action is attached to or null if not attached*/
    @Nullable
    private final ControllerType controllerType;

    public VRActionIdentifier(@NotNull String actionId,
                              @NotNull ControllerType controllerType){
        this.value = actionId;
        this.controllerType = controllerType;
    }

    public VRActionIdentifier(@NotNull String actionId){
        this.value = actionId;
        this.controllerType = null;
    }

    /**
     * If action is attached to right controller
     *
     * @return true/false
     */
    public boolean isRight(){
        return controllerType == ControllerType.RIGHT;
    }

    /**
     * If action is attached to left controller
     *
     * @return true/false
     */
    public boolean isLeft(){
        return controllerType == ControllerType.LEFT;
    }



}
