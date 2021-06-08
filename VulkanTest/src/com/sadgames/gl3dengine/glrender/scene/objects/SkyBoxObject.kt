package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture

class SkyBoxObject(cubeTexture: AbstractTexture?, glScene: GLRendererInterface<SceneObjectsTreeItem>):
        AbstractSkyObject(cubeTexture, glScene.getCachedShader(GLObjectType.SKY_BOX_OBJECT)) {

    override fun createSkyPrimitive(halfSize: Float) =
        CubePrimitiveObject(null, program, 1f, COLLISION_OBJECT, halfSize)
}