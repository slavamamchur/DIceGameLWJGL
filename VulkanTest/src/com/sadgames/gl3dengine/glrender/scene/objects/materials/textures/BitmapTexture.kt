package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import com.sadgames.sysutils.common.CommonUtils.getBitmapFromFile
import org.lwjgl.opengl.GL30.glGenerateMipmap

class BitmapTexture private constructor(bitmap: BitmapWrapper, useMipMap: Boolean):
        AbstractTexture(bitmap.width, bitmap.height, bitmap, bitmap.name, useMipMap) {

    @Throws(UnsupportedOperationException::class) override fun loadTexture(bitmap: BitmapWrapper?) {
        if (bitmap != null) {
            loadTextureInternal(textureType, bitmap)

            if (textureParams.filterMode.isMipMap)
                glGenerateMipmap(textureType)
        }
    }

    override fun reloadData() = createTexture(textureData ?: getBitmapFromFile(textureName, false))

    companion object {
        @JvmStatic fun createInstance(bitmap: BitmapWrapper) = BitmapTexture(bitmap, GLOBAL_USE_MIP_MAP)
        @JvmStatic fun createInstance(file: String?) = createInstance(file, GLOBAL_USE_MIP_MAP)
        @JvmStatic fun createInstance(file: String?, useMipMap: Boolean) = BitmapTexture(getBitmapFromFile(file, false)!!, useMipMap)
    }
}
