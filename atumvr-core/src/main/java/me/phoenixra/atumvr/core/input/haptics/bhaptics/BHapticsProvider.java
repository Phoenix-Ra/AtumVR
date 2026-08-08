package me.phoenixra.atumvr.core.input.haptics.bhaptics;

import me.phoenixra.atumvr.api.input.haptics.bhaptics.BHaptics;
import me.phoenixra.atumvr.api.input.haptics.bhaptics.BHapticsDotPoint;
import me.phoenixra.atumvr.api.input.haptics.bhaptics.BHapticsPathPoint;
import me.phoenixra.atumvr.api.input.haptics.bhaptics.BHapticsPosition;
import me.phoenixra.atumvr.core.XRProvider;
import me.phoenixra.atumvr.core.input.haptics.XRBodyHapticsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class BHapticsProvider implements XRBodyHapticsProvider, BHaptics {

    private static final String PLAYER_HOST = "127.0.0.1";
    private static final int PLAYER_PORT = 15881;
    private static final int DETECT_TIMEOUT_MS = 400;
    private static final long RECONNECT_DELAY_MS = 3000;

    private static final BHapticsDotPoint[] NO_DOTS = {};
    private static final BHapticsPathPoint[] NO_PATHS = {};

    private final XRProvider vrProvider;
    private final String appId;
    private final String appName;

    private Boolean supported;

    private HttpClient httpClient;
    private ScheduledExecutorService executor;

    private volatile WebSocket webSocket;
    private volatile boolean shuttingDown;
    private final AtomicBoolean connecting = new AtomicBoolean();
    private volatile boolean connectFailureLogged;
    private volatile boolean parseFailureLogged;

    private final ConcurrentLinkedQueue<String> sendQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean sendInFlight = new AtomicBoolean();

    private volatile Set<BHapticsPosition> connectedPositions = Set.of();
    private volatile Set<String> activeKeys = Set.of();
    private volatile int connectedDeviceCount;

    // key -> parsed project json, resent on every (re)connect
    private final Map<String, Object> registeredPatterns = new LinkedHashMap<>();


    public BHapticsProvider(@NotNull XRProvider vrProvider) {
        this(vrProvider, "AtumVR", "AtumVR");
    }

    public BHapticsProvider(@NotNull XRProvider vrProvider,
                            @NotNull String appId,
                            @NotNull String appName) {
        this.vrProvider = vrProvider;
        this.appId = appId;
        this.appName = appName;
    }


    // -------- LIFECYCLE --------

    @Override
    public boolean isSupported() {
        if (supported == null) {
            supported = detectPlayer();
        }
        return supported;
    }

    private boolean detectPlayer() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(PLAYER_HOST, PLAYER_PORT), DETECT_TIMEOUT_MS);
            return true;
        } catch (IOException e) {
            vrProvider.getLogger().logInfo(
                    "bHaptics Player is not running (nothing listens on "
                            + PLAYER_HOST + ":" + PLAYER_PORT + ")"
            );
            return false;
        }
    }

    @Override
    public void onAttached() {
        // re-entrant after destroy(), a runtime re-enable starts a fresh connection
        shuttingDown = false;
        connecting.set(false);
        sendInFlight.set(false);
        connectFailureLogged = false;
        executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "AtumVR-BHaptics");
            thread.setDaemon(true);
            return thread;
        });
        httpClient = HttpClient.newBuilder()
                .executor(executor)
                .build();
        executor.scheduleWithFixedDelay(
                this::tryConnect,
                0, RECONNECT_DELAY_MS, TimeUnit.MILLISECONDS
        );
    }

    private void tryConnect() {
        if (shuttingDown || webSocket != null || !connecting.compareAndSet(false, true)) {
            return;
        }
        URI uri = URI.create(
                "ws://" + PLAYER_HOST + ":" + PLAYER_PORT + "/v2/feedbacks"
                        + "?app_id=" + URLEncoder.encode(appId, StandardCharsets.UTF_8)
                        + "&app_name=" + URLEncoder.encode(appName, StandardCharsets.UTF_8)
        );
        httpClient.newWebSocketBuilder()
                .buildAsync(uri, new PlayerListener())
                .whenComplete((socket, error) -> {
                    connecting.set(false);
                    if (shuttingDown) {
                        if (socket != null) {
                            socket.abort();
                        }
                        return;
                    }
                    if (error != null) {
                        if (!connectFailureLogged) {
                            connectFailureLogged = true;
                            vrProvider.getLogger().logWarn(
                                    "bHaptics Player connection failed: "
                                            + rootMessage(error) + ", retrying in background"
                            );
                        }
                        return;
                    }
                    connectFailureLogged = false;
                    webSocket = socket;
                    vrProvider.getLogger().logInfo("Connected to bHaptics Player");
                    resendRegisteredPatterns();
                });
    }

    private void handleDisconnect(@NotNull String reason) {
        boolean wasConnected = webSocket != null;
        webSocket = null;
        connectedPositions = Set.of();
        activeKeys = Set.of();
        connectedDeviceCount = 0;
        sendQueue.clear();
        if (wasConnected && !shuttingDown) {
            vrProvider.getLogger().logWarn(
                    "Lost bHaptics Player connection (" + reason + "), reconnecting in background"
            );
        }
    }

    @Override
    public void destroy() {
        shuttingDown = true;
        WebSocket socket = webSocket;
        webSocket = null;
        if (socket != null) {
            try {
                awaitSendIdle(200);
                socket.sendText(BHapticsPackets.turnOffAll(), true)
                        .get(300, TimeUnit.MILLISECONDS);
                socket.sendClose(WebSocket.NORMAL_CLOSURE, "shutdown")
                        .get(300, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                socket.abort();
            }
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        httpClient = null;
        sendQueue.clear();
        connectedPositions = Set.of();
        activeKeys = Set.of();
        connectedDeviceCount = 0;
        // registered patterns are kept, they get resent when reconnected after a re-enable
    }

    private void awaitSendIdle(long timeoutMillis) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (sendInFlight.get() && System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }


    // -------- SENDING --------

    private void submit(@NotNull String packet) {
        if (shuttingDown || webSocket == null) {
            return;
        }
        sendQueue.add(packet);
        drainQueue();
    }

    // websocket allows a single outstanding text send
    private void drainQueue() {
        if (!sendInFlight.compareAndSet(false, true)) {
            return;
        }
        WebSocket socket = webSocket;
        String next = socket == null ? null : sendQueue.poll();
        if (next == null) {
            sendInFlight.set(false);
            if (socket != null && !sendQueue.isEmpty()) {
                drainQueue();
            }
            return;
        }
        socket.sendText(next, true).whenComplete((result, error) -> {
            sendInFlight.set(false);
            if (error != null) {
                handleDisconnect("send failed: " + rootMessage(error));
                return;
            }
            if (!sendQueue.isEmpty()) {
                drainQueue();
            }
        });
    }

    private void resendRegisteredPatterns() {
        synchronized (registeredPatterns) {
            for (Map.Entry<String, Object> entry : registeredPatterns.entrySet()) {
                submit(BHapticsPackets.register(entry.getKey(), entry.getValue()));
            }
        }
    }


    // -------- RECEIVING --------

    private final class PlayerListener implements WebSocket.Listener {

        private final StringBuilder buffer = new StringBuilder();

        @Override
        public void onOpen(WebSocket socket) {
            socket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                String message = buffer.toString();
                buffer.setLength(0);
                handleMessage(message);
            }
            socket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket socket, int statusCode, String reason) {
            handleDisconnect("closed with code " + statusCode);
            return null;
        }

        @Override
        public void onError(WebSocket socket, Throwable error) {
            handleDisconnect(rootMessage(error));
        }
    }

    private void handleMessage(@NotNull String message) {
        Object parsed;
        try {
            parsed = BHapticsJson.parse(message);
        } catch (Exception e) {
            if (!parseFailureLogged) {
                parseFailureLogged = true;
                vrProvider.getLogger().logError(
                        "Broken bHaptics Player message: " + e.getMessage()
                );
            }
            return;
        }
        parseFailureLogged = false;
        if (!(parsed instanceof Map<?, ?> map)) {
            return;
        }

        if (map.get("ConnectedPositions") instanceof List<?> positions) {
            EnumSet<BHapticsPosition> resolved = EnumSet.noneOf(BHapticsPosition.class);
            for (Object entry : positions) {
                if (entry instanceof String key) {
                    BHapticsPosition position = BHapticsPosition.fromKey(key);
                    if (position != null) {
                        resolved.add(position);
                    }
                }
            }
            connectedPositions = Collections.unmodifiableSet(resolved);
        }
        if (map.get("ConnectedDeviceCount") instanceof Double count) {
            connectedDeviceCount = count.intValue();
        }
        if (map.get("ActiveKeys") instanceof List<?> keys) {
            Set<String> resolved = new java.util.HashSet<>(Math.max(4, keys.size() * 2));
            for (Object entry : keys) {
                if (entry instanceof String key) {
                    resolved.add(key);
                }
            }
            activeKeys = Collections.unmodifiableSet(resolved);
        }
    }


    // -------- BODY HAPTICS BRIDGE --------

    @Override
    public boolean isActive() {
        return webSocket != null && connectedDeviceCount > 0;
    }

    @Override
    public @Nullable BHaptics asBHaptics() {
        return this;
    }


    // -------- BHAPTICS API --------

    @Override
    public boolean isPlayerConnected() {
        return webSocket != null;
    }

    @Override
    public @NotNull Set<BHapticsPosition> getConnectedPositions() {
        return connectedPositions;
    }

    @Override
    public boolean isDeviceConnected(@NotNull BHapticsPosition position) {
        Set<BHapticsPosition> positions = connectedPositions;
        if (positions.contains(position)) {
            return true;
        }
        return (position == BHapticsPosition.VEST_FRONT || position == BHapticsPosition.VEST_BACK)
                && positions.contains(BHapticsPosition.VEST);
    }

    @Override
    public void playDots(@NotNull String key,
                         @NotNull BHapticsPosition position,
                         int durationMillis,
                         @NotNull BHapticsDotPoint... points) {
        submit(BHapticsPackets.frame(key, position, durationMillis, points, NO_PATHS));
    }

    @Override
    public void playPath(@NotNull String key,
                         @NotNull BHapticsPosition position,
                         int durationMillis,
                         @NotNull BHapticsPathPoint... points) {
        submit(BHapticsPackets.frame(key, position, durationMillis, NO_DOTS, points));
    }

    @Override
    public void registerPattern(@NotNull String key, @NotNull String tactJson) {
        Object project;
        try {
            Object parsed = BHapticsJson.parse(tactJson);
            if (parsed instanceof Map<?, ?> map && map.containsKey("project")) {
                project = map.get("project");
            } else {
                project = parsed;
            }
            if (!(project instanceof Map<?, ?>)) {
                throw new IllegalArgumentException("no project object found");
            }
        } catch (Exception e) {
            vrProvider.getLogger().logError(
                    "Broken tact pattern '" + key + "': " + e.getMessage()
            );
            return;
        }
        synchronized (registeredPatterns) {
            registeredPatterns.put(key, project);
        }
        submit(BHapticsPackets.register(key, project));
    }

    @Override
    public boolean isPatternRegistered(@NotNull String key) {
        synchronized (registeredPatterns) {
            return registeredPatterns.containsKey(key);
        }
    }

    @Override
    public void playRegistered(@NotNull String key,
                               float intensityScale, float durationScale,
                               float rotationAngleX, float rotationOffsetY) {
        submit(BHapticsPackets.playRegistered(
                key, intensityScale, durationScale, rotationAngleX, rotationOffsetY
        ));
    }

    @Override
    public boolean isPlaying(@NotNull String key) {
        return activeKeys.contains(key);
    }

    @Override
    public boolean isPlayingAny() {
        return !activeKeys.isEmpty();
    }

    @Override
    public void stop(@NotNull String key) {
        submit(BHapticsPackets.turnOff(key));
    }

    @Override
    public void stopAll() {
        submit(BHapticsPackets.turnOffAll());
    }


    private static String rootMessage(@NotNull Throwable error) {
        Throwable current = error;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.isBlank()
                ? current.getClass().getSimpleName()
                : message;
    }
}
