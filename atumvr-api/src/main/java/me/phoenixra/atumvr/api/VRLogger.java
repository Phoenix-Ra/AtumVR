package me.phoenixra.atumvr.api;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VRLogger {
    /**
     * VR logger with no output
     */
    VRLogger EMPTY = new VRLogger() {
        @Override
        public void logDebug(@NotNull String msg) {}
        @Override
        public void logInfo(@NotNull String msg) {}
        @Override
        public void logWarn(@NotNull String msg) {}
        @Override
        public void logError(@NotNull String msg) {}
    };

    /**
     * Simple VR logger, that uses System.out.println
     */
    VRLogger SIMPLE = new VRLogger() {
        @Override
        public void logDebug(@NotNull String msg) {System.out.println("DEBUG: "+msg);}
        @Override
        public void logInfo(@NotNull String msg) {System.out.println("INFO: "+msg);}
        @Override
        public void logWarn(@NotNull String msg) {System.out.println("WARN: "+msg);}
        @Override
        public void logError(@NotNull String msg) {System.out.println("ERROR: "+msg);}
    };

    /**
     * Logs a debug message.
     *
     * @param msg the message to log, must be non-null
     */
    void logDebug(@NotNull String msg);

    /**
     * Logs an informational message.
     *
     * @param msg the message to log, must be non-null
     */
    void logInfo(@NotNull String msg);

    /**
     * Logs a warning message.
     *
     * @param msg the message to log, must be non-null
     */
    void logWarn(@NotNull String msg);

    /**
     * Logs an error message.
     *
     * @param msg the message to log, must be non-null
     */
    void logError(@NotNull String msg);

    /**
     * Logs an error message with optional exception details.
     *
     * @param msg the error message, may be null
     * @param t   the throwable whose stack trace to log
     */
    default void logError(@Nullable String msg, @NotNull Throwable t) {
        if (msg != null) {
            logError(msg);
        }
        for (StackTraceElement element : t.getStackTrace()) {
            logError(element.toString());
        }
        Throwable cause = t.getCause();
        if (cause != null) {
            logError("Caused by: " + cause);
        }
        for (Throwable suppressed : t.getSuppressed()) {
            logError(null, suppressed);
        }
    }
}
