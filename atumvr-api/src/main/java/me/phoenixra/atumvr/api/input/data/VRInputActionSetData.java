package me.phoenixra.atumvr.api.input.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class VRInputActionSetData {
    private String name;
    private String localizedName;
    private String usage;
    private boolean advanced;
}
