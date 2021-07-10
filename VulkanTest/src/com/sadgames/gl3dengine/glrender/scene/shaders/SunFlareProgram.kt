package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.gamelogic.client.GameConst
import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.objects.SunObject
import com.sadgames.sysutils.common.toArray

class SunFlareProgram: GUIRendererProgram() {

    override fun getFragmentShaderResId() = FLARE_FRAGMENT_SHADER
    override fun getGeometryShaderResId(): String? = null

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) {
        val sun = scene.scene.getChild(GameConst.SUN_OBJECT) as SunObject

        params[LIGHT_COLOUR_PARAM_NAME]?.value = sun.lightSource!!.lightColour.toArray()
        params[AMBIENT_RATE_PARAM_NAME]?.value = sun.brightness * 0.6f
    }

}
