package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*

open class Gl32TerrainRenderer : TerrainRendererProgram() {

    override fun getVertexShaderResId() = TERRAIN_RENDERER_VERTEX_SHADER
    override fun getFragmentShaderResId() = TERRAIN_RENDERER_FRAGMENT_SHADER
    override fun getGeometryShaderResId() = TERRAIN_RENDERER_GEOMETRY_SHADER

}