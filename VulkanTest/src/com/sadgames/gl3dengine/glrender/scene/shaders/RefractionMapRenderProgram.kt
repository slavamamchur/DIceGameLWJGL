package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.objects.generated.ForestGenerator
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import com.sadgames.sysutils.common.Mat4x4
import com.sadgames.sysutils.common.toInt

class RefractionMapRenderProgram : VBOShaderProgram() {

    override fun getVertexShaderResId() = REFRACTION_MAP_VERTEX_SHADER
    override fun getFragmentShaderResId() = REFRACTION_MAP_FRAGMENT_SHADER

    override fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>) {
        val lightSource = scene.lightSource

        params[LIGHT_POSITION_PARAM_NAME]?.value = lightSource!!.lightPosInEyeSpace
        params[LIGHT_COLOUR_PARAM_NAME]?.value = lightSource!!.lightColour
        params[IS_2D_MODEF_PARAM_NAME]?.value = settingsManager.isIn_2D_Mode.toInt()
    }

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {
        val isObjectGroup = renderable is ForestGenerator
        params[IS_OBJECT_GROUP_PARAM_NAME]?.value = isObjectGroup.toInt()
        params[IS_OBJECT_GROUPF_PARAM_NAME]?.value = isObjectGroup.toInt()

        /*if (isObjectGroup) {
            renderable.modelMatrix[13] -= 0.03530573f
            params[MODEL_MATRIX_PARAM_NAME]?.value = renderable.modelMatrix
            renderable.modelMatrix[13] += 0.03530573f
        }
        else*/
            params[MODEL_MATRIX_PARAM_NAME]?.value = renderable.modelMatrix

        params[IS_LIGHT_SOURCE_PARAM_NAME]?.value = renderable.isLightSource.toInt()
    }

    override fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        val modelMatrix = if (renderable is ForestGenerator) Mat4x4() else Mat4x4(renderable.modelMatrix)
        val modelViewMatrix = Mat4x4(viewMatrix) * modelMatrix

        setMVMatrixData(modelViewMatrix.value)
        setMVPMatrixData((Mat4x4(projectionMatrix) * modelViewMatrix).value)
    }
}