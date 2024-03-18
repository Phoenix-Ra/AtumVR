package me.phoenixra.atumvr.api.exceptions;

import me.phoenixra.atumvr.api.utils.VRUtils;

public class VRInputException extends RuntimeException{
    public VRInputException(String message, int error){
        super(message+" Error id: " + error
                + "  Error Message: " + VRUtils.getInputErrorMessage(error));
    }
}
