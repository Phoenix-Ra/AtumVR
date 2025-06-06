package me.phoenixra.atumvr.example;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.api.rendering.RenderContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Scanner;

public class ExampleVRApp {
    public static ExampleVRApp appInstance;
    public static ExampleVRProvider provider;
    @Getter
    private final File dataFolder;

    public static boolean leftHanded;

    public ExampleVRApp() {

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
                        provider = new ExampleVRProvider(VRLogger.SIMPLE.setDebug(true));
                        provider.initializeVR();
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                        System.out.println("Init error");
                        break;
                    }
                    init=true;
                    continue;
                }
                if(Thread.interrupted()){
                    break;
                }
                if(provider.isXrStopping()){
                    break;
                }
                RenderContext context = () -> 1;
                provider.preRender(context);
                provider.render(context);
                provider.postRender(context);

            } while (true);
        });
    }


}
