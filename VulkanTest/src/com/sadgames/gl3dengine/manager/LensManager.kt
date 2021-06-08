package com.sadgames.gl3dengine.manager

import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.LensObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem

val scales = floatArrayOf(1f, 0.46f, 0.2f, 0.1f, 0.04f, 0.12f, 0.24f, 0.14f, 0.024f, 0.4f, 0.2f, 0.14f, 0.6f, 0.8f, 1.2f)
val numbers = intArrayOf(0, 4, 2 ,7, 1, 3, 9, 5, 1, 7, 9, 3, 5, 4, 8)

fun generateLens(glScene: GLRendererInterface<SceneObjectsTreeItem>) {
    for (i in 0 .. 14) {
        val len = LensObject(glScene, scale = scales[i], spacing = 0.16f, number = i, texNum = numbers[i])
        len.loadObject()
        glScene.scene.putChild(len, len.itemName)
    }
}