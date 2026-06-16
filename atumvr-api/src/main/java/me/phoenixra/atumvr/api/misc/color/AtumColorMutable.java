package me.phoenixra.atumvr.api.misc.color;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Mutable {@link AtumColor}. All channel operations modify this instance and return {@code this}.
 * <p>
 * Intended as a reusable scratch color on hot paths (e.g. per-frame tinting) to avoid
 * allocations. Not thread-safe. Integer getters are computed on demand from the floats.
 */
@Getter
public class AtumColorMutable implements AtumColor {

    private float red;
    private float green;
    private float blue;
    private float alpha;

    public AtumColorMutable(float red, float green, float blue, float alpha) {
        set(red, green, blue, alpha);
    }

    public AtumColorMutable(int red, int green, int blue, int alpha) {
        set(red, green, blue, alpha);
    }

    public AtumColorMutable(int color, boolean hasAlpha) {
        this(
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                color & 0xFF,
                hasAlpha ? (color >> 24) & 0xFF : 0xFF
        );
    }

    public @NotNull AtumColorMutable set(int red, int green, int blue, int alpha) {
        this.red = AtumColor.clampColorValue(red) / 255f;
        this.green = AtumColor.clampColorValue(green) / 255f;
        this.blue = AtumColor.clampColorValue(blue) / 255f;
        this.alpha = AtumColor.clampColorValue(alpha) / 255f;
        return this;
    }
    public @NotNull AtumColorMutable set(int red, int green, int blue) {
        this.red = AtumColor.clampColorValue(red) / 255f;
        this.green = AtumColor.clampColorValue(green) / 255f;
        this.blue = AtumColor.clampColorValue(blue) / 255f;
        return this;
    }
    public @NotNull AtumColorMutable set(int[] colors) {
        this.red = AtumColor.clampColorValue(colors[0]) / 255f;
        this.green = AtumColor.clampColorValue(colors[1]) / 255f;
        this.blue = AtumColor.clampColorValue(colors[2]) / 255f;

        this.alpha = colors.length > 3
                ? AtumColor.clampColorValue(colors[3]) / 255f
                : 1.0f;

        return this;
    }

    public @NotNull AtumColorMutable set(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        return this;
    }
    public @NotNull AtumColorMutable set(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        return this;
    }
    public @NotNull AtumColorMutable set(float[] colors) {
        this.red = colors[0];
        this.green = colors[1];
        this.blue = colors[2];

        this.alpha = colors.length > 3
                ? colors[3]
                : 1.0f;

        return this;
    }

    /** Copies all components from {@code other} into this instance. */
    public @NotNull AtumColorMutable set(@NotNull AtumColor other) {
        return set(other.getRed(), other.getGreen(), other.getBlue(), other.getAlpha());
    }

    @Override
    public int getRedInt() {
        return AtumColor.floatToInt(red);
    }

    @Override
    public int getGreenInt() {
        return AtumColor.floatToInt(green);
    }

    @Override
    public int getBlueInt() {
        return AtumColor.floatToInt(blue);
    }

    @Override
    public int getAlphaInt() {
        return AtumColor.floatToInt(alpha);
    }

    @Override
    public @NotNull AtumColorMutable blend(@NotNull AtumColor other, float ratio) {
        red += ratio * (other.getRed() - red);
        green += ratio * (other.getGreen() - green);
        blue += ratio * (other.getBlue() - blue);
        alpha += ratio * (other.getAlpha() - alpha);
        return this;
    }

    @Override
    public @NotNull AtumColorMutable multiply(float multiplier) {
        red *= multiplier;
        green *= multiplier;
        blue *= multiplier;
        return this;
    }

    @Override
    public @NotNull AtumColorMutable invert() {
        red = 1f - red;
        green = 1f - green;
        blue = 1f - blue;
        return this;
    }

    @Override
    public @NotNull AtumColorMutable toGrayscale() {
        float luminance = 0.3f * red + 0.59f * green + 0.11f * blue;
        red = green = blue = luminance;
        return this;
    }

    @Override
    public @NotNull AtumColorMutable withAlpha(float newAlpha) {
        alpha = newAlpha;
        return this;
    }

    public @NotNull AtumColorImmutable asImmutable() {
        return new AtumColorImmutable(red, green, blue, alpha);
    }

    @Override
    public String toString() {
        return "AtumColorMutable{red=" + red + ", green=" + green
                + ", blue=" + blue + ", alpha=" + alpha + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtumColor)) return false;
        AtumColor that = (AtumColor) o;
        return Float.compare(that.getRed(), red) == 0
                && Float.compare(that.getGreen(), green) == 0
                && Float.compare(that.getBlue(), blue) == 0
                && Float.compare(that.getAlpha(), alpha) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }
}