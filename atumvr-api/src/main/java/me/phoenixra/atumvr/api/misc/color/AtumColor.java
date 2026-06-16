package me.phoenixra.atumvr.api.misc.color;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;

/**
 * An RGBA color stored as normalized float components.
 */
public interface AtumColor {

    /** Symbol used in color-codes formatting. */
    String COLOR_SYMBOL = "§";

    AtumColorImmutable WHITE      = immutable(1.0f, 1.0f, 1.0f);
    AtumColorImmutable BLACK      = immutable(0.0f, 0.0f, 0.0f);
    AtumColorImmutable RED        = immutable(1.0f, 0.0f, 0.0f);
    AtumColorImmutable GREEN      = immutable(0.0f, 1.0f, 0.0f);
    AtumColorImmutable BLUE       = immutable(0.0f, 0.0f, 1.0f);
    AtumColorImmutable YELLOW     = immutable(1.0f, 1.0f, 0.0f);
    AtumColorImmutable CYAN       = immutable(0.0f, 1.0f, 1.0f);
    AtumColorImmutable MAGENTA    = immutable(1.0f, 0.0f, 1.0f);
    AtumColorImmutable GRAY       = immutable(0.5f, 0.5f, 0.5f);
    AtumColorImmutable DARK_GRAY  = immutable(0.25f, 0.25f, 0.25f);
    AtumColorImmutable LIGHT_GRAY = immutable(0.75f, 0.75f, 0.75f);
    AtumColorImmutable ORANGE     = immutable(1.0f, 0.5f, 0.0f);
    AtumColorImmutable PINK       = immutable(1.0f, 0.68f, 0.68f);
    AtumColorImmutable PURPLE     = immutable(0.5f, 0.0f, 0.5f);
    AtumColorImmutable BROWN      = immutable(0.5f, 0.25f, 0.0f);
    AtumColorImmutable LIME       = immutableFromHex("#39FF14");


    // ======= COMPONENTS =======

    float getRed();

    float getGreen();

    float getBlue();

    float getAlpha();

    int getRedInt();

    int getGreenInt();

    int getBlueInt();

    int getAlphaInt();


    // ======= OPERATIONS =======

    /**
     * Blends towards {@code other}: ratio 0.0 keeps this color, 1.0 gives {@code other}.
     */
    @NotNull AtumColor blend(@NotNull AtumColor other, float ratio);

    /**
     * Multiplies the RGB channels by the given factor; alpha is unchanged.
     */
    @NotNull AtumColor multiply(float multiplier);

    /**
     * Replaces each RGB channel with its complement ({@code 1 - value});
     * alpha is unchanged.
     */
    @NotNull AtumColor invert();

    /**
     * Converts to grayscale using BT.601 luma weights (0.3, 0.59, 0.11).
     */
    @NotNull AtumColor toGrayscale();

    @NotNull AtumColor withAlpha(float newAlpha);

    /**
     * Lightens by blending towards {@link #WHITE}.
     */
    default @NotNull AtumColor lighten(float factor) {
        return blend(WHITE, factor);
    }

    /**
     * Darkens by blending towards {@link #BLACK}.
     */
    default @NotNull AtumColor darken(float factor) {
        return blend(BLACK, factor);
    }

    /**
     * Alias for {@link #blend(AtumColor, float)}.
     */
    default @NotNull AtumColor lerp(@NotNull AtumColor other, float t) {
        return blend(other, t);
    }


    // ======= ALLOCATION-FREE VARIANTS =======

    default float[] blend(@NotNull AtumColor other, float ratio, float[] out) {
        float r = getRed(), g = getGreen(), b = getBlue();
        out[0] = r + ratio * (other.getRed() - r);
        out[1] = g + ratio * (other.getGreen() - g);
        out[2] = b + ratio * (other.getBlue() - b);
        if (out.length > 3) {
            float a = getAlpha();
            out[3] = a + ratio * (other.getAlpha() - a);
        }
        return out;
    }

    default int[] blend(@NotNull AtumColor other, float ratio, int[] out) {
        float r = getRed(), g = getGreen(), b = getBlue();
        out[0] = floatToInt(r + ratio * (other.getRed() - r));
        out[1] = floatToInt(g + ratio * (other.getGreen() - g));
        out[2] = floatToInt(b + ratio * (other.getBlue() - b));
        if (out.length > 3) {
            float a = getAlpha();
            out[3] = floatToInt(a + ratio * (other.getAlpha() - a));
        }
        return out;
    }

    default float[] multiply(float multiplier, float[] out) {
        out[0] = getRed() * multiplier;
        out[1] = getGreen() * multiplier;
        out[2] = getBlue() * multiplier;
        if (out.length > 3) out[3] = getAlpha();
        return out;
    }

    default int[] multiply(float multiplier, int[] out) {
        out[0] = floatToInt(getRed() * multiplier);
        out[1] = floatToInt(getGreen() * multiplier);
        out[2] = floatToInt(getBlue() * multiplier);
        if (out.length > 3) out[3] = getAlphaInt();
        return out;
    }

    default float[] invert(float[] out) {
        out[0] = 1f - getRed();
        out[1] = 1f - getGreen();
        out[2] = 1f - getBlue();
        if (out.length > 3) out[3] = getAlpha();
        return out;
    }

    default int[] invert(int[] out) {
        out[0] = floatToInt(1f - getRed());
        out[1] = floatToInt(1f - getGreen());
        out[2] = floatToInt(1f - getBlue());
        if (out.length > 3) out[3] = getAlphaInt();
        return out;
    }

    default float[] toGrayscale(float[] out) {
        float luminance = 0.3f * getRed() + 0.59f * getGreen() + 0.11f * getBlue();
        out[0] = out[1] = out[2] = luminance;
        if (out.length > 3) out[3] = getAlpha();
        return out;
    }

    default int[] toGrayscale(int[] out) {
        int luminance = floatToInt(0.3f * getRed() + 0.59f * getGreen() + 0.11f * getBlue());
        out[0] = out[1] = out[2] = luminance;
        if (out.length > 3) out[3] = getAlphaInt();
        return out;
    }

    default float[] withAlpha(float newAlpha, float[] out) {
        out[0] = getRed();
        out[1] = getGreen();
        out[2] = getBlue();
        if (out.length > 3) out[3] = newAlpha;
        return out;
    }

    default int[] withAlpha(float newAlpha, int[] out) {
        out[0] = getRedInt();
        out[1] = getGreenInt();
        out[2] = getBlueInt();
        if (out.length > 3) out[3] = floatToInt(newAlpha);
        return out;
    }


    // ======= ANALYSIS =======

    /**
     * WCAG 2.x contrast ratio between this color and {@code other},
     * from 1 (none) to 21 (max).
     */
    default double getContrastRatio(@NotNull AtumColor other) {
        double l1 = getRelativeLuminance();
        double l2 = other.getRelativeLuminance();
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        return (lighter + 0.05) / (darker + 0.05);
    }

    /**
     * WCAG relative luminance of this color: 0.0 for black, 1.0 for white.
     */
    default double getRelativeLuminance() {
        return 0.2126 * linearizeChannel(getRed())
                + 0.7152 * linearizeChannel(getGreen())
                + 0.0722 * linearizeChannel(getBlue());
    }


    // ======= CONVERSIONS =======

    /**
     * @return {hue, saturation, brightness}, each 0.0–1.0
     */
    default float[] toHSB() {
        return Color.RGBtoHSB(getRedInt(), getGreenInt(), getBlueInt(), null);
    }

    /**
     * New {r, g, b} or {r, g, b, a} array (0–255).
     * <p>
     *     Use {@link #asIntArray(int[])} on hot paths.
     * </p>
     */
    default int[] asIntArray(boolean withAlpha) {
        return withAlpha
                ? new int[]{getRedInt(), getGreenInt(), getBlueInt(), getAlphaInt()}
                : new int[]{getRedInt(), getGreenInt(), getBlueInt()};
    }

    default int[] asIntArray(int[] out) {
        out[0] = getRedInt();
        out[1] = getGreenInt();
        out[2] = getBlueInt();
        if (out.length > 3) out[3] = getAlphaInt();
        return out;
    }


    /** New {r, g, b} or {r, g, b, a} array (0.0–1.0).
     *  <p>
     *      Use {@link #asFloatArray(float[])} on hot paths.
     *  </p>
     */
    default float[] asFloatArray(boolean withAlpha) {
        return withAlpha
                ? new float[]{getRed(), getGreen(), getBlue(), getAlpha()}
                : new float[]{getRed(), getGreen(), getBlue()};
    }

    default float[] asFloatArray(float[] out) {
        out[0] = getRed();
        out[1] = getGreen();
        out[2] = getBlue();
        if (out.length > 3) out[3] = getAlpha();
        return out;
    }

    /** Packs as {@code 0xAARRGGBB}. */
    default int asInt() {
        return asInt(true);
    }

    /**
     * Packs as {@code 0xAARRGGBB}, or {@code 0x00RRGGBB} if {@code withAlpha} is false.
     */
    default int asInt(boolean withAlpha) {
        int value = (getRedInt() << 16) | (getGreenInt() << 8) | getBlueInt();
        if (withAlpha) {
            value |= getAlphaInt() << 24;
        }
        return value;
    }

    /** Formats as {@code "r;g;b;a"} with 0–255 values. */
    default @NotNull String asString() {
        return asString(true);
    }

    /** Formats as {@code "r;g;b"} or {@code "r;g;b;a"} with 0–255 values. */
    default @NotNull String asString(boolean withAlpha) {
        if (withAlpha) {
            return getRedInt() + ";" + getGreenInt() + ";" + getBlueInt() + ";" + getAlphaInt();
        }
        return getRedInt() + ";" + getGreenInt() + ";" + getBlueInt();
    }

    /** Formats as {@code "#rrggbb"} or {@code "#rrggbbaa"}. */
    default @NotNull String asHex(boolean withAlpha) {
        if (withAlpha) {
            return String.format("#%02x%02x%02x%02x",
                    getRedInt(), getGreenInt(), getBlueInt(), getAlphaInt());
        }
        return String.format("#%02x%02x%02x", getRedInt(), getGreenInt(), getBlueInt());
    }

    default @NotNull Color asAwtColor() {
        return new Color(getRedInt(), getGreenInt(), getBlueInt(), getAlphaInt());
    }


    // ======= FACTORIES =======

    static @NotNull AtumColorMutable mutable(float red, float green, float blue, float alpha) {
        return new AtumColorMutable(red, green, blue, alpha);
    }

    static @NotNull AtumColorMutable mutable(float red, float green, float blue) {
        return new AtumColorMutable(red, green, blue, 1.0f);
    }

    static @NotNull AtumColorMutable mutable(int red, int green, int blue, int alpha) {
        return new AtumColorMutable(red, green, blue, alpha);
    }

    static @NotNull AtumColorMutable mutable(int red, int green, int blue) {
        return new AtumColorMutable(red, green, blue, 255);
    }

    /** @param color {@code 0xAARRGGBB} if {@code hasAlpha}, otherwise {@code 0xRRGGBB} */
    static @NotNull AtumColorMutable mutable(int color, boolean hasAlpha) {
        return new AtumColorMutable(color, hasAlpha);
    }

    /** @param colorString {@code "r;g;b"} or {@code "r;g;b;a"} with 0–255 values */
    static @NotNull AtumColorMutable mutableFromString(@NotNull String colorString) {
        int[] v = valuesFromString(colorString);
        return new AtumColorMutable(v[0], v[1], v[2], v[3]);
    }

    /** @param hex {@code "#RRGGBB"} or {@code "#RRGGBBAA"}, the {@code #} is optional */
    static @NotNull AtumColorMutable mutableFromHex(@NotNull String hex) {
        int[] v = valuesFromHex(hex);
        return new AtumColorMutable(v[0], v[1], v[2], v[3]);
    }

    static @NotNull AtumColorImmutable immutable(float red, float green, float blue, float alpha) {
        return new AtumColorImmutable(red, green, blue, alpha);
    }

    static @NotNull AtumColorImmutable immutable(float red, float green, float blue) {
        return new AtumColorImmutable(red, green, blue, 1.0f);
    }

    static @NotNull AtumColorImmutable immutable(int red, int green, int blue, int alpha) {
        return new AtumColorImmutable(red, green, blue, alpha);
    }

    static @NotNull AtumColorImmutable immutable(int red, int green, int blue) {
        return new AtumColorImmutable(red, green, blue, 255);
    }

    /** @param color {@code 0xAARRGGBB} if {@code hasAlpha}, otherwise {@code 0xRRGGBB} */
    static @NotNull AtumColorImmutable immutable(int color, boolean hasAlpha) {
        return new AtumColorImmutable(color, hasAlpha);
    }

    /** @param colorString {@code "r;g;b"} or {@code "r;g;b;a"} with 0–255 values */
    static @NotNull AtumColorImmutable immutableFromString(@NotNull String colorString) {
        int[] v = valuesFromString(colorString);
        return new AtumColorImmutable(v[0], v[1], v[2], v[3]);
    }

    /** @param hex {@code "#RRGGBB"} or {@code "#RRGGBBAA"}, the {@code #} is optional */
    static @NotNull AtumColorImmutable immutableFromHex(@NotNull String hex) {
        int[] v = valuesFromHex(hex);
        return new AtumColorImmutable(v[0], v[1], v[2], v[3]);
    }


    // ======= PARSING & UTILITIES =======

    /**
     * Parses {@code "#RRGGBB"} or {@code "#RRGGBBAA"} (the {@code #} is optional) into
     * {red, green, blue, alpha} components in the 0–255 range; alpha defaults to 255.
     *
     * @throws IllegalArgumentException if the string is not a valid hex color
     */
    static int[] valuesFromHex(@NotNull String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() != 6 && hex.length() != 8) {
            throw new IllegalArgumentException(
                    "Invalid hex color, expected RRGGBB or RRGGBBAA: " + hex
            );
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        int a = hex.length() == 8 ? Integer.parseInt(hex.substring(6, 8), 16) : 255;
        return new int[]{r, g, b, a};
    }

    /**
     * Parses {@code "r;g;b"} or {@code "r;g;b;a"} into {red, green, blue, alpha} components
     * clamped to the 0–255 range; alpha defaults to 255.
     *
     * @throws IllegalArgumentException if the string is not a valid color string
     */
    static int[] valuesFromString(@NotNull String colorString) {
        String[] parts = colorString.split(";");
        if (parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException(
                    "Invalid color string, expected 'r;g;b' or 'r;g;b;a': " + colorString
            );
        }
        int r = clampColorValue(Integer.parseInt(parts[0].trim()));
        int g = clampColorValue(Integer.parseInt(parts[1].trim()));
        int b = clampColorValue(Integer.parseInt(parts[2].trim()));
        int a = parts.length == 4 ? clampColorValue(Integer.parseInt(parts[3].trim())) : 255;
        return new int[]{r, g, b, a};
    }

    /**
     *  Converts a normalized channel (0.0–1.0) to 0–255, clamping out-of-range input.
     */
    static int floatToInt(float value) {
        if (value <= 0f) return 0;
        if (value >= 1f) return 255;
        return Math.round(value * 255f);
    }

    /**
     * sRGB linearization of a single channel, as defined by WCAG relative luminance.
     */
    static double linearizeChannel(double channel) {
        return channel <= 0.03928 ? channel / 12.92 : Math.pow((channel + 0.055) / 1.055, 2.4);
    }

    /** Clamps to the 0–255 range. */
    static int clampColorValue(int value) {
        return Math.max(0, Math.min(255, value));
    }
}