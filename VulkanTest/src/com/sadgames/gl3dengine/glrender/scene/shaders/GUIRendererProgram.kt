package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.GUI2DImageObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem

open class GUIRendererProgram : ShadowMapProgram() {

    override fun getVertexShaderResId() = GUI_VERTEX_SHADER
    override fun getFragmentShaderResId() = GUI_FRAGMENT_SHADER

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {
        super.bindAdditionalParams(scene, renderable)
        params[EFFECTS_PARAM_NAME]?.value = (renderable as? GUI2DImageObject)?.effects
        params[TARGET_WIDTH_PARAM_NAME]?.value = 0
        params[TARGET_HEIGHT_PARAM_NAME]?.value = 0
    }

    override fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {}

    fun setAdditionalParams(paramMap: Map<String, Any>) {
        for (pair in paramMap.entries) {
            val param = params[pair.key]

            if (param != null && param.paramReference >= 0)
                param.value = pair.value
        }
    }

}
