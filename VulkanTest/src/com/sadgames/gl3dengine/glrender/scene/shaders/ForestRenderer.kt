package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.sysutils.common.Mat4x4

class ForestRenderer : TerrainRendererProgram() {

    override fun getVertexShaderResId() = FOREST_VERTEX_SHADER

    override fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        setMVMatrixData(viewMatrix)
        setMVPMatrixData((Mat4x4(projectionMatrix) * Mat4x4(viewMatrix)).value)
    }

    override fun bindLightSourceMVP(renderable: AbstractGL3DObject, ls: GLLightSource?, hasDepthTextureExtension: Boolean) {
        params[LIGHT_MVP_MATRIX_PARAM_NAME]?.value = (Mat4x4(BIAS) * (Mat4x4(ls!!.projectionMatrix) * Mat4x4(ls.viewMatrix))).value
    }

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {
        super.bindAdditionalParams(scene, renderable)

        renderable.modelMatrix[13] -= 0.03530573f
        params[MODEL_MATRIX_PARAM_NAME]?.value = renderable.modelMatrix
    }
}