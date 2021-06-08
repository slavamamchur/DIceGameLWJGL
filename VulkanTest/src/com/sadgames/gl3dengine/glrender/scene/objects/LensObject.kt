package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.sysutils.common.*
import com.sadgames.sysutils.common.MathUtils.scaleM
import org.lwjgl.opengl.GL11.*
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class LensObject(glScene: GLRendererInterface<SceneObjectsTreeItem>, scale: Float, val spacing: Float, val number: Int, texNum: Int):
        SunObject(glScene, scale) {

    init {
        textureResName = "lens_flare/tex$texNum.png"
        itemName = "len$number"
        isDrawInRaysBuffer = false
        isLightSource = false
        isReflected = false
    }

    override fun getAlphaScale() = brightness

    override fun get3dPos() =
        try {
            val sunPos = glScene.lightSource!!.toScreenSpace(getSunPos(), scaleFactor) ?: CENTER_SCREEN
            val sunToCenter = (-Vector2f(sunPos) + CENTER_SCREEN) * (number * spacing)

            Vector3f((sunPos + sunToCenter).toArray())
        }
        catch (e : Exception) {
            Vector3f(1000f, 1000f, 1000f)
        }

    override fun updateTransform() {
        val mxModel = Mat4x4(modelMatrix)
        mxModel()

        val pos2D = worldPosition
        mxModel += Vector3f(pos2D.x * 2f - 1f, 1f - pos2D.y * 2f, 0f)

        val aspect: Float = GdxExt.height * 1f / GdxExt.width
        scaleM(mxModel.value, 0, scaleFactor * aspect * 1.5f, scaleFactor * 1.5f, 1f)
    }

    override fun render() {
        updateTransform()

        glDisable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE)

        super.render()

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
    }
}