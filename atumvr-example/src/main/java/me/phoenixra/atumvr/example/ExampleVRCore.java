package me.phoenixra.atumvr.example;

import lombok.Getter;
import me.phoenixra.atumconfig.api.config.ConfigManager;
import me.phoenixra.atumconfig.core.config.AtumConfigManager;
import me.phoenixra.atumvr.api.VRProvider;
import me.phoenixra.atumvr.core.AtumVRApp;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;

public class ExampleVRCore extends AtumVRApp {
    public static ExampleVRCore appInstance;
    public static ExampleVRProvider provider;
    private final Logger logger;
    @Getter
    private final File dataFolder;

    public ExampleVRCore(VRProvider vrProvider) {
        super(vrProvider);
        logger = Logger.getLogger("ExampleVRApp");
        dataFolder = new File("data");
    }


    @Override
    protected ConfigManager createConfigManager() {
        return new AtumConfigManager(this);
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
                        provider = new ExampleVRProvider();

                        appInstance = new ExampleVRCore(provider);
                        appInstance.init();
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                        System.out.println("WHAT tick");
                        break;
                    }
                    init=true;
                    continue;
                }
                if(Thread.interrupted()){
                    appInstance.destroy();
                    break;
                }
                provider.getAttachedApp().preRender(1);
                provider.getAttachedApp().render(1);
                provider.getAttachedApp().postRender(1);

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

}
