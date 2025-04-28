package me.phoenixra.atumvr.api.input.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class InputOriginDeviceInfo {
    /**
     * The value of the {@code devicePath} field.
     **/
    private long devicePath;
    /**
     * The value of the {@code trackedDeviceIndex} field.
     **/
    private int deviceIndex;
    /**
     * The null-terminated string stored
     * in the {@code rchRenderModelComponentName} field.
     **/
    private String renderModelComponentName;

}
