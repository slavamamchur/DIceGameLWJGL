package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL30.*

class RGBFTexture(width: Int, height: Int, attachmentNum: Int = 0, useMipmap: Boolean = false): RGBATexture(width, height, attachmentNum) {

    //override val textureType; get() = GL_TEXTURE_2D_MULTISAMPLE

    @Throws(UnsupportedOperationException::class) override fun loadTexture(bitmap: BitmapWrapper?) {
        glTexImage2D/*Multisample*/(textureType,0, GL_RG32F, width, height, 0, GL_RGBA, GL_FLOAT, 0)

        if (textureParams.filterMode.isMipMap)
            glGenerateMipmap(textureType)
    }

    /*override fun attach() = GL32.glFramebufferTexture2D(
        GL_FRAMEBUFFER,
        GL_COLOR_ATTACHMENT0,
        textureType,
        textureId,
        0
    )

    override fun bind(glTextureSlot: Int): Int {
        return  if (textureId <= 0)
            -1
        else {
            glActiveTexture(GL_TEXTURE0 + glTextureSlot)

            *//*if (this is CubeMapTexture)
                glBindTextureCube(textureId)
            else*//*
            glBindTexture(textureType, textureId)

            glTextureSlot
        }
    }*/

    //todo: use variance shadow map (because witout bias) filtered by gaussian blur ??? or hardware anisitropic mimmap
    override fun setTextureParams() {
        textureParams.filterMode = TextureParams.TextureFilter.Linear
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, textureParams.filterMode.gLEnum)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, TextureParams.TextureFilter.Nearest.gLEnum)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, textureParams.wrapMode.gLEnum)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, textureParams.wrapMode.gLEnum)

        if (textureParams.filterMode.isMipMap) {
            val max = FloatArray(16)
            glGetFloatv(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max)
            glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, max[0].coerceAtMost(16f)) //todo: use quality settings
        }

        //super.setTextureParams()

        glTexParameteri(textureType, GL_TEXTURE_COMPARE_MODE,GL_COMPARE_REF_TO_TEXTURE);
        glTexParameteri(textureType, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
    }
}