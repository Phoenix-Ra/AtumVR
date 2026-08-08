package me.phoenixra.atumvr.core.input.treadmill.kat;

import com.sun.jna.Memory;
import lombok.Getter;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.treadmill.XRTreadmillProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.nio.charset.StandardCharsets;

/**
 * KAT Walk provider (C2 / C2+ / Core 2) over KATNativeSDK.dll
 */
public class KATTreadmillProvider implements XRTreadmillProvider {

    private static final float IDLE_EPSILON = 1.0e-4f;

    private final XRProvider vrProvider;
    private final @Nullable String librarySearchPath;

    private KATNativeSDK sdk;
    private Boolean supported;

    @Getter
    private String deviceName = "";
    @Getter
    private String deviceSerial = "";
    private Memory cachedSerial;
    private KATNativeSDK.TreadMillData cachedData;

    private boolean active;
    private boolean readFailed;
    private final Vector2f motion = new Vector2f();
    private float speed;

    private final Quaternionf bodyOrientation = new Quaternionf();
    private boolean hasOrientation;
    private float sensorYaw;
    private float yawOffset;


    public KATTreadmillProvider(@NotNull XRProvider vrProvider) {
        this(vrProvider, null);
    }

    public KATTreadmillProvider(@NotNull XRProvider vrProvider,
                                @Nullable String librarySearchPath) {
        this.vrProvider = vrProvider;
        this.librarySearchPath = librarySearchPath;
    }


    @Override
    public boolean isSupported() {
        if (supported == null) {
            supported = detectTreadmill();
        }
        return supported;
    }

    private boolean detectTreadmill() {
        try {
            sdk = KATNativeSDK.load(librarySearchPath);
        } catch (Throwable t) {
            vrProvider.getLogger().logInfo(
                    "KATNativeSDK library not available: " + describeLoadFailure(t)
            );
            return false;
        }
        try {
            int count = sdk.DeviceCount();
            var desc = new KATNativeSDK.DeviceDescription();
            for (int i = 0; i < count; i++) {
                sdk.GetDevicesDesc(desc, i);
                if (desc.deviceType != KATNativeSDK.DeviceDescription.TYPE_TREADMILL) {
                    continue;
                }
                deviceName = nativeString(desc.deviceName);
                deviceSerial = nativeString(desc.serialNumber);

                byte[] serialBytes = deviceSerial.getBytes(StandardCharsets.US_ASCII);
                cachedSerial = new Memory(serialBytes.length + 1L);
                cachedSerial.write(0, serialBytes, 0, serialBytes.length);
                cachedSerial.setByte(serialBytes.length, (byte) 0);

                cachedData = new KATNativeSDK.TreadMillData();
                vrProvider.getLogger().logInfo(
                        "KAT treadmill found: " + deviceName + " (" + deviceSerial + ")"
                );
                return true;
            }
        } catch (Throwable t) {
            vrProvider.getLogger().logError("KAT device scan failed: " + t.getMessage());
        }
        return false;
    }


    @Override
    public void update() {
        try {
            sdk.GetWalkStatus(cachedData, cachedSerial);
            readFailed = false;
        } catch (Throwable t) {
            if (!readFailed) {
                readFailed = true;
                vrProvider.getLogger().logError("KAT GetWalkStatus failed: " + t.getMessage());
            }
            setIdle(false);
            return;
        }

        if (cachedData.connected == 0) {
            setIdle(false);
            return;
        }
        active = true;

        float strafe = cachedData.speedX;
        float forward = cachedData.speedZ;
        speed = (float) Math.sqrt(strafe * strafe + forward * forward);
        if (speed > IDLE_EPSILON) {
            motion.set(strafe, forward).div(speed);
        } else {
            motion.zero();
            speed = 0f;
        }

        // sensor quaternion is Unity-handed, yaw negated for play space
        float x = cachedData.qx, y = cachedData.qy, z = cachedData.qz, w = cachedData.qw;
        sensorYaw = -(float) Math.atan2(2f * (x * z + w * y), 1f - 2f * (x * x + y * y));
        hasOrientation = true;
        refreshOrientation();
    }

    private void setIdle(boolean connected) {
        active = connected;
        motion.zero();
        speed = 0f;
    }


    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public @NotNull Vector2fc getMotion() {
        return motion;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public @Nullable Quaternionfc getBodyOrientation() {
        return hasOrientation ? bodyOrientation : null;
    }

    @Override
    public void recenter(float playSpaceYawRadians) {
        yawOffset = playSpaceYawRadians - sensorYaw;
        refreshOrientation();
    }

    private void refreshOrientation() {
        bodyOrientation.identity().rotateY(sensorYaw + yawOffset);
    }


    //the SDK ignores vibration/LED on hardware without them
    public void vibrateConstant(float amplitude) {
        sdk.VibrateConst(amplitude);
    }

    public void vibrate(float amplitude, float durationSeconds, float frequency) {
        sdk.VibrateFor(durationSeconds, frequency, amplitude);
    }

    public void stopVibration() {
        sdk.VibrateConst(0f);
    }

    public void led(float amplitude) {
        sdk.LEDConst(amplitude);
    }

    public void ledFor(float durationSeconds, float frequency, float amplitude) {
        sdk.LEDFor(durationSeconds, frequency, amplitude);
    }


    @Override
    public void destroy() {
        if (sdk == null) {
            return;
        }
        try {
            sdk.VibrateConst(0f);
            sdk.LEDConst(0f);
        } catch (Throwable ignored) {
        }
        setIdle(false);
    }


    private static String describeLoadFailure(Throwable t) {
        if (t instanceof NoClassDefFoundError) {
            return "JNA is not on the runtime classpath";
        }
        return t.getMessage();
    }

    private static String nativeString(byte[] data) {
        int length = 0;
        while (length < data.length && data[length] != 0) {
            length++;
        }
        return new String(data, 0, length, StandardCharsets.US_ASCII);
    }
}
