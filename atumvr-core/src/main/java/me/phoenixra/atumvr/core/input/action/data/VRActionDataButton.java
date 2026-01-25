package me.phoenixra.atumvr.core.input.action.data;


/**
 * VR action data for buttons
 */
public interface VRActionDataButton extends VRActionData{


    /**
     * If button is pressed
     *
     * @return true/false
     */
    boolean isPressed();

    /**
     * If button is changed
     *
     * @return true/false
     */
    boolean isButtonChanged();

    /**
     * Get button last change time
     *
     * <p>
     *     The value retrieved from openXR or locally in milliseconds(for float type button)
     * </p>
     *
     * @return change time
     */
    long getButtonLastChangeTime();

}
