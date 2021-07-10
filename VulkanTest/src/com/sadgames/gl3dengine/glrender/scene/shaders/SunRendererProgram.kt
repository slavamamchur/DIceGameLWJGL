package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.LensObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.objects.SunObject
import com.sadgames.sysutils.common.Mat4x4
import com.sadgames.sysutils.common.MathUtils.applyViewMatrix
import com.sadgames.sysutils.common.MathUtils.getMatrix4f
import com.sadgames.sysutils.common.toInt

class SunRendererProgram : GUIRendererProgram() {

    override fun getVertexShaderResId() = SUN_VERTEX_SHADER
    override fun getFragmentShaderResId() = SUN_FRAGMENT_SHADER
    override fun getGeometryShaderResId(): String? = null

    override fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        if (renderable is LensObject)
            setMVPMatrixData(renderable.modelMatrix)
        else
            setMVPMatrixData(buildMVP(renderable, viewMatrix, projectionMatrix)) //todo: moveto gui2d
    }

    fun buildMVP(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray): FloatArray {
        val scale = renderable.scaleFactor
        renderable.scaleFactor = scale

        val modelViewMat = applyViewMatrix(getMatrix4f(renderable.modelMatrix), getMatrix4f(viewMatrix))
        ///modelViewMat *= scale

        return (Mat4x4(projectionMatrix) * modelViewMat).value
    }

    override fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>) {
        super.bindGlobalParams(scene)

        params[LIGHT_COLOUR_PARAM_NAME]?.value = scene.lightSource!!.lightColour
    }

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {
        params[ALPHA_SCALE_PARAM_NAME]?.value = (renderable as SunObject).getAlphaScale()
        params[IS_LIGHT_SOURCE_PARAM_NAME]?.value = renderable.isLightSource.toInt()
    }

}
