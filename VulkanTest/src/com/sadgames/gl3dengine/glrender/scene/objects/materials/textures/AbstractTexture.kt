package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import com.sadgames.gl3dengine.manager.AbstractEntityCacheManager.CachedEntity
import org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT
import org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengles.OESCompressedETC1RGB8Texture
import java.util.*

abstract class AbstractTexture(width: Int,
                               height: Int,
                               bitmap: BitmapWrapper?,
                               textureName: String?,
                               protected var textureParams: TextureParams): CachedEntity {
    companion object {
        private const val GL_EXT_TEXTURE_FILTER_ANISOTROPIC = "GL_EXT_texture_filter_anisotropic"
        val GLOBAL_USE_MIP_MAP = true
    }

    var width = width; private set
    var height = height; private set
    var textureId = 0; private set
    var textureSize: Long = bitmap?.imageSizeBytes?.toLong() ?: 0.toLong(); protected set
    var textureName: String? = textureName; protected set

    protected var textureData: BitmapWrapper? = null
    protected open val textureType; get() = GL_TEXTURE_2D

    override val name; get() = textureName!!
    override val size; get() = textureSize
    override val isDeleted; get() = textureId == 0
    override val isReleased; get() = textureData == null

    @JvmOverloads constructor(width: Int, height: Int, bitmap: BitmapWrapper?, textureName: String? = bitmap?.name, useMipMap: Boolean = GLOBAL_USE_MIP_MAP) : this(width, height, bitmap, textureName, TextureParams(useMipMap))

    init {
        createTexture(bitmap)
    }

    @Throws(UnsupportedOperationException::class) protected abstract fun loadTexture(bitmap: BitmapWrapper?)

    protected fun setTextureParams() {
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glTexParameteri(textureType, GL_TEXTURE_MIN_FILTER, textureParams.filterMode.gLEnum)
        glTexParameteri(textureType, GL_TEXTURE_MAG_FILTER, textureParams.filterMode.gLEnum)

        glTexParameteri(textureType, GL_TEXTURE_WRAP_S, textureParams.wrapMode.gLEnum)
        glTexParameteri(textureType, GL_TEXTURE_WRAP_T, textureParams.wrapMode.gLEnum)

        if (textureParams.filterMode.isMipMap) {
            val max = FloatArray(16)
            glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max)
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, max[0].coerceAtMost(16f)) //todo: use quality settings
        }
    }

    fun createTexture(bitmap: BitmapWrapper?) {
        val textureIds = IntArray(1)
        glGenTextures(textureIds)

        if (textureIds[0] != 0) {
            /*if (this is CubeMapTexture)
                glBindTextureCube(textureIds[0])
            else*/ glBindTexture(GL_TEXTURE_2D, textureIds[0])

            setTextureParams()

            try {
                loadTexture(bitmap)
            } catch (exception: UnsupportedOperationException) {
                textureIds[0] = 0
            }
        }

        textureId = textureIds[0]
        textureData = bitmap
    }

    @JvmOverloads fun bind(glTextureSlot: Int = 0): Int {
        return  if (textureId <= 0)
                    -1
                else {
                    val realSlot = GL13.GL_TEXTURE0 + glTextureSlot
                    glActiveTexture(realSlot)

                    /*if (this is CubeMapTexture)
                        glBindTextureCube(textureId)
                    else*/
                        glBindTexture(GL_TEXTURE_2D, textureId)

                    realSlot
                }
    }

    fun deleteTexture() { glDeleteTextures(intArrayOf(textureId)); textureId = 0 }

    fun loadTextureInternal(target: Int, bitmap: BitmapWrapper) {
        try {
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)

            if (!bitmap.isCompressed)
                glTexImage2D(target,
                            0,
                            GL_RGBA,
                            width,
                            height,
                            0,
                            GL_RGBA,
                            GL_UNSIGNED_BYTE,
                            bitmap.rawData)
            else {
                val data = bitmap.rawData

                glCompressedTexImage2D(target,
                                      0,
                                       OESCompressedETC1RGB8Texture.GL_ETC1_RGB8_OES,
                                       bitmap.width,
                                       bitmap.height,
                                      0,
                                       data)
            }
        } catch (exception: Exception) { throw UnsupportedOperationException() }
    }

    override fun reloadData() = createTexture(null)
    override fun delete() { if (textureId > 0) deleteTexture() }
    override fun release() { textureData?.release(); textureData = null }

    /** public static boolean isETC1Supported() {
     * int[] results = new int[20];
     * glGetIntegerv(GL_NUM_COMPRESSED_TEXTURE_FORMATS, results, 0);
     * int numFormats = results[0];
     * if (numFormats > results.length) {
     * results = new int[numFormats];
     * }
     * glGetIntegerv(GL_COMPRESSED_TEXTURE_FORMATS, results, 0);
     * for (int i = 0; i < numFormats; i++) {
     * if (results[i] == ETC1_RGB8_OES) {
     * return true;
     * }
     * }
     * return false;
     * }  */

}