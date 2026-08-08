package me.phoenixra.atumvr.api.input.haptics;

import me.phoenixra.atumvr.api.input.haptics.bhaptics.BHaptics;
import org.jetbrains.annotations.Nullable;

/**
 * Body haptics hardware
 * <p>
 *     Vendors diverge too much for a unified effect model,
 *     so this is a bridge to the active vendor api
 * </p>
 */
public interface AtumVRBodyHaptics {

    AtumVRBodyHaptics EMPTY = new AtumVRBodyHaptics() {
        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void stopAll() {
        }
    };


    /**
     * If the vendor hardware is reachable
     * and at least one haptic device is connected
     */
    boolean isActive();

    /**
     * Stop all currently playing haptic effects
     */
    void stopAll();


    /**
     * Get as the bHaptics vendor api
     *
     * @return the vendor api, or null if another vendor is active
     */
    default @Nullable BHaptics asBHaptics() {
        return null;
    }
}
