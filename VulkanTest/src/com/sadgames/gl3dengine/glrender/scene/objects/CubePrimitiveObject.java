package com.sadgames.gl3dengine.glrender.scene.objects;

import com.bulletphysics.collision.shapes.BoxShape;
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram;

import org.jetbrains.annotations.NotNull;

import javax.vecmath.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glDrawArrays;

public class CubePrimitiveObject extends GameItemObject {

    protected static final float[] normal = new float[] {
            // Front face
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            // Right face
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            // Back face
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,

            // Left face
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,

            // Top face
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            // Bottom face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f
    };

    protected float halfSize;

    public CubePrimitiveObject(String textureResName, VBOShaderProgram program, float mass, int tag, float halfSize) {
        super(textureResName, program, mass, tag);
        this.halfSize = halfSize;
    }

    public CubePrimitiveObject(VBOShaderProgram program, int color, float mass, int tag, float halfSize) {
        super(program, color, mass, tag);
        this.halfSize = halfSize;
    }


    @Override public int getFacesCount() { return 24; }

    @Override
    protected float[] getVertexesArray() {
        return new float[] {
                // Front face
                -halfSize, halfSize, halfSize, 0.25f, 0.333f,
                -halfSize, -halfSize, halfSize, 0.25f, 0.666f,
                halfSize, halfSize, halfSize, 0.5f, 0.333f,
                halfSize, -halfSize, halfSize, 0.5f, 0.666f,

                // Right face
                halfSize, halfSize, halfSize,  0.5f, 0.333f,
                halfSize, -halfSize, halfSize, 0.5f, 0.666f,
                halfSize, halfSize, -halfSize,  0.75f, 0.333f,
                halfSize, -halfSize, -halfSize, 0.75f, 0.666f,

                // Back face
                halfSize, halfSize, -halfSize, 0.75f, 0.333f,
                halfSize, -halfSize, -halfSize, 0.75f, 0.666f,
                -halfSize, halfSize, -halfSize, 1f, 0.333f,
                -halfSize, -halfSize, -halfSize, 1f, 0.666f,

                // Left face
                -halfSize, halfSize, -halfSize, 0f, 0.333f,
                -halfSize, -halfSize, -halfSize, 0f, 0.666f,
                -halfSize, halfSize, halfSize, 0.25f, 0.333f,
                -halfSize, -halfSize, halfSize, 0.25f, 0.666f,

                // Top face
                -halfSize, halfSize, -halfSize, 0.25f, 0f,
                -halfSize, halfSize, halfSize, 0.25f, 0.333f,
                halfSize, halfSize, -halfSize,0.5f, 0f,
                halfSize, halfSize, halfSize, 0.5f, 0.333f,

                // Bottom face
                halfSize, -halfSize, -halfSize, 0.25f, 0.666f,
                halfSize, -halfSize, halfSize, 0.25f, 1f,
                -halfSize, -halfSize, -halfSize, 0.5f, 0.666f,
                -halfSize, -halfSize, halfSize, 0.5f, 1f
        };
    }

    @Override protected float[] getNormalsArray() {
        return normal;
    }
    @Override protected short[] getFacesArray() {
        return null;
    }
    @Override protected void createCollisionShape(@NotNull float[] vertexes) { _shape = new BoxShape(new Vector3f(halfSize, halfSize, halfSize)); }
    @Override public void render() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, getFacesCount());
    }
}
