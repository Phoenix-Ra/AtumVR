package me.phoenixra.atumvr.api.overlays.variable;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;


@Getter
public class OverlayVariableNullable<T> {
    @Nullable
    private T variable;

    @Setter
    private boolean updated;

    public OverlayVariableNullable(@Nullable T variable){
        this.variable = variable;
    }


    public void setVariable(@Nullable T variable, boolean update){
        this.variable = variable;
        if(update) {
            updated = true;
        }
    }

}
