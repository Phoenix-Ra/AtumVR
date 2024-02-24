package me.phoenixra.atumvr.example;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.scene.VRSceneRenderer;
import me.phoenixra.atumvr.core.AtumVRCore;
import me.phoenixra.atumvr.example.scene.ExampleSceneRenderer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
        Thread updateThread = getThread();
        updateThread.start();

        Scanner scanner = new Scanner(System.in);
        while(!scanner.nextLine().equals("stop")){
            System.out.println("Use 'stop' to exit");
        }

        updateThread.interrupt(); // Stop the update thread
        try {
            updateThread.join(); // Wait for the update thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scanner.close();
    }

    @NotNull
    private static Thread getThread() {
        return new Thread(() -> {
            boolean init = false;
            do {
                if(!init){
                    try {
                        instance = new ExampleVRApp();
                        instance.initializeVR();
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                        System.out.println("WHAT tick");
                        break;
                    }
                    init=true;
                    continue;
                }
                if(Thread.interrupted()){
                    instance.clear();
                    break;
                }
                instance.update();

            } while (true);
        });
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

    @Override
    public @NotNull VRSceneRenderer createSceneRenderer(@NotNull VRApp vrApp) {
        return new ExampleSceneRenderer(vrApp);
    }
}
