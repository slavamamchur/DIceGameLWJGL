package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GEN_TERRAIN_FRAGMENT_SHADER

class GenTerrainProgram: TerrainRendererProgram() {

    override fun getFragmentShaderResId() = GEN_TERRAIN_FRAGMENT_SHADER
}