package me.phoenixra.atumvr.core.input.treadmill.kat;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import lombok.Getter;
import lombok.Setter;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.treadmill.XRTreadmillProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * Legacy KAT provider (first generation KAT Walk / loco) over WalkerBase.dll.
 * <p>
 *     The legacy API reports unitless power 0..3000 and forward/backward only,
 *     speed is normalized through the settable maxSpeedMps
 * </p>
 */
public class KATLegacyTreadmillProvider implements XRTreadmillProvider {

    private interface WalkerBase extends Library {
        String LIBRARY_NAME = "WalkerBase";

        void Init(int mode);

        int Launch();

        boolean CheckForLaunch();

        void Halt();

        boolean GetWalkerData(int deviceIndex,
                              IntByReference yaw,
                              DoubleByReference power,
                              IntByReference direction,
                              IntByReference moving,
                              FloatByReference extra);
    }

    private static final float MAX_POWER = 3000f;
    private static final float YAW_RANGE = 1024f;

    private final XRProvider vrProvider;
    private final @Nullable String librarySearchPath;

    private WalkerBase sdk;
    private Boolean supported;
    private boolean halted;

    @Getter @Setter
    private float maxSpeedMps = 4f;

    // created in detectTreadmill so constructing the provider never touches JNA classes
    private IntByReference cachedYaw;
    private DoubleByReference cachedPower;
    private IntByReference cachedDirection;
    private IntByReference cachedMoving;
    private FloatByReference cachedExtra;

    private boolean active;
    private boolean readFailed;
    private final Vector2f motion = new Vector2f();
    private float speed;

    private final Quaternionf bodyOrientation = new Quaternionf();
    private boolean hasOrientation;
    private float sensorYaw;
    private float yawOffset;


    public KATLegacyTreadmillProvider(@NotNull XRProvider vrProvider) {
        this(vrProvider, null);
    }

    public KATLegacyTreadmillProvider(@NotNull XRProvider vrProvider,
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
            if (librarySearchPath != null) {
                NativeLibrary.addSearchPath(WalkerBase.LIBRARY_NAME, librarySearchPath);
            }
            sdk = Native.load(WalkerBase.LIBRARY_NAME, WalkerBase.class);

            cachedYaw = new IntByReference();
            cachedPower = new DoubleByReference();
            cachedDirection = new IntByReference();
            cachedMoving = new IntByReference();
            cachedExtra = new FloatByReference();
        } catch (Throwable t) {
            vrProvider.getLogger().logInfo(
                    "WalkerBase library not available: " + describeLoadFailure(t)
            );
            return false;
        }
        try {
            sdk.Init(1);
            sdk.Launch();
            if (sdk.CheckForLaunch()) {
                vrProvider.getLogger().logInfo("Legacy KAT treadmill software found");
                return true;
            }
        } catch (Throwable t) {
            vrProvider.getLogger().logError("Legacy KAT init failed: " + t.getMessage());
        }
        return false;
    }


    @Override
    public void onAttached() {
        // destroy() halts the walker software, a runtime re-enable relaunches it
        if (sdk == null || !halted) {
            return;
        }
        try {
            sdk.Init(1);
            sdk.Launch();
            halted = false;
            readFailed = false;
        } catch (Throwable t) {
            vrProvider.getLogger().logError("Legacy KAT relaunch failed: " + t.getMessage());
        }
    }


    @Override
    public void update() {
        try {
            sdk.GetWalkerData(0, cachedYaw, cachedPower, cachedDirection, cachedMoving, cachedExtra);
            readFailed = false;
        } catch (Throwable t) {
            if (!readFailed) {
                readFailed = true;
                vrProvider.getLogger().logError("Legacy KAT GetWalkerData failed: " + t.getMessage());
            }
            active = false;
            motion.zero();
            speed = 0f;
            return;
        }
        active = true;

        // the reported direction sign is inverted, matches Vivecraft's handling
        int direction = -cachedDirection.getValue();
        float power = (float) (cachedPower.getValue() / MAX_POWER);
        if (cachedMoving.getValue() != 1 || power <= 0f || direction == 0) {
            motion.zero();
            speed = 0f;
        } else {
            motion.set(0f, direction > 0 ? 1f : -1f);
            speed = Math.min(power, 1f) * maxSpeedMps;
        }

        // ring sensor 0..1023, clockwise-positive, negated for play space
        sensorYaw = -(cachedYaw.getValue() / YAW_RANGE) * (float) (Math.PI * 2.0);
        hasOrientation = true;
        refreshOrientation();
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


    @Override
    public void destroy() {
        if (sdk == null) {
            return;
        }
        try {
            sdk.Halt();
        } catch (Throwable ignored) {
        }
        halted = true;
        active = false;
        motion.zero();
        speed = 0f;
    }


    private static String describeLoadFailure(Throwable t) {
        if (t instanceof NoClassDefFoundError) {
            return "JNA is not on the runtime classpath";
        }
        return t.getMessage();
    }
}
