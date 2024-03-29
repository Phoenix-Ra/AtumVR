package me.phoenixra.atumvr.api.misc;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Use this class to store colors and
 * draw colored stuff with OpenGL.
 */
public class AtumColor implements Cloneable{
    public static final AtumColor WHITE = new AtumColor(1.0f,1.0f,1.0f,1.0f,false);
    public static final AtumColor BLACK = new AtumColor(0.0f,0.0f,0.0f,1.0f,false);
    public static final AtumColor RED = new AtumColor(1.0f,0.0f,0.0f,1.0f,false);
    public static final AtumColor GREEN = new AtumColor(0.0f,1.0f,0.0f,1.0f,false);
    public static final AtumColor LIME = fromHex("#39FF14",false);
    public static final AtumColor BLUE = new AtumColor(0.0f,0.0f,1.0f,1.0f,false);
    public static final AtumColor YELLOW = new AtumColor(1.0f,1.0f,0.0f,1.0f,false);
    public static final AtumColor CYAN = new AtumColor(0.0f,1.0f,1.0f,1.0f,false);
    public static final AtumColor MAGENTA = new AtumColor(1.0f,0.0f,1.0f,1.0f,false);
    public static final AtumColor GRAY = new AtumColor(0.5f,0.5f,0.5f,1.0f,false);
    public static final AtumColor DARK_GRAY = new AtumColor(0.25f,0.25f,0.25f,1.0f,false);
    public static final AtumColor LIGHT_GRAY = new AtumColor(0.75f,0.75f,0.75f,1.0f,false);
    public static final AtumColor ORANGE = new AtumColor(1.0f,0.5f,0.0f,1.0f,false);
    public static final AtumColor PINK = new AtumColor(1.0f,0.68f,0.68f,1.0f,false);
    public static final AtumColor PURPLE = new AtumColor(0.5f,0.0f,0.5f,1.0f,false);
    public static final AtumColor BROWN = new AtumColor(0.5f,0.25f,0.0f,1.0f,false);


    @Getter
    private float red;
    @Getter
    private float green;
    @Getter
    private float blue;
    @Getter
    private float alpha;

    //added for optimization in case of often usage of int values
    @Getter
    private int redInt;
    @Getter
    private int greenInt;
    @Getter
    private int blueInt;
    @Getter
    private int alphaInt;

    private final boolean modifiable;
    public AtumColor(float red, float green, float blue, float alpha, boolean modifiable) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;

        this.redInt = floatToIntColor(red);
        this.greenInt = floatToIntColor(green);
        this.blueInt = floatToIntColor(blue);
        this.alphaInt = floatToIntColor(alpha);

        this.modifiable = modifiable;
    }
    public AtumColor(int red, int green, int blue, int alpha, boolean modifiable) {
        this.redInt = red;
        this.greenInt = green;
        this.blueInt = blue;
        this.alphaInt = alpha;

        this.red = ((float)red/255);
        this.green = ((float)green/255);
        this.blue = ((float)blue/255);
        this.alpha = ((float)alpha/255);

        this.modifiable = modifiable;
    }
    public AtumColor(float red, float green, float blue, float alpha) {
        this(red,green,blue,alpha,true);
    }
    public AtumColor(int red, int green, int blue, int alpha) {
        this(red,green,blue,alpha,true);
    }

    public AtumColor(float red, float green, float blue) {
        this(red, green, blue, 1);
    }
    public AtumColor(int red, int green, int blue) {
        this(red, green, blue, 1);
    }

    public void setRed(float red) {
        if(!modifiable) {
            throw new RuntimeException("Not allowed to modify color variables!");
        }
        this.red = red;
        this.redInt = floatToIntColor(red);
    }
    public void setRed(int red) {
        if(!modifiable) {
            throw new RuntimeException("Not allowed to modify color variables!");
        }
        this.redInt = red;
        this.red = ((float)red/255);
    }

    public void setGreen(float green) {
        if(!modifiable) {
            throw new RuntimeException("Not allowed to modify color variables!");
        }
        this.green = green;
        this.greenInt = floatToIntColor(green);
    }
    public void setGreen(int green) {
        if(!modifiable) {
            throw new RuntimeException("Not allowed to modify color variables!");
        }
        this.greenInt = green;
        this.green = ((float)green/255);
    }

    public void setBlue(float blue) {
        if(!modifiable) {
            throw new RuntimeException("Not allowed to modify color variables!");
        }
        this.blue = blue;
        this.blueInt = floatToIntColor(blue);
    }
    public void setBlue(int blue) {
        if(!modifiable) {
            throw new RuntimeException("Not allowed to modify color variables!");
        }
        this.blueInt = blue;
        this.blue = ((float)blue/255);
    }

    public void setAlpha(float alpha) {
        if(!modifiable) {
            throw new RuntimeException("Not allowed to modify color variables!");
        }
        this.alpha = alpha;
        this.alphaInt = floatToIntColor(alpha);
    }
    public void setAlpha(int alpha) {
        if(!modifiable) {
            throw new RuntimeException("Not allowed to modify color variables!");
        }
        this.alphaInt = alpha;
        this.alpha = ((float)alpha/255);
    }

    public int toInt() {
        return ((int) (red * 255) << 16)
                | ((int) (green * 255) << 8)
                | (int) (blue * 255)
                | ((int) (alpha * 255) << 24);
    }

    public String toHex(boolean withAlpha){
        if(withAlpha){
            return String.format(
                    "#%02x%02x%02x%02x",
                    (int) (red * 255),
                    (int) (green * 255),
                    (int) (blue * 255),
                    (int) (alpha * 255)
            );
        }
        return String.format(
                "#%02x%02x%02x",
                (int) (red * 255),
                (int) (green * 255),
                (int) (blue * 255)
        );
    }



    //from string hex rgba
    public static AtumColor fromHex(@NotNull String hex, boolean modifiable) {
        try {
            hex = hex.substring(1);

            if (hex.length() == 6) {

                return new AtumColor(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        1,
                        modifiable
                );
            }
            if (hex.length() == 8) {
                return new AtumColor(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        Integer.valueOf(hex.substring(6, 8), 16),
                        modifiable
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AtumColor.WHITE;
    }
    public static AtumColor fromHex(@NotNull String hex) {
        return fromHex(hex, true);
    }

    //from int rgba where a >0 <=1
    public static AtumColor from(int color, boolean hasAlpha, boolean modifiable) {
        if(hasAlpha){
            return new AtumColor(
                    (color >> 16 & 0xFF) / 255f,
                    (color >> 8 & 0xFF) / 255f,
                    (color & 0xFF) / 255f,
                    1,
                    modifiable
            );
        }
        return new AtumColor(
                (color >> 16 & 0xFF) / 255f,
                (color >> 8 & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                (color >> 24 & 0xFF) / 255f,
                modifiable
        );
    }
    public static AtumColor from(int color, boolean hasAlpha){
        return from(color,hasAlpha, true);
    }


    private int floatToIntColor(float value){
        if(value<=0) return 0;
        if(value>=1) return 255;
        return  (int)(value*255);
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
    public AtumColor clone() throws CloneNotSupportedException{
        return (AtumColor) super.clone();
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

}
