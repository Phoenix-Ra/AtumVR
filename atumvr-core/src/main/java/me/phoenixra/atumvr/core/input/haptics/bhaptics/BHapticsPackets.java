package me.phoenixra.atumvr.core.input.haptics.bhaptics;

import me.phoenixra.atumvr.api.input.haptics.bhaptics.BHapticsDotPoint;
import me.phoenixra.atumvr.api.input.haptics.bhaptics.BHapticsPathPoint;
import me.phoenixra.atumvr.api.input.haptics.bhaptics.BHapticsPosition;
import org.jetbrains.annotations.NotNull;


final class BHapticsPackets {

    private BHapticsPackets() {
    }

    static @NotNull String frame(@NotNull String key,
                                 @NotNull BHapticsPosition position,
                                 int durationMillis,
                                 @NotNull BHapticsDotPoint[] dotPoints,
                                 @NotNull BHapticsPathPoint[] pathPoints) {
        StringBuilder out = new StringBuilder(96 + dotPoints.length * 32 + pathPoints.length * 48);
        out.append("{\"Submit\":[{\"type\":\"frame\",\"key\":");
        BHapticsJson.writeString(out, key);
        out.append(",\"Frame\":{\"durationMillis\":").append(Math.max(durationMillis, 0));
        out.append(",\"position\":\"").append(position.getKey()).append('"');

        out.append(",\"dotPoints\":[");
        for (int i = 0; i < dotPoints.length; i++) {
            BHapticsDotPoint point = dotPoints[i];
            if (i > 0) {
                out.append(',');
            }
            out.append("{\"index\":").append(point.index())
                    .append(",\"intensity\":").append(point.intensity())
                    .append('}');
        }

        out.append("],\"pathPoints\":[");
        for (int i = 0; i < pathPoints.length; i++) {
            BHapticsPathPoint point = pathPoints[i];
            if (i > 0) {
                out.append(',');
            }
            out.append("{\"x\":").append(point.x())
                    .append(",\"y\":").append(point.y())
                    .append(",\"intensity\":").append(point.intensity())
                    .append(",\"motorCount\":").append(point.motorCount())
                    .append('}');
        }

        out.append("]}}]}");
        return out.toString();
    }

    static @NotNull String playRegistered(@NotNull String key,
                                          float intensityScale, float durationScale,
                                          float rotationAngleX, float rotationOffsetY) {
        StringBuilder out = new StringBuilder(128);
        out.append("{\"Submit\":[{\"type\":\"key\",\"key\":");
        BHapticsJson.writeString(out, key);

        boolean scaled = intensityScale != 1f || durationScale != 1f;
        boolean rotated = rotationAngleX != 0f || rotationOffsetY != 0f;
        if (scaled || rotated) {
            out.append(",\"Parameters\":{");
            if (scaled) {
                out.append("\"scaleOption\":{\"intensity\":").append(intensityScale)
                        .append(",\"duration\":").append(durationScale)
                        .append('}');
            }
            if (rotated) {
                if (scaled) {
                    out.append(',');
                }
                out.append("\"rotationOption\":{\"offsetAngleX\":").append(rotationAngleX)
                        .append(",\"offsetY\":").append(rotationOffsetY)
                        .append('}');
            }
            out.append('}');
        }

        out.append("}]}");
        return out.toString();
    }

    static @NotNull String turnOff(@NotNull String key) {
        StringBuilder out = new StringBuilder(48);
        out.append("{\"Submit\":[{\"type\":\"turnOff\",\"key\":");
        BHapticsJson.writeString(out, key);
        out.append("}]}");
        return out.toString();
    }

    static @NotNull String turnOffAll() {
        return "{\"Submit\":[{\"type\":\"turnOffAll\"}]}";
    }

    static @NotNull String register(@NotNull String key, @NotNull Object project) {
        StringBuilder out = new StringBuilder(1024);
        out.append("{\"Register\":[{\"key\":");
        BHapticsJson.writeString(out, key);
        out.append(",\"project\":");
        BHapticsJson.write(out, project);
        out.append("}]}");
        return out.toString();
    }
}
