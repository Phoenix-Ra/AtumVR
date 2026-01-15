package me.phoenixra.atumvr.api.input.action;

import lombok.Getter;
import me.phoenixra.atumvr.api.enums.ControllerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class ActionIdentifier {
    private final String value;
    @Nullable
    private final ControllerType controllerType;

    public ActionIdentifier(@NotNull String actionId,
                            @NotNull ControllerType controllerType){
        this.value = actionId;
        this.controllerType = controllerType;
    }
    public ActionIdentifier(String actionId){
        this.value = actionId;
        this.controllerType = null;
    }

    public boolean isRight(){
        return controllerType == ControllerType.RIGHT;
    }
    public boolean isLeft(){
        return controllerType == ControllerType.LEFT;
    }

}
