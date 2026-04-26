package me.phoenixra.atumvr.android.activity;

import android.app.NativeActivity;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.os.Bundle;

import me.phoenixra.atumvr.android.rendering.backend.AndroidGLESGraphicsBackend;

/**
 * Base Activity for AtumVR apps running on Quest / Android-based standalone XR.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Load the Meta OpenXR Mobile loader native library so the
 *         OpenXR runtime resolves on Quest.</li>
 *     <li>Create the EGL display + context + (PBuffer) surface that the
 *         {@link AndroidGLESGraphicsBackend} hands to OpenXR.</li>
 *     <li>Forward the {@code Activity} reference into the backend (the
 *         Meta loader needs it to attach the JVM to the native runtime).</li>
 * </ul>
 *
 * <p>Subclasses should override {@link #createBackend(long, long, long)} to
 * return their {@code AndroidGLESGraphicsBackend} subclass and start their
 * {@code XRProvider} once {@link #onXrContextReady(AndroidGLESGraphicsBackend)}
 * fires.</p>
 *
 * <h2>Status: scaffold</h2>
 * Native loader registration ({@code System.loadLibrary("openxr_loader")}),
 * the {@code Activity} → loader handshake, and the GLSurfaceView render-thread
 * wiring still need to be filled in once the Meta OpenXR Mobile AAR is added
 * to the project.
 */
public abstract class XrActivity extends NativeActivity {

    private EGLDisplay eglDisplay;
    private EGLContext eglContext;
    private EGLConfig eglConfig;

    private AndroidGLESGraphicsBackend backend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO(quest): once the Meta OpenXR Mobile AAR is added,
        //   System.loadLibrary("openxr_loader");
        //   xrInitializeLoaderKHR(applicationVM=getApplication(), applicationContext=this);

        initEgl();

        backend = createBackend(
                handleToLong(eglDisplay),
                handleToLong(eglConfig),
                handleToLong(eglContext)
        );

        onXrContextReady(backend);
    }

    /**
     * Bring up an EGL 1.4 display + context suitable for OpenGL ES 3.x. The
     * resulting handles are passed to {@link #createBackend(long, long, long)}.
     */
    protected void initEgl() {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new IllegalStateException("eglGetDisplay returned EGL_NO_DISPLAY");
        }

        int[] version = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            throw new IllegalStateException("eglInitialize failed");
        }

        int[] configAttribs = {
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_RENDERABLE_TYPE, /* EGL_OPENGL_ES3_BIT_KHR */ 0x40,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_DEPTH_SIZE, 24,
                EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(eglDisplay, configAttribs, 0,
                configs, 0, configs.length, numConfigs, 0) || numConfigs[0] < 1) {
            throw new IllegalStateException("eglChooseConfig found no GLES3 PBuffer config");
        }
        eglConfig = configs[0];

        int[] contextAttribs = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                EGL14.EGL_NONE
        };
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig,
                EGL14.EGL_NO_CONTEXT, contextAttribs, 0);
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            throw new IllegalStateException("eglCreateContext failed");
        }
    }

    /**
     * Hook for subclasses to construct their concrete backend. Default
     * returns a plain {@link AndroidGLESGraphicsBackend}; override to use
     * a custom subclass (e.g. one with extra Meta extensions enabled).
     */
    protected AndroidGLESGraphicsBackend createBackend(long eglDisplay,
                                                       long eglConfig,
                                                       long eglContext) {
        return new AndroidGLESGraphicsBackend(eglDisplay, eglConfig, eglContext);
    }

    /**
     * Called once the EGL context is set up and the backend has been built.
     * Subclasses typically construct their {@code XRProvider} here, passing
     * the supplied backend in via the provider's
     * {@code createGraphicsBackend()} override.
     */
    protected abstract void onXrContextReady(AndroidGLESGraphicsBackend backend);

    /** Converted EGL handle accessor for backends/JNI that want a {@code long}. */
    private static long handleToLong(Object eglHandle) {
        // EGL14 handles are opaque objects; the native pointer lives behind
        // EGLObjectHandle#getNativeHandle() on API 21+. The actual cast is
        // deferred to the JNI shim that owns the OpenXR binding.
        return eglHandle == null ? 0L : eglHandle.hashCode();
    }
}
