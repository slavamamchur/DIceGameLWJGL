package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.gamelogic.client.GameConst.DUDVMAP_TEXTURE
import com.sadgames.gl3dengine.gamelogic.client.GameConst.NORMALMAP_TEXTURE
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.WATER_OBJECT
import com.sadgames.gl3dengine.glrender.GLRenderConsts.SEA_SIZE_IN_WORLD_SPACE
import com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData.ElementType
import com.sadgames.gl3dengine.manager.TextureCache
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.vecmath.Vector3f

class WaterObject(program: VBOShaderProgram) :
        ProceduralSurfaceObject(WATER_OBJECT, null, SEA_SIZE_IN_WORLD_SPACE, program) {

    init {
        isCubeMap = true
        isCastShadow = false
        isReflected = false
        isDrawInRaysBuffer = false

        glNormalMap = TextureCache[NORMALMAP_TEXTURE]
        glDUDVMap = TextureCache[DUDVMAP_TEXTURE] //todo: do not mipmap
    }

    override fun calculateLandScale(landSize: Float) = 1.0f
    override fun getHeightValue(i: Int, j: Int) = 0.0f
    override fun getDimension() = /*if (isGlES30Supported())*/ 249 / 2 /*else FLAT_MAP_DEFAULT_DIMENSION*/

    override fun getVertexVal(i: Int, j: Int): Vector3f {
        val idx = (j * (dimension + 1) + i) * 4
        return Vector3f(vertexes[idx], 0f, vertexes[idx + 1])
    }

    override fun disposeTempData() {}

    override fun render() {
        if (!settingsManager.isIn_2D_Mode)
            super.render()
    }

    override fun createVertexesVBO() {
        dimension = getDimension()
        vertexes = FloatArray((dimension + 1) * (dimension + 1) * 4)

        val tdu = 1.0f / dimension
        val dx = LAND_WIDTH / dimension
        val dz = LAND_HEIGHT / dimension
        val x0 = -LAND_WIDTH / 2f
        val z0 = -LAND_HEIGHT / 2f
        var k = 0

        for (j in 0..dimension) {
            for (i in 0..dimension) {
                vertexes[k] = x0 + i * dx /** x */
                vertexes[k + 1] = z0 + j * dz /** z */
                vertexes[k + 2] = i * tdu /** u */
                vertexes[k + 3] = j * tdu /** v */

                k += 4
            }
        }

        disposeTempData()

        val vertexData = ByteBuffer
                .allocateDirect(vertexes.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexes)

        vertexData.position(0)
        vertexVBO = VBOData(ElementType.VERTEX, TEXEL_UV_SIZE, 16, 0, vertexData)
        texelVBO = VBOData(ElementType.VERTEX, TEXEL_UV_SIZE, 16, TEXEL_UV_SIZE * 4, vertexData)
        vertexData.limit(0)
    }

    override fun createNormalsVBO() {}
}
