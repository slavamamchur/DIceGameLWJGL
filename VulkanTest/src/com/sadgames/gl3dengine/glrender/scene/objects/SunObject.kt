package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.gamelogic.client.GameConst.SUN_OBJECT
import com.sadgames.gl3dengine.gamelogic.client.GameConst.SUN_TEXTURE_NAME
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.glapi.GLOcclusionQuery
import com.sadgames.sysutils.common.*
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

open class SunObject(protected val glScene: GLRendererInterface<SceneObjectsTreeItem>, scale: Float): AbstractLightSourceObject (glScene) {

    companion object {
        private const val SUN_DIS = 4.5f
        private const val TOTAL_SAMPLES = 2000f

        val CENTER_SCREEN = Vector2f(0.5f, 0.5f)
        var visibilityRate = 1f
    }

    private val query = GLOcclusionQuery()
    val worldPosition get() = get3dPos()

    val brightness: Float
        get() {
            println(visibilityRate)
            val sunCoords = get2DPos()

            return if (sunCoords != null)
                        (1.0f - (-sunCoords + Vector2f(CENTER_SCREEN)).length()) * 0.7f * visibilityRate
                   else
                        0f
        }

    init {
        textureResName = SUN_TEXTURE_NAME
        itemName = SUN_OBJECT
        scaleFactor = scale
        isReflected = false
        isDrawInRaysBuffer = true
    }

    fun getSunPos() = Vector3f(lightSource!!.lightPosInModelSpace).normalized() * SUN_DIS + glScene.camera!!.cameraPosition
    protected open fun get3dPos() = getSunPos()
    override fun get2DPos() = glScene.lightSource!!.toScreenSpace(getSunPos(), scaleFactor)
    open fun getAlphaScale() = 1.0f

    override fun updateTransform() {
        val mxModel = Mat4x4(modelMatrix)
        mxModel()
        mxModel += worldPosition
        mxModel *= scaleFactor
    }

    override fun render() {
        if (!settingsManager.isIn_2D_Mode) {
            if (query.isResultReady) {
                val visibleSamples = query.result
                visibilityRate = (visibleSamples / TOTAL_SAMPLES).coerceAtMost(1f)
                ///println(visibleSamples)
            }

            if (!query.isInUse && glScene.program == glScene.getCachedShader(GLObjectType.REFRACTION_MAP_OBJECT)) {
                //glDisable(GL20.GL_DEPTH_TEST)
                //glDisable(GL20.GL_CULL_FACE)

                //glColorMask(false, false, false, false)
                //glDepthMask(false)

                query.start()
                super.render()
                query.end()

                //glColorMask(true, true, true, true)
                //glDepthMask(true)
                //glEnable(GL20.GL_DEPTH_TEST)
                //glEnable(GL20.GL_CULL_FACE)
            }
            else
                super.render()
        }
    }

    override fun clearData(): Void? {
        query.delete()
        super.clearData()

        return null
    }

}
