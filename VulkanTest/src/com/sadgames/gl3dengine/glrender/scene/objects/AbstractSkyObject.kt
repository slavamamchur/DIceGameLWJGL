package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20

abstract class AbstractSkyObject internal constructor(cubeTexture: AbstractTexture?, program: VBOShaderProgram?):
        GameItemObject((cubeTexture as? BitmapTexture)?.textureName, program, 1f, COLLISION_OBJECT) {

    var rotationAngle = 0f
    protected var skyPrimitive = createSkyPrimitive(LAND_SIZE_IN_WORLD_SPACE / 2f /*+ 0.25f*/)

    init {
        isCastShadow = false
        isDrawInRaysBuffer = false
    }

    protected abstract fun createSkyPrimitive(halfSize: Float): GameItemObject

    fun calcRotationAngle(frametime: Long) {
        rotationAngle = if (settingsManager.isIn_2D_Mode) 0f else rotationAngle + 0.5f * frametime / 250f
        rotationAngle = if (rotationAngle > 360f) 360f - rotationAngle else rotationAngle
    }

    override fun render() {
        glCullFace(GL_FRONT)
        glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, facesCount)
        glCullFace(GL_BACK)
    }

    override fun getFacesCount() = skyPrimitive.facesCount
    override fun getVertexesArray() = skyPrimitive.vertexesArray
    override fun getNormalsArray() = skyPrimitive.normalsArray
    override fun getFacesArray() = skyPrimitive.facesArray

}