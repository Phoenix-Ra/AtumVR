package me.phoenixra.atumvr.example;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRLogger;
import me.phoenixra.atumvr.api.rendering.VRRenderContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExampleVRApp {
    public static ExampleVRApp appInstance;
    public static ExampleVRProvider vrProvider;
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
                if(vrProvider != null){
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
                        vrProvider = new ExampleVRProvider(VRLogger.SIMPLE.setDebug(true));
                        vrProvider.initializeVR();
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
                    vrProvider.destroy();
                    init = false;
                    restart.set(false);
                    continue;
                }
                if(vrProvider.isXrStopping()){
                    break;
                }

                VRRenderContext context = () -> 1;
                vrProvider.startFrame();
                vrProvider.render(context);
                vrProvider.postRender();

            } while (true);
        });
    }


}
