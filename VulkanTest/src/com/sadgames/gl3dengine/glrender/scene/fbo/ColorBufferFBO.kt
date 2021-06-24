package com.sadgames.gl3dengine.glrender.scene.fbo

import com.sadgames.gl3dengine.glrender.GLRenderConsts.FBO_TEXTURE_SLOT
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.DepthTexture
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.RGBATexture
import org.lwjgl.opengl.GL30.*
import javax.vecmath.Color4f

class ColorBufferFBO @JvmOverloads constructor(width: Int, height: Int, clearColor: Color4f, hasDepthTexture: Boolean = false, attachmentsCnt: Int = 1, isMultiSampled: Boolean = false):
        AbstractFBO(width, height, clearColor, hasDepthTexture, attachmentsCnt, isMultiSampled) {
    private lateinit var depthBuffer: IntArray
    lateinit var depthTexture: AbstractTexture; private set

    override fun attachTexture(num: Int): AbstractTexture? {
        if (num == 0)
            if (hasAdditionalTextures) {
                depthTexture = DepthTexture(width, height)(0, true)
            } else {
                depthBuffer = IntArray(1)
                glGenRenderbuffers(depthBuffer)
                glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer[0])
                if (!isMultiSampled)
                    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height)
                else
                    glRenderbufferStorageMultisample(GL_RENDERBUFFER, 4, GL_DEPTH_COMPONENT16, width, height)
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer[0])
            }

        return if (!isMultiSampled)
                    RGBATexture(width, height, num)(FBO_TEXTURE_SLOT + num, true)
               else {
                    val colorBuffer = IntArray(1)
                    glGenRenderbuffers(colorBuffer)
                    glBindRenderbuffer(GL_RENDERBUFFER, colorBuffer[0])
                    glRenderbufferStorageMultisample(GL_RENDERBUFFER, 4, GL_RGBA8, width, height)
                    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + num, GL_RENDERBUFFER, colorBuffer[0])
                    colorBuffers.add(colorBuffer[0])

                    null
               }
    }

    override fun getBltMask() = (if (hasAdditionalTextures) GL_DEPTH_BUFFER_BIT else 0) or GL_COLOR_BUFFER_BIT

    override fun cleanUp() {
        if (hasAdditionalTextures)
            depthTexture.deleteTexture()
        else
            glDeleteRenderbuffers(depthBuffer)

        colorBuffers.forEach { glDeleteRenderbuffers(intArrayOf(it)) }
        colorBuffers.clear()

        super.cleanUp()
    }
}