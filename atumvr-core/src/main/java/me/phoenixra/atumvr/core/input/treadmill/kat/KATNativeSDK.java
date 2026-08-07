package me.phoenixra.atumvr.core.input.treadmill.kat;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.jetbrains.annotations.Nullable;

/**
 * Struct of KATNativeSDK.dll (KAT Walk C2 / C2+ / Core 2 generation).
 */
public interface KATNativeSDK extends Library {

    String LIBRARY_NAME = "KATNativeSDK";

    static KATNativeSDK load(@Nullable String searchDirectory) {
        if (searchDirectory != null) {
            NativeLibrary.addSearchPath(LIBRARY_NAME, searchDirectory);
        }
        return Native.load(LIBRARY_NAME, KATNativeSDK.class);
    }


    int DeviceCount();

    void GetDevicesDesc(DeviceDescription out, int index);

    void GetWalkStatus(TreadMillData out, Pointer deviceSerial);

    double GetLastCalibratedTimeEscaped();

    //vibration & LED extensions, WalkCoord2 generation and later
    void VibrateConst(float amplitude);

    void VibrateOnce(float amplitude);

    void VibrateInSeconds(float amplitude, float duration);

    void VibrateFor(float duration, float frequency, float amplitude);

    void LEDConst(float amplitude);

    void LEDOnce(float amplitude);

    void LEDInSeconds(float amplitude, float duration);

    void LEDFor(float duration, float frequency, float amplitude);


    @Structure.FieldOrder({"deviceName", "serialNumber", "pid", "vid", "deviceType", "reserved"})
    class DeviceDescription extends Structure {
        public static final int TYPE_TREADMILL = 1;

        public byte[] deviceName = new byte[64];
        public byte[] serialNumber = new byte[64];
        public int pid;
        public int vid;
        public int deviceType;
        // headroom in case the native struct grows
        public byte[] reserved = new byte[116];

        public DeviceDescription() {
            setAlignType(ALIGN_NONE);
        }
    }

    @Structure.FieldOrder({"deviceName", "connected", "lastUpdateTimePoint",
            "qx", "qy", "qz", "qw", "speedX", "speedY", "speedZ",
            "sensorData", "extraData", "reserved"})
    class TreadMillData extends Structure {
        public byte[] deviceName = new byte[64];
        public byte connected;
        public double lastUpdateTimePoint;
        //body rotation, treadmill sensor space
        public float qx, qy, qz, qw;
        //target move speed in m/s, body-local
        public float speedX, speedY, speedZ;
        //3x { btnPressed, isBatteryCharging, batteryLevel(float), firmwareVersion }
        public byte[] sensorData = new byte[21];
        public byte[] extraData = new byte[128];
        // headroom in case the native struct grows
        public byte[] reserved = new byte[134];

        public TreadMillData() {
            setAlignType(ALIGN_NONE);
        }
    }
}
