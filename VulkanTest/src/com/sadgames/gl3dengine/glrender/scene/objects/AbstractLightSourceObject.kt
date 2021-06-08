package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.GLRenderConsts
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import javax.vecmath.Vector2f
import javax.vecmath.Vector4f

abstract class AbstractLightSourceObject (glScene: GLRendererInterface<SceneObjectsTreeItem>):
        GUI2DImageObject(glScene.getCachedShader(GLRenderConsts.GLObjectType.SUN_OBJECT),
                         Vector4f(-0.5f, 0.5f, 0.5f, -0.5f), false) {

    val lightSource: GLLightSource? = glScene.lightSource

    init {
        isLightSource = true
    }

    abstract fun get2DPos(): Vector2f?
}