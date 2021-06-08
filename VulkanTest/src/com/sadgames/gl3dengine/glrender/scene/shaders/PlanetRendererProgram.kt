package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import org.lwjgl.opengl.GL20
import java.util.*

class PlanetRendererProgram: TerrainRendererProgram() {

    override fun getShaderProgram() = MyShaderProgram(
            object: HashMap<Int, String>() {
                init {
                    put(GL20.GL_VERTEX_SHADER, PLANET_RENDERER_VERTEX_SHADER)
                    //put(GL_TESS_CONTROL_SHADER, PLANET_RENDERER_TC_SHADER)
                    //put(GL_TESS_EVALUATION_SHADER, PLANET_RENDERER_TE_SHADER)
                    //put(GL_GEOMETRY_SHADER, PLANET_RENDERER_GEOMETRY_SHADER)
                    put(GL20.GL_FRAGMENT_SHADER, PLANET_RENDERER_FRAGMENT_SHADER)
                }}
    )

    override fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>) {
        super.bindGlobalParams(scene)

        params[TESSELLATION_PARAMS_PARAM_NAME]?.value = floatArrayOf(8.0f, 8.0f, 0.5f, 1.1f)
    }
}