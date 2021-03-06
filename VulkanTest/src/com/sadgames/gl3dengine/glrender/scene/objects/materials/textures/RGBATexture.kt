package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.glFramebufferTexture

open class RGBATexture(width: Int, height: Int, private val attachmentNum: Int = 0, isMultiSampled: Boolean = false): AbstractFBOTexture(width, height, isMultiSampled) {

    @Throws(UnsupportedOperationException::class) override fun loadTexture(bitmap: BitmapWrapper?) {
        glTexImage2D(textureType,
                0,
                GL_RGBA,
                width,
                height,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                0)
    }

    override fun attach() = glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + attachmentNum, textureId, 0)
}