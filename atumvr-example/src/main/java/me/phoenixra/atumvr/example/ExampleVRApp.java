package me.phoenixra.atumvr.example;

import lombok.Getter;
import me.phoenixra.atumconfig.api.utils.FileUtils;
import me.phoenixra.atumvr.core.AtumVRCore;
import org.jetbrains.annotations.NotNull;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Logger;

public class ExampleVRApp extends AtumVRCore {
    public static ExampleVRApp instance;
    private final Logger logger;
    @Getter
    private final File dataFolder;
    public ExampleVRApp(){
        logger = Logger.getLogger("ExampleVRApp");
        dataFolder = new File("data");
    }

    public static void main(String[] args) {
        try {
            instance = new ExampleVRApp();
            instance.initializeVR();
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return;
        }

        Thread updateThread = new Thread(() -> {
            do {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
                instance.update();

            } while (true);
        });
        updateThread.start();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine(); // Wait for user input to terminate

        updateThread.interrupt(); // Stop the update thread
        try {
            updateThread.join(); // Wait for the update thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scanner.close();
        instance.clear();
    }

    @Override
    public @NotNull String getName() {
        return "Example App";
    }

    @Override
    public boolean supportMinecraft() {
        return false;
    }

    @Override
    public void logInfo(String message) {
        logger.info(message);
    }

    @Override
    public void logWarning(String message) {
        logger.warning(message);
    }

    @Override
    public void logError(String message) {
        logger.severe(message);
    }
}
