package me.phoenixra.atumvr.example;

import me.phoenixra.atumvr.api.AtumVRLogger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ExampleFileLogger implements AtumVRLogger {

    private static final String LOG_FILE_NAME = "latest.log";

    private static final DateTimeFormatter LINE_STAMP =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final Object lock = new Object();

    private BufferedWriter writer;
    private boolean debug;

    public ExampleFileLogger(@NotNull File logsFolder) {
        try {
            if (!logsFolder.exists() && !logsFolder.mkdirs()) {
                System.err.println("Could not create logs folder: " + logsFolder.getAbsolutePath());
            }
            File logFile = new File(logsFolder, LOG_FILE_NAME);

            writer = new BufferedWriter(new FileWriter(logFile, false));
            Runtime.getRuntime().addShutdownHook(new Thread(this::close));
            logInfo("Logging to " + logFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to open log file, continuing console-only: " + e.getMessage());
            writer = null;
        }
    }

    private void write(@NotNull String level, @NotNull String msg) {
        String line = "[" + LocalDateTime.now().format(LINE_STAMP) + "] [" + level + "] " + msg;
        synchronized (lock) {
            System.out.println(line);
            if (writer != null) {
                try {
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                } catch (IOException e) {
                    System.err.println("Failed to write log line: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void logDebug(@NotNull String msg) {
        if (debug) {
            write("DEBUG", msg);
        }
    }

    @Override
    public void logInfo(@NotNull String msg) {
        write("INFO", msg);
    }

    @Override
    public void logWarn(@NotNull String msg) {
        write("WARN", msg);
    }

    @Override
    public void logError(@NotNull String msg) {
        write("ERROR", msg);
    }

    @Override
    public @NotNull AtumVRLogger setDebug(boolean flag) {
        this.debug = flag;
        return this;
    }


    public void close() {
        synchronized (lock) {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException ignored) {

                } finally {
                    writer = null;
                }
            }
        }
    }
}
