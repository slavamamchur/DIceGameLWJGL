package com.sadgames.gl3dengine.manager

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import com.sadgames.gl3dengine.GLEngineConsts.TEXTURE_CACHE_RAM_SIZE
import com.sadgames.gl3dengine.GLEngineConsts.TEXTURE_CACHE_SIZE
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.createTexture

object TextureCache: AbstractEntityCacheManager<AbstractTexture>(TEXTURE_CACHE_SIZE, TEXTURE_CACHE_RAM_SIZE, ::createTexture)
