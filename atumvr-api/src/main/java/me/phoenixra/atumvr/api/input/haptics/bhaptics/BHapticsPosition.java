package me.phoenixra.atumvr.api.input.haptics.bhaptics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Device positions of the bHaptics ecosystem,
 * keys match the bHaptics Player wire protocol
 */
public enum BHapticsPosition {

    /**
     * Whole vest, 40 motor indexes (0-19 front, 20-39 back).
     * The Player reports a connected vest under this position
     */
    VEST("Vest"),
    /**
     * Front vest panel, 20 motor indexes in a 4x5 grid
     */
    VEST_FRONT("VestFront"),
    /**
     * Back vest panel, 20 motor indexes in a 4x5 grid
     */
    VEST_BACK("VestBack"),

    /**
     * Tactal head pad, 6 motor indexes
     */
    HEAD("Head"),

    /**
     * Tactosy arm sleeve, 6 motor indexes
     */
    FOREARM_LEFT("ForearmL"),
    FOREARM_RIGHT("ForearmR"),

    /**
     * Tactosy hand attachment, 3 motor indexes
     */
    HAND_LEFT("HandL"),
    HAND_RIGHT("HandR"),

    /**
     * TactGlove, 6 motor indexes
     */
    GLOVE_LEFT("GloveL"),
    GLOVE_RIGHT("GloveR"),

    /**
     * Tactosy foot attachment, 3 motor indexes
     */
    FOOT_LEFT("FootL"),
    FOOT_RIGHT("FootR");


    private static final Map<String, BHapticsPosition> BY_KEY = new HashMap<>();

    static {
        for (BHapticsPosition position : values()) {
            BY_KEY.put(position.key, position);
        }
    }

    private final String key;

    BHapticsPosition(String key) {
        this.key = key;
    }

    public @NotNull String getKey() {
        return key;
    }

    public static @Nullable BHapticsPosition fromKey(@NotNull String key) {
        return BY_KEY.get(key);
    }
}
