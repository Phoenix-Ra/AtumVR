package me.phoenixra.atumvr.api.input;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class VRInputActionSet {
    private String name;
    private String localizedName;
    private String usage;
    private boolean advanced;
}
