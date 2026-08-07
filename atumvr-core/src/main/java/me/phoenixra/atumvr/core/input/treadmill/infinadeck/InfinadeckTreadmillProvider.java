package me.phoenixra.atumvr.core.input.treadmill.infinadeck;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.ptr.IntByReference;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.treadmill.XRTreadmillProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * Infinadeck provider over InfinadeckAPI.dll.
 * Reports the floor velocity as forward gait along its angle
 */
public class InfinadeckTreadmillProvider implements XRTreadmillProvider {

    private interface InfinadeckAPI extends Library {
        String LIBRARY_NAME = "InfinadeckAPI";

        int InitInternal(IntByReference error, boolean forceConnect);

        int DeInitInternal();

        boolean CheckConnection();

        boolean GetTreadmillRunState();

        double GetFloorSpeedAngle();

        double GetFloorSpeedMagnitude();
    }

    private static final double IDLE_EPSILON = 1.0e-4;

    private final XRProvider vrProvider;
    private final @Nullable String librarySearchPath;

    private InfinadeckAPI sdk;
    private Boolean supported;

    private boolean active;
    private boolean readFailed;
    private final Vector2f motion = new Vector2f();
    private float speed;

    private final Quaternionf bodyOrientation = new Quaternionf();
    private boolean hasOrientation;
    private float sensorYaw;
    private float yawOffset;


    public InfinadeckTreadmillProvider(@NotNull XRProvider vrProvider) {
        this(vrProvider, null);
    }

    public InfinadeckTreadmillProvider(@NotNull XRProvider vrProvider,
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
                NativeLibrary.addSearchPath(InfinadeckAPI.LIBRARY_NAME, librarySearchPath);
            }
            sdk = Native.load(InfinadeckAPI.LIBRARY_NAME, InfinadeckAPI.class);
        } catch (Throwable t) {
            vrProvider.getLogger().logInfo(
                    "InfinadeckAPI library not available: " + describeLoadFailure(t)
            );
            return false;
        }
        try {
            IntByReference error = new IntByReference();
            sdk.InitInternal(error, false);
            if (error.getValue() != 0) {
                sdk.InitInternal(error, true);
            }
            if (error.getValue() == 0) {
                vrProvider.getLogger().logInfo("Infinadeck connected");
                return true;
            }
            vrProvider.getLogger().logInfo(
                    "Infinadeck connection failed, error code " + error.getValue()
            );
        } catch (Throwable t) {
            vrProvider.getLogger().logError("Infinadeck init failed: " + t.getMessage());
        }
        return false;
    }


    @Override
    public void update() {
        try {
            if (!sdk.CheckConnection()) {
                setIdle(false);
                return;
            }
            active = true;

            double magnitude = sdk.GetFloorSpeedMagnitude();
            if (!sdk.GetTreadmillRunState() || magnitude <= IDLE_EPSILON) {
                motion.zero();
                speed = 0f;
            } else {
                motion.set(0f, 1f);
                speed = (float) magnitude;
            }

            // angle is radians in deck space, negated for play space
            sensorYaw = -(float) sdk.GetFloorSpeedAngle();
            hasOrientation = true;
            refreshOrientation();
            readFailed = false;
        } catch (Throwable t) {
            if (!readFailed) {
                readFailed = true;
                vrProvider.getLogger().logError("Infinadeck read failed: " + t.getMessage());
            }
            setIdle(false);
        }
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


    @Override
    public void destroy() {
        if (sdk == null) {
            return;
        }
        try {
            sdk.DeInitInternal();
        } catch (Throwable ignored) {
        }
        active = false;
    }


    private static String describeLoadFailure(Throwable t) {
        if (t instanceof NoClassDefFoundError) {
            return "JNA is not on the runtime classpath";
        }
        return t.getMessage();
    }
}
