package com.sadgames.gl3dengine.glrender

import com.sadgames.gl3dengine.gamelogic.client.GameLogic
import com.sadgames.gl3dengine.glrender.GLRenderConsts.RenderType
import com.sadgames.gl3dengine.glrender.scene.GLScene
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import java.util.*

object GLRendererFabric {
    private val renderers: MutableMap<RenderType, GLRendererInterface<SceneObjectsTreeItem>> = EnumMap(RenderType::class.java)

    @JvmStatic fun produceRenderByType(type: RenderType, logic: GameLogic) =
        renderers[type]?:
        when (type) {
            RenderType.GL41_RENDER -> {
                val scene: GLRendererInterface<SceneObjectsTreeItem> = GLScene(logic)
                scene.luaEngine = logic.initScriptEngine(scene)
                renderers[type] = scene
                renderers[type]
            }
            else -> {
                renderers[type] = GLScene(logic)
                renderers[type]
            }
        }
}