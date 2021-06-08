package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData.ElementType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.vecmath.Vector4f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;

public class GUI2DImageObject extends AbstractGL3DObject {

    private float left;
    private float top;
    private float right;
    private float bottom;
    private boolean isReftectedY;
    private int effects;

    public AbstractTexture diffuse = null;

    public GUI2DImageObject(VBOShaderProgram program, Vector4f box, boolean isReftectedY) {
        super(GUI_OBJECT, program);

        this.left = box.x;
        this.top = box.y;
        this.right = box.z;
        this.bottom = box.w;
        this.isReftectedY = isReftectedY;
        effects = 0;
        setDrawInRaysBuffer(false);

        setCastShadow(false);
        setReflected(false);
    }

    public int getEffects() {
        return effects;
    }
    public void setEffects(int effects) {
        this.effects = effects;
    }

    @Override
    public int getFacesCount() {
        return 4;
    }

    @Override
    protected void createVertexesVBO() {

        float[] vertexes = new float[] {
                left, top, 0.0f,
                left, bottom, 0.0f,
                right, top, 0.0f,
                right, bottom, 0.0f,
        };

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexes);

        vertexData.position(0);
        setVertexVBO(new VBOData(ElementType.VERTEX, VERTEX_SIZE, 0, 0, vertexData));
        vertexData.limit(0);
    }

    @Override public void render() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, getFacesCount());
    }

    @Override
    protected void createTexelsVBO() {
        float[] texcoords = new float[] {
                0.0f, 1.0f - (isReftectedY ? 1 : 0),
                0.0f, 0.0f + (isReftectedY ? 1 : 0),
                1.0f, 1.0f - (isReftectedY ? 1 : 0),
                1.0f, 0.0f + (isReftectedY ? 1 : 0)
        };

        FloatBuffer texelData = ByteBuffer
                .allocateDirect(texcoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(texcoords);

        texelData.position(0);
        setTexelVBO(new VBOData(ElementType.VERTEX, TEXEL_UV_SIZE, 0, 0, texelData));
        texelData.limit(0);
    }

    @Override protected void createNormalsVBO() {}
    @Override protected void createFacesIBO() {}

    @Override
    public void bindMaterial(VBOShaderProgram program) {
        super.bindMaterial(program);

        if (diffuse != null) {
            diffuse.bind(0);

            GLShaderParam param = program.paramByName(ACTIVE_TEXTURE_SLOT_PARAM_NAME);
            if (param != null && param.getParamReference() >= 0)
                param.setValue(0);
        }
    }
}
