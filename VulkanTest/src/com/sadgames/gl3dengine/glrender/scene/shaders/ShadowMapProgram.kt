package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.objects.generated.ForestGenerator
import com.sadgames.sysutils.common.Mat4x4
import com.sadgames.sysutils.common.toInt

open class ShadowMapProgram : VBOShaderProgram() {

    override fun getVertexShaderResId() = SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT
    override fun getFragmentShaderResId() = SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT
    //override fun getGeometryShaderResId(): String? = SHADOWMAP_GEOMETRY_SHADER_DEPTH_SUPPORT

    override fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>) {}

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {
        val isObjectGroup = renderable is ForestGenerator
        params[IS_OBJECT_GROUP_PARAM_NAME]?.value = isObjectGroup.toInt()

        if (isObjectGroup) {
            renderable.modelMatrix[13] -= 0.03530573f
            params[MODEL_MATRIX_PARAM_NAME]?.value = renderable.modelMatrix
            renderable.modelMatrix[13] += 0.03530573f
        }
    }

    override fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        val modelMatrix = if (renderable is ForestGenerator) Mat4x4() else Mat4x4(renderable.modelMatrix)
        setMVPMatrixData((Mat4x4(projectionMatrix) * (Mat4x4(viewMatrix) * modelMatrix)).value)
    }
}
