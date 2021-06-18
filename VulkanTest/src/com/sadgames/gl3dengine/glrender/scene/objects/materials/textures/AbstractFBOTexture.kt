package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.TextureParams.TextureFilter
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.TextureParams.TextureWrap


abstract class AbstractFBOTexture(width: Int, height: Int):
                AbstractTexture(width, height, null, null, TextureParams(TextureFilter.Linear, TextureWrap.ClampToEdge)) {

    abstract fun attach()

    operator fun invoke(slot: Int, attach2FBO: Boolean = false): AbstractTexture {
        bind(slot)
        if (attach2FBO) attach()

        return this
    }
}