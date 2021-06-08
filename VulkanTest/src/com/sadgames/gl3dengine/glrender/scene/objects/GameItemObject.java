package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData.ElementType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.GAME_ITEM_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VBO_STRIDE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;

public abstract class GameItemObject extends PNodeObject {

    public GameItemObject(String textureResName, VBOShaderProgram program, float mass, int tag) {
        super(GAME_ITEM_OBJECT, textureResName, program, mass, tag);
    }

    public GameItemObject(VBOShaderProgram program, int color, float mass, int tag) {
        super(GAME_ITEM_OBJECT, program, color, mass, tag);
    }

    protected abstract float[] getVertexesArray();
    protected abstract float[] getNormalsArray();
    protected abstract short[] getFacesArray();

    @Override
    protected void createVertexesVBO() {
        float[] vertexes = getVertexesArray();

        FloatBuffer vertexData = ByteBuffer.allocateDirect(vertexes.length * 4)
                                           .order(ByteOrder.nativeOrder())
                                           .asFloatBuffer()
                                           .put(vertexes);

        vertexData.position(0);
        setVertexVBO(new VBOData(ElementType.VERTEX, VERTEX_SIZE, VBO_STRIDE, 0, vertexData));
        setTexelVBO(new VBOData(ElementType.VERTEX, TEXEL_UV_SIZE, VBO_STRIDE, VERTEX_SIZE * 4, vertexData));
        vertexData.limit(0);

        createCollisionShape(vertexes);
    }

    @Override
    protected void createTexelsVBO() {}

    @Override
    protected void createNormalsVBO() {
        float[] normal = getNormalsArray();

        FloatBuffer normalData = ByteBuffer.allocateDirect(normal.length * 4)
                                           .order(ByteOrder.nativeOrder())
                                           .asFloatBuffer()
                                           .put(normal);

        normalData.position(0);
        setNormalVBO(new VBOData(ElementType.VERTEX, VERTEX_SIZE, 0, 0, normalData));
        normalData.limit(0);
    }

    @Override
    protected void createFacesIBO() {
        short[] index = getFacesArray();

        if (index != null) {
            ShortBuffer indexData = ByteBuffer.allocateDirect(index.length * 2)
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

}
