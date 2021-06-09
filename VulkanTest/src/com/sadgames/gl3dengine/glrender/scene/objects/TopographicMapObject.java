package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.sysutils.common.CommonUtils;
import com.sadgames.vulkan.newclass.Gdx2DPixmap;
import com.sadgames.vulkan.newclass.Pixmap;
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram;
import com.sadgames.sysutils.common.ColorUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE;

public abstract class TopographicMapObject extends AbstractTerrainObject {

    public enum ColorType {
        CYAN,
        BLUE,
        GREEN,
        YELLOW,
        BROWN,
        WHITE,
        UNKNOWN
    }

    protected static final float MIN_CYAN = getColorBrightness(130, 160, 232); //todo: correct values from topo map
    protected static final float MIN_BLUE = getColorBrightness(13, 30, 70);
    protected static final float MIN_GREEN = getColorBrightness(0, 0, 30); //(0, 56, 17)
    protected static final float MIN_YELLOW = getColorBrightness(145, 133, 30);
    protected static final float MIN_BROWN = getColorBrightness(83, 17, 0);
    protected static final float MIN_WHITE = getColorBrightness(127, 127, 127);

    protected static final float MAX_CYAN = getColorBrightness(220, 245, 245);
    protected static final float MAX_BLUE = getColorBrightness(129, 159, 231);
    protected static final float MAX_GREEN = getColorBrightness(85, 255, 164);
    protected static final float MAX_YELLOW = getColorBrightness(246, 246, 162);
    protected static final float MAX_BROWN = getColorBrightness(192, 135, 58);
    protected static final float MAX_WHITE = getColorBrightness(255, 255, 255);

    protected static final float[] MIN_HEIGHT_VALUES = {0.0005f, 0.55f, 0.0f, 0.205f, 1.205f, 2.505f};
    public static final float[] MAX_HEIGHT_VALUES = {0.5f, 10f, 0.2f, 1.2f, 2.5f, 10f};
    protected static final float[] DELTA_COLOR_VALUES = {MAX_CYAN - MIN_CYAN,
            MAX_BLUE - MIN_BLUE,
            MAX_GREEN - MIN_GREEN,
            MAX_YELLOW - MIN_YELLOW,
            MAX_BROWN - MIN_BROWN,
            MAX_WHITE - MIN_WHITE};

    protected static final float[] MIN_COLOR_VALUES = {MIN_CYAN,
            MIN_BLUE,
            MIN_GREEN,
            MIN_YELLOW,
            MIN_BROWN,
            MIN_WHITE};

    protected static final boolean[] INVERT_LIGHT_FACTOR = {true, true, false, true, true, false};

    protected Pixmap heightMap = null;

    public TopographicMapObject(VBOShaderProgram program, String mapName) {
        super(mapName, program);
    }

    protected abstract ByteBuffer loadReliefMap();

    protected Pixmap getReliefMap() {
        try {
            ByteBuffer buffer = loadReliefMap();

            return buffer != null ?
                new Pixmap(
                        new Gdx2DPixmap(
                                buffer,
                                TEXTURE_RESOLUTION_SCALE[CommonUtils.getSettingsManager().getGraphicsQualityLevel().ordinal()]
                        )
                )
            :
                null;
        } catch (IOException e){
            //e.printStackTrace()
            return null;
        }
    }

    @Override
    protected float getHeightValue(int i, int j) {
        if (heightMap == null)
            dimension = getDimension();

        if (dimension == FLAT_MAP_DEFAULT_DIMENSION)
            return FLAT_MAP_DEFAULT_HEIGHT;

        return getInterpolatedValue(i, j);
    }

    private float interpolate(float a, float b, float blend) {
        double theta = blend * Math.PI;
        float f = (float)(1f - Math.cos(theta)) * 0.5f;

        return a * (1f - f) + b * f;
    }

    private float getSmoothHeightValue(int x, int z) {
        float corners = (getHeightValueInternal(x - 1, z - 1) + getHeightValueInternal(x + 1, z - 1) + getHeightValueInternal(x - 1, z + 1)
                + getHeightValueInternal(x + 1, z + 1)) / 16f;
        float sides = (getHeightValueInternal(x - 1, z) + getHeightValueInternal(x + 1, z) + getHeightValueInternal(x, z - 1)
                + getHeightValueInternal(x, z + 1)) / 8f;
        float center = getHeightValueInternal(x, z) / 4f;
        return corners + sides + center;
    }

    private float getInterpolatedValue(float x, float z){
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;

        float v1 = getSmoothHeightValue(intX, intZ);
        float v2 = getSmoothHeightValue(intX + 1, intZ);
        float v3 = getSmoothHeightValue(intX, intZ + 1);
        float v4 = getSmoothHeightValue(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);

        return interpolate(i1, i2, fracZ);
    }

    private static float getColorBrightness(int R, int G, int B) {
        return R * 0.2126f +  G * 0.7152f + B * 0.0722f;
    }

    protected float getHeightValueInternal(int i, int j) {
        float tu = i * 1.0f / dimension;
        float tv = j * 1.0f / dimension;
        int xCoord = Math.round((heightMap.getWidth() - 1) * tu);
        int zCoord = Math.round((heightMap.getHeight() - 1) * tv);
        xCoord = Math.min(xCoord, dimension);
        zCoord = Math.min(zCoord, dimension);
        xCoord = Math.max(xCoord, 0);
        zCoord = Math.max(zCoord, 0);
        int vColor = heightMap.getPixel(xCoord, zCoord);

        return getYValueInternal(heightMap, xCoord, zCoord, vColor);
    }

    protected float getYValueInternal(Pixmap map, int xCoord, int yCoord, int vColor) {
        ColorType cType = CheckColorType(vColor);

        if (cType.equals(ColorType.UNKNOWN)) {
            vColor = interpolateUnknownColorValue(map, xCoord, yCoord);
            cType = CheckColorType(vColor);
        }

        float deltaY = 0, minY = 0;
        try {
            deltaY = getLandScale() * (MAX_HEIGHT_VALUES[cType.ordinal()] - MIN_HEIGHT_VALUES[cType.ordinal()]);
            minY = getLandScale() * MIN_HEIGHT_VALUES[cType.ordinal()];
        }
        catch (Exception e) {
            return 0;
        }

        float colorLight = getColorBrightness(ColorUtils.red(vColor),
                ColorUtils.green(vColor),
                ColorUtils.blue(vColor)) - MIN_COLOR_VALUES[cType.ordinal()];
        float deltaLight = DELTA_COLOR_VALUES[cType.ordinal()];

        float kXZ = colorLight / deltaLight;
        kXZ = INVERT_LIGHT_FACTOR[cType.ordinal()] ? 1.0f - kXZ : kXZ;

        float y = minY + deltaY * kXZ;
        y = cType.equals(ColorType.BLUE) || cType.equals(ColorType.CYAN) ? -y : y;

        return y * 3f;
    }

    protected static ColorType CheckColorType(Integer color) {
        int R = ColorUtils.red(color);
        int G = ColorUtils.green(color);
        int B = ColorUtils.blue(color);
        //int A = ColorUtils.blue(color);

        if ((B - G > 30) && (R < G))
            return G <= 0.5 * B ? ColorType.BLUE : ColorType.CYAN; //B <= 231 ? BLUE : CYAN;//
        else if ((R < G)/* && (B < G)*/)
            return ColorType.GREEN;
        else if (/*(G <= R) && */(B < G))
            return G <= 0.7 * R ? ColorType.BROWN : ColorType.YELLOW;
        else if ((R > G) && (R > B))
            return ColorType.BROWN;
        else if ((G == R) && (B == G) && (R >= 180))
            return ColorType.WHITE;
        else
            return ColorType.UNKNOWN;
    }

    @Override
    protected int getDimension() {
        if (heightMap == null)
            heightMap = getReliefMap();

        return heightMap == null ? FLAT_MAP_DEFAULT_DIMENSION : heightMap.getWidth() - 1;
    }

    @Override
    protected void disposeTempData() {
        if ((heightMap != null)) {
            heightMap.dispose();
            heightMap = null;
        }
    }

    protected int interpolateUnknownColorValue(Pixmap map, int xCoord, int yCoord) {
        int count = 0, R = 0, G = 0, B = 0;

        for (int j = yCoord - 1; j <= yCoord + 1; j++)
            for (int i = xCoord - 1; i <= xCoord + 1; i++)
                try {
                    if ( !((i == xCoord) && (j == yCoord)) && (i <= dimension)
                         && (j <= dimension)
                        ) {
                        int color = map.getPixel(i, j);
                        R += ColorUtils.red(color);
                        G += ColorUtils.green(color);
                        B += ColorUtils.blue(color);

                        count++;
                    }
                } catch (IllegalArgumentException ignored) {}

        R /= count;
        G /= count;
        B /= count;

        return ColorUtils.argb(255, R, G, B);
    }

}
