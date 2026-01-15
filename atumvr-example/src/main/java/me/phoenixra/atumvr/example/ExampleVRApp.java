package me.phoenixra.atumvr.example;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.api.rendering.IRenderContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExampleVRApp {
    public static ExampleVRApp appInstance;
    public static ExampleVRProvider provider;
    @Getter
    private final File dataFolder;

    public static boolean leftHanded;

    private static final AtomicBoolean restart = new AtomicBoolean(false);

    public ExampleVRApp() {

        dataFolder = new File("data");
    }



    public static void main(String[] args) {
        Thread updateThread = getThread();
        updateThread.start();

        Scanner scanner = new Scanner(System.in);
        while(true){
            String cmd = scanner.nextLine();
            if(cmd.equals("stop")){
                break;
            }
            if(cmd.equals("restart")){
                if(provider != null){
                    restart.set(true);
                }
            }
            System.out.println("Use 'stop' or 'restart'");
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
                if(restart.get()){
                    provider.destroy();
                    init = false;
                    restart.set(false);
                    continue;
                }
                if(provider.isXrStopping()){
                    break;
                }

                IRenderContext context = () -> 1;
                provider.startFrame();
                provider.render(context);
                provider.postRender();

            } while (true);
        });
    }


}
