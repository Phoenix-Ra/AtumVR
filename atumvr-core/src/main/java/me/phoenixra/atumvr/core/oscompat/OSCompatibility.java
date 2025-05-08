package me.phoenixra.atumvr.core.oscompat;

import org.lwjgl.openxr.XrInstance;
import org.lwjgl.openxr.XrSwapchainImageOpenGLKHR;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

public interface OSCompatibility {


    void initOpenXRLoader(MemoryStack stack);

    String getGraphicsExtension();

    XrSwapchainImageOpenGLKHR.Buffer createImageBuffers(int imageCount, MemoryStack stack);

    Struct<?> checkGraphics(MemoryStack stack, XrInstance instance,
                         long systemID,
                         long windowHandle);

    static OSCompatibility detectDevice() {
        return System.getProperty("os.version").contains("Android") ? new AndroidCompat() : new DesktopCompat();
    }
}
