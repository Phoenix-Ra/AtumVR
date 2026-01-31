package me.phoenixra.atumvr.api;

/**
 * Handles VR session and holds data related.
 * <p>
 *     It is meant to initialize VR session and
 *     be an access point for most of low-level stuff
 * </p>
 * <p>
 *     The interface itself does not provide access to VR session data and operations,
 *     its only goal is to define this element in library structure
 * </p>
 */
public interface VRSession {

    void init();

    void destroy();

}
