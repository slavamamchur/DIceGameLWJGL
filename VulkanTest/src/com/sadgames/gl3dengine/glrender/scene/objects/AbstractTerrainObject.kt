package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.TERRAIN_OBJECT
import com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_KM
import com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import javax.vecmath.Vector2f

abstract class AbstractTerrainObject(textureResName: String, program: VBOShaderProgram) :
        ProceduralSurfaceObject(TERRAIN_OBJECT, textureResName, LAND_SIZE_IN_WORLD_SPACE, program) {

    var scaleX: Float = 0f; protected set
    var scaleZ: Float = 0f; protected set

    init {
        isCastShadow = false
        isCubeMap = true
    }

    override fun calculateLandScale(landSize: Float) = landSize / LAND_SIZE_IN_KM

    override fun loadTexture(): AbstractTexture? {
        val glTexture = super.loadTexture()

        scaleX = LAND_WIDTH / glTexture.width * 1f
        scaleZ = LAND_HEIGHT / glTexture.height * 1f

        return glTexture
    }

    fun map2WorldCoord(x: Float, y: Float) = Vector2f(x * scaleX - LAND_WIDTH / 2, y * scaleZ - LAND_HEIGHT / 2)
}
