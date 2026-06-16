package me.phoenixra.atumvr.api.misc.color;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Immutable {@link AtumColor}. All channel operations return a new instance.
 * <p>
 * Integer components are precomputed, so the int getters are free.
 */
@Getter
public class AtumColorImmutable implements AtumColor {

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    private final int redInt;
    private final int greenInt;
    private final int blueInt;
    private final int alphaInt;

    public AtumColorImmutable(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.redInt = AtumColor.floatToInt(red);
        this.greenInt = AtumColor.floatToInt(green);
        this.blueInt = AtumColor.floatToInt(blue);
        this.alphaInt = AtumColor.floatToInt(alpha);
    }

    public AtumColorImmutable(int red, int green, int blue, int alpha) {
        this.redInt = AtumColor.clampColorValue(red);
        this.greenInt = AtumColor.clampColorValue(green);
        this.blueInt = AtumColor.clampColorValue(blue);
        this.alphaInt = AtumColor.clampColorValue(alpha);
        this.red = this.redInt / 255f;
        this.green = this.greenInt / 255f;
        this.blue = this.blueInt / 255f;
        this.alpha = this.alphaInt / 255f;
    }

    public AtumColorImmutable(int color, boolean hasAlpha) {
        this(
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                color & 0xFF,
                hasAlpha ? (color >> 24) & 0xFF : 0xFF
        );
    }

    @Override
    public @NotNull AtumColorImmutable blend(@NotNull AtumColor other, float ratio) {
        return new AtumColorImmutable(
                red + ratio * (other.getRed() - red),
                green + ratio * (other.getGreen() - green),
                blue + ratio * (other.getBlue() - blue),
                alpha + ratio * (other.getAlpha() - alpha)
        );
    }

    @Override
    public @NotNull AtumColorImmutable multiply(float multiplier) {
        return new AtumColorImmutable(red * multiplier, green * multiplier, blue * multiplier, alpha);
    }

    @Override
    public @NotNull AtumColorImmutable invert() {
        return new AtumColorImmutable(1f - red, 1f - green, 1f - blue, alpha);
    }

    @Override
    public @NotNull AtumColorImmutable toGrayscale() {
        float luminance = 0.3f * red + 0.59f * green + 0.11f * blue;
        return new AtumColorImmutable(luminance, luminance, luminance, alpha);
    }

    @Override
    public @NotNull AtumColorImmutable withAlpha(float newAlpha) {
        return new AtumColorImmutable(red, green, blue, newAlpha);
    }

    public @NotNull AtumColorMutable asMutable() {
        return new AtumColorMutable(red, green, blue, alpha);
    }

    @Override
    public String toString() {
        return "AtumColorImmutable{red=" + red + ", green=" + green
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