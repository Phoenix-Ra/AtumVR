package me.phoenixra.atumvr.core.oscompat;

import org.lwjgl.openxr.XrInstance;
import org.lwjgl.openxr.XrSwapchainImageOpenGLKHR;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Struct;

public class AndroidCompat implements OSCompatibility{


    @Override
    public void initOpenXRLoader(MemoryStack stack) {

    }

    @Override
    public String getGraphicsExtension() {
        return "";
    }

    @Override
    public XrSwapchainImageOpenGLKHR.Buffer createImageBuffers(int imageCount, MemoryStack stack) {
        return null;
    }

    @Override
    public Struct<?> checkGraphics(MemoryStack stack, XrInstance instance, long systemID, long windowHandle) {
        return null;
    }
}
