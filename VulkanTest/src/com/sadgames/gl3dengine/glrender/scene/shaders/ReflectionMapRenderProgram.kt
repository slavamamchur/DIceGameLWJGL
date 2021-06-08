package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.gamelogic.client.GameConst.ROAD_TEXTURE_NAME
import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.objects.generated.ForestGenerator
import com.sadgames.gl3dengine.manager.TextureCache
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import com.sadgames.sysutils.common.Mat4x4
import com.sadgames.sysutils.common.toInt

open class ReflectionMapRenderProgram : VBOShaderProgram() {

    protected var drawTextures = true

    override fun getVertexShaderResId() = REFLECTION_MAP_VERTEX_SHADER
    override fun getFragmentShaderResId() = REFLECTION_MAP_FRAGMENT_SHADER

    override fun createUniforms() {
        super.createUniforms()

        if (!drawTextures)
            params[ACTIVE_ROAD_TEXTURE_SLOT_PARAM_NAME] = null
    }

    override fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>) {
        val lightSource = scene.lightSource

        val title = TextureCache[ROAD_TEXTURE_NAME]
        if (title != null && title.textureId != 0) {
            title.bind(ROAD_TILE_TEXTURE_SLOT)
            params[ACTIVE_ROAD_TEXTURE_SLOT_PARAM_NAME]?.value = ROAD_TILE_TEXTURE_SLOT
        }

        params[LIGHT_POSITION_PARAM_NAME]?.value = lightSource!!.lightPosInEyeSpace
        params[IS_2D_MODEF_PARAM_NAME]?.value = settingsManager.isIn_2D_Mode.toInt()
    }

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {
        val isObjectGroup = renderable is ForestGenerator
        params[IS_OBJECT_GROUP_PARAM_NAME]?.value = isObjectGroup.toInt()

        /*if (isObjectGroup) {
            renderable.modelMatrix[13] -= 0.03530573f
            params[MODEL_MATRIX_PARAM_NAME]?.value = renderable.modelMatrix
            renderable.modelMatrix[13] += 0.03530573f
        }
        else {*/
            params[MODEL_MATRIX_PARAM_NAME]?.value = renderable.modelMatrix
        //}
    }

    override fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        val modelMatrix = if (renderable is ForestGenerator) Mat4x4() else Mat4x4(renderable.modelMatrix)
        val modelViewMatrix = Mat4x4(viewMatrix) * modelMatrix

        setMVMatrixData(modelViewMatrix.value)
        setMVPMatrixData((Mat4x4(projectionMatrix) * modelViewMatrix).value)
    }
}
