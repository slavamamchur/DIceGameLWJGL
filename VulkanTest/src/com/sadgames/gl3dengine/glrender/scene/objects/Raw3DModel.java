package com.sadgames.gl3dengine.glrender.scene.objects;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class Raw3DModel {

    private float[] vertexes;
    private float[] textureCoords;
    private float[] normals;
    private short[] faces;

    public Raw3DModel(float[] vertexes, float[] textureCoords, float[] normals, short[] faces) {
        this.vertexes = vertexes;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.faces = faces;
    }

    public float[] getVertexes() {
        return vertexes;
    }
    public void setVertexes(float[] vertexes) {
        this.vertexes = vertexes;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }
    public void setTextureCoords(float[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public LuaTable getNormalsLua() {
        LuaTable result = new LuaTable();

        for (int i = 0; i < normals.length; i++)
           result.insert(i+1, CoerceJavaToLua.coerce(normals[i]));

        return result;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public short[] getFaces() {
        return faces;
    }
    public void setFaces(short[] faces) {
        this.faces = faces;
    }
}
