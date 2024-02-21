package me.phoenixra.atumvr.api.misc;

import lombok.Getter;

import java.awt.*;

/**
 * Use this class to store colors and
 * draw colored stuff with OpenGL.
 */
public class AtumColor implements Cloneable{
    public static final AtumColor WHITE = new AtumColor(1,1,1,1);
    public static final AtumColor BLACK = new AtumColor(0,0,0,1);
    public static final AtumColor RED = new AtumColor(1,0,0,1);
    public static final AtumColor GREEN = new AtumColor(0,1,0,1);
    public static final AtumColor LIME = fromHex("#39FF14");
    public static final AtumColor BLUE = new AtumColor(0,0,1,1);
    public static final AtumColor YELLOW = new AtumColor(1,1,0,1);
    public static final AtumColor CYAN = new AtumColor(0,1,1,1);
    public static final AtumColor MAGENTA = new AtumColor(1,0,1,1);
    public static final AtumColor GRAY = new AtumColor(0.5f,0.5f,0.5f,1);
    public static final AtumColor DARK_GRAY = new AtumColor(0.25f,0.25f,0.25f,1);
    public static final AtumColor LIGHT_GRAY = new AtumColor(0.75f,0.75f,0.75f,1);
    public static final AtumColor ORANGE = new AtumColor(1,0.5f,0,1);
    public static final AtumColor PINK = new AtumColor(1,0.68f,0.68f,1);
    public static final AtumColor PURPLE = new AtumColor(0.5f,0,0.5f,1);
    public static final AtumColor BROWN = new AtumColor(0.5f,0.25f,0,1);

    @Getter
    private float red;
    @Getter
    private float green;
    @Getter
    private float blue;
    @Getter
    private float alpha;

    public AtumColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    public AtumColor(float red, float green, float blue) {
        this(red, green, blue, 1);
    }


    public int toInt() {
        return ((int) (red * 255) << 16) | ((int) (green * 255) << 8) | (int) (blue * 255) | ((int) (alpha * 255) << 24);
    }
    public String toHex(boolean withAlpha){
        if(withAlpha){
            return String.format("#%02x%02x%02x%02x", (int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
        }
        return String.format("#%02x%02x%02x", (int) (red * 255), (int) (green * 255), (int) (blue * 255));
    }

    //from string hex rgba
    public static AtumColor fromHex(String hex) {
        try {
            hex = hex.substring(1);

            if (hex.length() == 6) {

                return AtumColor.from(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16)
                );
            }
            if (hex.length() == 8) {
                return AtumColor.from(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        Integer.valueOf(hex.substring(6, 8), 16)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AtumColor.WHITE;
    }

    public static AtumColor from(float red, float green, float blue, float alpha) {
        return new AtumColor(
                red / 255,
                green / 255,
                blue / 255,
                alpha / 255
        );
    }
    public static AtumColor from(float red, float green, float blue) {
        return new AtumColor(
                red / 255,
                green / 255,
                blue / 255
        );
    }
    //from int rgba where a >0 <=1
    public static AtumColor from(int color, boolean hasAlpha) {
        if(hasAlpha){
            return new AtumColor(
                    (color >> 16 & 0xFF) / 255f,
                    (color >> 8 & 0xFF) / 255f,
                    (color & 0xFF) / 255f
            );
        }
        return new AtumColor(
                (color >> 16 & 0xFF) / 255f,
                (color >> 8 & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                (color >> 24 & 0xFF) / 255f
        );
    }

    public int getRedAsInteger(){
        if(red<=0) return 0;
        if(red>=1) return 255;
        return  (int)(red*255);
    }
    public int getGreenAsInteger(){
        if(green<=0) return 0;
        if(green>=1) return 255;
        return  (int)(green*255);
    }
    public int getBlueAsInteger(){
        if(blue<=0) return 0;
        if(blue>=1) return 255;
        return  (int)(blue*255);
    }
    public int getAlphaAsInteger(){
        if(alpha<=0) return 0;
        if(alpha>=1) return 255;
        return  (int)(alpha*255);
    }

    public Color asAwtColor(){
        return new Color(
                getRed(),
                getGreen(),
                getBlue(),
                getAlpha()
        );
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AtumColor){
            AtumColor color = (AtumColor) obj;
            return color.red == red && color.green == green && color.blue == blue && color.alpha == alpha;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toInt();
    }

    @Override
    public String toString() {
        return "AtumColor{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", alpha=" + alpha +
                '}';
    }

    @Override
    public AtumColor clone() throws CloneNotSupportedException{
        AtumColor color = (AtumColor) super.clone();
        color.red = red;
        color.green = green;
        color.blue = blue;
        color.alpha = alpha;
        return color;
    }
}
