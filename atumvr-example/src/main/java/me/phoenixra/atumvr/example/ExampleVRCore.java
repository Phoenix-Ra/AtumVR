package me.phoenixra.atumvr.example;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRApp;
import me.phoenixra.atumvr.api.input.VRInputHandler;
import me.phoenixra.atumvr.core.AtumVRCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;

public class ExampleVRCore extends AtumVRCore {
    public static ExampleVRCore instance;
    private final Logger logger;
    @Getter
    private final File dataFolder;
    public ExampleVRCore(){
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
                        instance = new ExampleVRCore();
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
                instance.getVrApp().preRender(1);
                instance.getVrApp().render(1);
                instance.getVrApp().postRender(1);

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
    public @NotNull VRApp createVRApp() {
        return new ExampleVRApp(this);
    }

    @Override
    public @Nullable VRInputHandler createVRInputHandler() {
        return null;
    }
}
