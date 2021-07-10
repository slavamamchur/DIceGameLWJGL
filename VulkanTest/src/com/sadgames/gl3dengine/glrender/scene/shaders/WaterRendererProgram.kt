package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.sysutils.common.Mat4x4

class WaterRendererProgram : TerrainRendererProgram() {

    private val WAVE_SPEED = 0.0005f
    private var time = 0f

    override fun getVertexShaderResId() = WATER_RENDERER_VERTEX_SHADER_ES32
    override fun getFragmentShaderResId() = WATER_RENDERER_FRAGMENT_SHADER
    override fun getGeometryShaderResId() = WATER_RENDERER_GEOMETRY_SHADER
    override fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {}

    override fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>) {
        super.bindGlobalParams(scene)

        val modelView = Mat4x4(scene.camera!!.projectionMatrix) * Mat4x4(scene.camera!!.viewMatrix)
        setMVMatrixData(scene.camera!!.viewMatrix)
        setMVPMatrixData(modelView.value)

        params[LIGHT_POSITION_PARAM_NAME]?.value = scene.lightSource!!.lightPosInModelSpace
        params[LIGHT_POSITIONF_PARAM_NAME]?.value = scene.lightSource!!.lightPosInModelSpace

        params[LIGHT_MVP_MATRIX_PARAM_NAME]?.value = (Mat4x4(BIAS) * Mat4x4(scene.lightSource!!.projectionMatrix) * Mat4x4(scene.lightSource!!.viewMatrix)).value

        time = (time + scene.frameTime * WAVE_SPEED) % 1
        params[TIME_PARAM_NAME]?.value = time
    }

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {}

}