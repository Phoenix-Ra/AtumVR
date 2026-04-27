package me.phoenixra.atumvr.android.nativebridge;

/**
 * Java surface for the {@code libatumvr_native.so} JNI layer.
 *
 * <p>This class is intentionally tiny right now (step 1 of the Quest port):
 * it loads the native library, exposes a couple of plumbing checks, and
 * lets unit tests / sample apps confirm the NDK + CMake build is wired up
 * correctly before any real OpenXR code lands in the native module.</p>
 *
 * <p>Subsequent steps will add native methods for OpenXR loader init,
 * instance / session / swapchain creation, and per-frame submit. They will
 * land here so the entire native API stays behind a single JNI surface.</p>
 *
 * <h2>Loading semantics</h2>
 * The shared library is loaded once on first class touch. Callers don't
 * need to call any explicit {@code init()} method — accessing any static on
 * this class triggers the static initializer.
 */
public final class NativeXRBridge {

    /** Library name emitted by CMake (matches {@code libatumvr_native.so}). */
    public static final String LIBRARY_NAME = "atumvr_native";

    /**
     * Native ABI revision the Java side compiles against. Bumped whenever a
     * native entry point changes shape; checked at runtime against
     * {@link #nativeAbiVersion()} to fail fast on mismatched native libs.
     */
    public static final int EXPECTED_ABI_VERSION = 1;

    private static volatile boolean loaded;

    static {
        try {
            System.loadLibrary(LIBRARY_NAME);
            loaded = true;
        } catch (UnsatisfiedLinkError err) {
            loaded = false;
            // Don't rethrow at class-init time — callers in unit tests / on
            // non-Android runtimes can still observe `isNativeLoaded()` and
            // skip features that depend on the native lib.
            System.err.println("[AtumVR] failed to load native library '"
                    + LIBRARY_NAME + "': " + err.getMessage());
        }
    }

    private NativeXRBridge() {
        throw new AssertionError("no instances");
    }

    /** {@code true} if {@code libatumvr_native.so} was loaded successfully. */
    public static boolean isNativeLoaded() {
        return loaded;
    }

    /**
     * Verify Java/native ABI agreement. Call this once during app startup;
     * throws {@link IllegalStateException} on mismatch (which usually means
     * a stale {@code libatumvr_native.so} is being picked up from a
     * previous install).
     */
    public static void verifyAbi() {
        if (!loaded) {
            throw new IllegalStateException(
                    "atumvr_native is not loaded — verify ABI/AAR setup");
        }
        int actual = nativeAbiVersion();
        if (actual != EXPECTED_ABI_VERSION) {
            throw new IllegalStateException(
                    "atumvr_native ABI mismatch: java expects "
                            + EXPECTED_ABI_VERSION + ", native reports " + actual);
        }
    }

    /**
     * Free-form build-time string from the native lib (variant, OpenXR
     * linkage state, etc). Useful for crash reports / logcat banners.
     */
    public static String buildInfo() {
        return loaded ? nativeBuildInfo() : "atumvr_native (not loaded)";
    }

    // -------------------------------------------------------------------------
    // Native entry points (declared here, defined in atumvr_jni.cpp)
    // -------------------------------------------------------------------------

    private static native int nativeAbiVersion();

    private static native String nativeBuildInfo();
}
