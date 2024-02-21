package me.phoenixra.atumvr.api.overlays.variable;


import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;



@Getter
public class OverlayVariableNotNull<T> {
    @NotNull
    private T variable;

    @Setter
    private boolean updated;

    public OverlayVariableNotNull(@NotNull T variable){
        this.variable = variable;
    }


    public void setVariable(@NotNull T variable, boolean update){
        this.variable = variable;
        if(update) {
            updated = true;
        }
    }

}
