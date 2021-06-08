package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.TextureParams.TextureFilter.Linear
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.TextureParams.TextureWrap.ClampToEdge
import com.sadgames.sysutils.common.CommonUtils
import org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X

class CubeMapTexture(textureName: String?):
        AbstractTexture(-1, -1, null, textureName, TextureParams(Linear, ClampToEdge)) {

    override val textureType; get() = GL_TEXTURE_CUBE_MAP

    @Throws(UnsupportedOperationException::class) override fun loadTexture(bitmap: BitmapWrapper?) {
        textureSize = 0
        var name: String? = null

        for (i in 0..5) try {
            name = textureName + "$i"
            val edge = CommonUtils.getBitmapFromFile(name, false)
            loadTextureInternal(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, edge!!)
            textureSize += edge.imageSizeBytes.toLong()
            edge.release()
        } catch (exception: Exception) {
            throw UnsupportedOperationException("Texture \"$name\" load error.")
        }
    }

}