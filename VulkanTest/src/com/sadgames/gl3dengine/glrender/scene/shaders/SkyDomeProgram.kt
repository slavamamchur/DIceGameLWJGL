package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.sysutils.common.toArray

class SkyDomeProgram: SkyBoxProgram() {

    override fun getVertexShaderResId() = SKYDOME_VERTEX_SHADER
    override fun getFragmentShaderResId() = SKYDOME_FRAGMENT_SHADER

    override fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>) {
        super.bindGlobalParams(scene)
        params[LIGHT_COLOUR_PARAM_NAME]?.value = scene.lightSource!!.lightColour.toArray()
    }
}