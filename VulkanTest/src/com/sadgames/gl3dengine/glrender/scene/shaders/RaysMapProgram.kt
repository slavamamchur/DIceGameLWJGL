package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem

class RaysMapProgram : ReflectionMapRenderProgram() {

    init {
        drawTextures = false
    }

    override fun getVertexShaderResId() = RAYS_VERTEX_SHADER
    override fun getFragmentShaderResId() = RAYS_FRAGMENT_SHADER

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {
        super.bindAdditionalParams(scene, renderable)

        params[IS_LIGHT_SOURCE_PARAM_NAME]?.value = if (renderable.isLightSource) 1 else 0
        params[LIGHT_COLOUR_PARAM_NAME]?.value = scene.lightSource!!.lightColour
    }
}