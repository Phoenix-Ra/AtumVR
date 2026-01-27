package me.phoenixra.atumvr.api.utils;

import org.lwjgl.opengl.GL11;

/**
 * Utilities class containing methods, that help
 * to interact with OpenGL
 */
public class GLUtils {

    private GLUtils() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }


    public static void checkGLError(String message) {
        int error = GL11.glGetError();
        if (error != 0) {
            throw new RuntimeException(message+" OpenGL Error Code: " + error);
        }
    }

}
