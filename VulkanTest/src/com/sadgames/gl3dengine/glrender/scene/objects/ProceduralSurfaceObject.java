package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData.ElementType;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VBO_ITEM_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VBO_STRIDE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;
import static com.sadgames.sysutils.common.ArraysUtilsKt.chain;

public abstract class ProceduralSurfaceObject extends PNodeObject {

    protected static final float FLAT_MAP_DEFAULT_HEIGHT = 0.001f;
    protected static final int FLAT_MAP_DEFAULT_DIMENSION = 3;

    protected float LAND_WIDTH;
    protected float LAND_HEIGHT;
    private float landScale;
    protected int dimension;

    protected float[] vertexes;

    public ProceduralSurfaceObject(GLObjectType type, String textureResName, float landSize, VBOShaderProgram program) {
        super(type, textureResName, program, 0, COLLISION_OBJECT);

        initMesh(landSize);
    }

    @SuppressWarnings("unused")
    public ProceduralSurfaceObject(GLObjectType type, float landSize, VBOShaderProgram program, int color) {
        super(type, program, color, 0, 0);

        initMesh(landSize);
    }

    private void initMesh(float landSize) {
        LAND_WIDTH = landSize;
        LAND_HEIGHT = landSize;
        this.landScale = calculateLandScale(landSize);
    }

    public float getLandScale() {
        return landScale;
    }
    public float getLAND_WIDTH() {
        return LAND_WIDTH;
    }

    protected abstract float calculateLandScale(float landSize);
    protected abstract float getHeightValue(int i, int j);
    protected abstract int getDimension();
    protected abstract void disposeTempData();

    @Override public int getFacesCount() { return 2 * (dimension + 1) * dimension + (dimension - 1); }

    private static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    protected Vector3f getVertexVal(int i, int j) {
        int idx = (j * (dimension + 1) + i) * VBO_ITEM_SIZE;
        return new Vector3f(vertexes[idx], vertexes[idx + 1], vertexes[idx + 2]);
    }

    @Override
    public float getPlaceHeight(@NotNull Vector2f pos) {
        float terrainX = pos.x + LAND_WIDTH / 2f;
        float terrainZ = pos.y + LAND_HEIGHT / 2f;
        float gridSquareSize = LAND_WIDTH / (float) dimension;
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if (gridX >= dimension || gridX < 0 || gridZ >= dimension || gridZ < 0)
            return 0;

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        return xCoord <= (1 - zCoord) ?
                barryCentric(new Vector3f(0, getVertexVal(gridX, gridZ).y, 0),
                             new Vector3f(1, getVertexVal(gridX + 1, gridZ).y, 0),
                             new Vector3f(0, getVertexVal(gridX, gridZ + 1).y, 1),
                             new Vector2f(xCoord, zCoord))
                :

                barryCentric(new Vector3f(1, getVertexVal(gridX + 1, gridZ).y, 0),
                             new Vector3f(1, getVertexVal(gridX + 1, gridZ + 1).y, 1),
                             new Vector3f(0, getVertexVal(gridX,gridZ + 1).y, 1),
                             new Vector2f(xCoord, zCoord));
    }

    @Override
    protected void createVertexesVBO() {
        dimension = getDimension();
        vertexes = new float[(dimension + 1) * (dimension + 1) * VBO_ITEM_SIZE];

        float tdu = 1.0f / dimension;
        float tdv = tdu;
        float dx = LAND_WIDTH / dimension;
        float dz = LAND_HEIGHT / dimension;
        float x0 = -LAND_WIDTH / 2f;
        float z0 = -LAND_HEIGHT / 2f;
        int k = 0;

        for (int j = 0; j <= dimension; j++) {
            for (int i = 0; i <= dimension; i++) {
                /** vertex */
                vertexes[k] = x0 + i * dx;
                vertexes[k + 1] = getHeightValue(i ,j);
                vertexes[k + 2] = z0 + j * dz;

                /** texture coords */
                vertexes[k + 3] = i * tdu;
                vertexes[k + 4] = j * tdv;

                k += 5;
            }
        }

        disposeTempData();

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexes);

        vertexData.position(0);
        setVertexVBO(new VBOData(ElementType.VERTEX, VERTEX_SIZE, VBO_STRIDE, 0, vertexData));
        //todo: optimize
        setTexelVBO(new VBOData(ElementType.VERTEX, TEXEL_UV_SIZE, VBO_STRIDE, VERTEX_SIZE * 4, vertexData));
        vertexData.limit(0);

        x0 *= 3;
        z0 *= 3;
        if (tag == COLLISION_OBJECT) {
            float[] collision_model = { x0, 0, z0, 0, 0,
                                       -x0, 0, z0, 0, 0,
                                        x0, 0, -z0, 0, 0,
                                        -x0, 0, -z0, 0, 0};
            createCollisionShape(collision_model);
        }
    }

    @Override
    protected void createTexelsVBO() {}

    @Override
    protected void createNormalsVBO() {
        float dx = LAND_WIDTH / dimension;
        float dz = LAND_HEIGHT / dimension;
        float [] normal = new float[(dimension + 1) * (dimension + 1) * VERTEX_SIZE];
        int k = 0;

        for (int j = 0; j <= dimension; j++)
            for (int i = 0; i <= dimension; i++) {
                if ((i == dimension) && (j == dimension)) {
                    /** Nx = (y[jmax][imax - 1] - y[jmax][imax]) * dz*/
                    normal[k] = vertexes[chain(i - 1, j, dimension, VBO_ITEM_SIZE) + 1] - vertexes[chain(i, j, dimension, VBO_ITEM_SIZE) + 1] * dz;
                    /** Nz = dx * (y[jmax - 1][imax] - y[jmax][imax])*/
                    normal[k + 2] = dx * (vertexes[chain(i, j - 1, dimension, VBO_ITEM_SIZE) + 1] - vertexes[chain(i, j, dimension, VBO_ITEM_SIZE) + 1]);
                } else if (i == dimension) {
                    /** Nx = (y[j][imax - 1] - y[j][imax]) * dz*/
                    normal[k] = vertexes[chain(i - 1, j, dimension, VBO_ITEM_SIZE) + 1] - vertexes[chain(i, j, dimension, VBO_ITEM_SIZE) + 1] * dz;
                    /** Nz = -dx * (y[j + 1][imax] - y[j][imax])*/
                    normal[k + 2] = -dx * (vertexes[chain(i, j + 1, dimension, VBO_ITEM_SIZE) + 1] - vertexes[chain(i, j, dimension, VBO_ITEM_SIZE) + 1]);
                } else if (j == dimension) {
                    /** Nx = -(y[jmax][i + 1] - y[jmax][i]) * dz*/
                    normal[k] = -(vertexes[chain(i + 1, j, dimension, VBO_ITEM_SIZE) + 1] - vertexes[chain(i, j, dimension, VBO_ITEM_SIZE) + 1]) * dz;
                    /** Nz = dx * (y[jmax - 1][i] - y[jmax][i])*/
                    normal[k + 2] = dx * (vertexes[chain(i, j - 1, dimension, VBO_ITEM_SIZE) + 1] - vertexes[chain(i, j, dimension, VBO_ITEM_SIZE) + 1]);
                } else {
                    /** Nx = -(y[j][i + 1] - y[j][i]) * dz*/
                    normal[k] = -(vertexes[chain(i + 1, j, dimension, VBO_ITEM_SIZE) + 1] - vertexes[chain(i, j, dimension, VBO_ITEM_SIZE) + 1]) * dz;
                    /** Nz = -dx * (y[j + 1][i] - y[j][i])*/
                    normal[k + 2] = -dx * (vertexes[chain(i, j + 1, dimension, VBO_ITEM_SIZE) + 1] - vertexes[chain(i, j, dimension, VBO_ITEM_SIZE) + 1]);
                }

                normal[k + 1] = dx * dz; /**set Ny value*/

                k += 3;
            }

        FloatBuffer normalData = ByteBuffer
                .allocateDirect(normal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(normal);

        normalData.position(0);
        setNormalVBO(new VBOData(ElementType.VERTEX, VERTEX_SIZE, 0, 0, normalData));
        normalData.limit(0);

        //vertexes = null;
    }

    @Override
    protected void createFacesIBO() {
        short[] index = new short[2 * (dimension + 1) * dimension + (dimension - 1)];
        int k=0;
        int j=0;

        while (j < dimension) {
            /** лента слева направо*/
            for (int i = 0; i <= dimension; i++) {
                index[k] = chain(j, i, dimension);
                k++;
                index[k] = chain(j+1, i, dimension);
                k++;
            }
            if (j < dimension - 1){
                /** вставим хвостовой индекс для связки*/
                index[k] = chain(j + 1, dimension, dimension);
                k++;
            }
            /** переводим ряд*/
            j++;

            /** проверяем достижение конца*/
            if (j < dimension){
                /** лента справа налево*/
                for (int i = dimension; i >= 0; i--) {
                    index[k] = chain(j, i, dimension);
                    k++;
                    index[k] = chain(j + 1, i, dimension);
                    k++;
                }
                if (j < dimension - 1){
                    /** вставим хвостовой индекс для связки*/
                    index[k] = chain(j + 1, 0, dimension);
                    k++;
                }
                /** переводим ряд*/
                j++;
            }
        }

        ShortBuffer indexData = ByteBuffer
                .allocateDirect(index.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(index);

        indexData.position(0);
        setFacesIBO(new VBOData(indexData));

        indexData.limit(0);
        indexData = null;
        setIndexData(indexData);
    }

}
