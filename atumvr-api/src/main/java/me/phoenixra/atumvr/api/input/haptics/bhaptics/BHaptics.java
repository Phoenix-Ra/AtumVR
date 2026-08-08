package me.phoenixra.atumvr.api.input.haptics.bhaptics;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The bHaptics vendor api, talks to the bHaptics Player app.
 * <p>
 *     All play/stop calls are non-blocking and no-ops while the Player
 *     is unreachable. Registered patterns survive reconnects
 * </p>
 */
public interface BHaptics {

    /**
     * If the connection to the bHaptics Player app is open
     */
    boolean isPlayerConnected();

    /**
     * Get device positions currently connected to the Player.
     * <p>
     *     A connected vest is reported as {@link BHapticsPosition#VEST}
     * </p>
     */
    @NotNull Set<BHapticsPosition> getConnectedPositions();

    /**
     * If a device is connected at the given position.
     * Vest front/back also match a connected {@link BHapticsPosition#VEST}
     */
    boolean isDeviceConnected(@NotNull BHapticsPosition position);


    /**
     * Play feedback on specific motors of a device.
     * <p>
     *     Re-submitting the same key replaces the playing frame,
     *     which is the way to stream continuous effects
     * </p>
     *
     * @param key            effect channel name
     * @param position       target device
     * @param durationMillis frame duration in milliseconds
     * @param points         motors to drive
     */
    void playDots(@NotNull String key,
                  @NotNull BHapticsPosition position,
                  int durationMillis,
                  @NotNull BHapticsDotPoint... points);

    /**
     * Play feedback at normalized points on a device surface,
     * interpolated onto the nearest motors by the Player
     *
     * @param key            effect channel name
     * @param position       target device
     * @param durationMillis frame duration in milliseconds
     * @param points         surface points to render
     */
    void playPath(@NotNull String key,
                  @NotNull BHapticsPosition position,
                  int durationMillis,
                  @NotNull BHapticsPathPoint... points);


    /**
     * Register a haptic pattern designed in the bHaptics Designer.
     * <p>
     *     Registering an existing key replaces the pattern for future plays.
     *     Registrations are session state of the Player, dropped when
     *     the app disconnects - there is no unregister
     * </p>
     *
     * @param key      pattern key to play it by
     * @param tactJson content of a .tact file
     */
    void registerPattern(@NotNull String key, @NotNull String tactJson);

    /**
     * If a pattern was registered with {@link #registerPattern}
     */
    boolean isPatternRegistered(@NotNull String key);

    /**
     * Play a registered pattern
     */
    default void playRegistered(@NotNull String key) {
        playRegistered(key, 1f, 1f, 0f, 0f);
    }

    /**
     * Play a registered pattern with intensity/duration multipliers
     */
    default void playRegistered(@NotNull String key,
                                float intensityScale, float durationScale) {
        playRegistered(key, intensityScale, durationScale, 0f, 0f);
    }

    /**
     * Play a registered pattern transformed on the vest
     *
     * @param key             pattern key
     * @param intensityScale  intensity multiplier, 1 plays as designed
     * @param durationScale   duration multiplier, 1 plays as designed
     * @param rotationAngleX  rotation around the vest in degrees,
     *                        e.g. 180 turns a front hit into a back hit
     * @param rotationOffsetY vertical offset in normalized units
     */
    void playRegistered(@NotNull String key,
                        float intensityScale, float durationScale,
                        float rotationAngleX, float rotationOffsetY);


    /**
     * If the effect with the given key is currently playing
     */
    boolean isPlaying(@NotNull String key);

    /**
     * If any effect is currently playing
     */
    boolean isPlayingAny();

    /**
     * Stop the effect with the given key
     */
    void stop(@NotNull String key);

    /**
     * Stop all currently playing effects
     */
    void stopAll();
}
