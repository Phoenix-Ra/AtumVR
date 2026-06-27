package me.phoenixra.atumvr.example;

import lombok.Getter;
import me.phoenixra.atumvr.api.AtumVRLogger;
import me.phoenixra.atumvr.api.rendering.AtumVRRenderContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExampleVRApp {
    public static ExampleVRApp appInstance;
    public static ExampleVRProvider vrProvider;
    @Getter
    private final File dataFolder;

    public static AtumVRLogger logger;

    public static boolean leftHanded;

    private static final AtomicBoolean restart = new AtomicBoolean(false);
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private static final AtomicBoolean stopping = new AtomicBoolean(false);

    private static volatile Thread updateThread;

    public ExampleVRApp() {

        dataFolder = new File("data");
    }



    public static void main(String[] args) {
        appInstance = new ExampleVRApp();
        logger = new ExampleFileLogger(new File("logs")).setDebug(true);
        ExampleResources.extractAll(
                appInstance.getDataFolder(),
                logger
        );

        updateThread = getThread();
        updateThread.start();

        createControlWindow();

        try (Scanner scanner = new Scanner(System.in)) {
            while (running.get() && scanner.hasNextLine()) {
                String cmd = scanner.nextLine().trim();
                if (cmd.equals("stop")) {
                    requestShutdown();
                    return;
                }
                if (cmd.equals("restart")) {
                    if (vrProvider != null) {
                        restart.set(true);
                    }
                    continue;
                }
                System.out.println("Use 'stop' or 'restart' (or just close the window)");
            }
        } catch (Exception ignored) {
        }
    }


    private static void requestShutdown() {
        if (!stopping.compareAndSet(false, true)) {
            return;
        }
        running.set(false);

        Thread t = updateThread;
        if (t != null && t.isAlive() && Thread.currentThread() != t) {
            t.interrupt();
            try {
                t.join(5000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        System.exit(0);
    }

    private static void createControlWindow() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("AtumVR Example");
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    requestShutdown();
                }
            });

            JLabel label = new JLabel(
                    "<html><center>AtumVR example is running.<br>"
                            + "Close this window to stop.</center></html>",
                    SwingConstants.CENTER
            );
            label.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            JButton stopButton = new JButton("Stop");
            stopButton.addActionListener(e -> requestShutdown());

            JPanel buttons = new JPanel();
            buttons.add(stopButton);

            frame.add(label, BorderLayout.CENTER);
            frame.add(buttons, BorderLayout.SOUTH);
            frame.setSize(340, 150);
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        });
    }

    @NotNull
    private static Thread getThread() {
        return new Thread(() -> {
            boolean init = false;
            AtumVRRenderContext context = () -> 1;
            try {
                do {
                    if (!init) {
                        try {
                            vrProvider = new ExampleVRProvider(logger);
                            vrProvider.initializeVR();
                        } catch (Throwable throwable) {
                            logger.logError("Init error: " + throwable.getMessage());
                            throwable.printStackTrace();
                            break;
                        }
                        init = true;
                        continue;
                    }
                    if (Thread.interrupted() || !running.get()) {
                        break;
                    }
                    if (restart.get()) {
                        vrProvider.destroy();
                        init = false;
                        restart.set(false);
                        continue;
                    }
                    if (vrProvider.isXrStopping()) {
                        break;
                    }

                    vrProvider.startFrame();
                    vrProvider.render(context);
                    vrProvider.postRender();

                } while (true);
            } catch (Throwable t) {
                logger.logError("Update loop error: " + t.getMessage());
                t.printStackTrace();
            } finally {
                if (init && vrProvider != null) {
                    try {
                        vrProvider.destroy();
                    } catch (Throwable t) {
                        logger.logError("destroy() failed: " + t.getMessage());
                    }
                }
            }

            requestShutdown();
        });
    }


}
