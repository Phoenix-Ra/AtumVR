package me.phoenixra.atumvr.api.exceptions;

/**
 * VRException is the class for VR-related exceptions
 */
public class AtumVRException extends RuntimeException {

    public AtumVRException(String message) {
        super(message);
    }

    public AtumVRException(String message, Throwable origin) {
        super(message, origin);
    }
}
