package com.sadgames.gl3dengine.glrender.scene.fbo

import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import javax.vecmath.Color4f

@Suppress("LeakingThis")
abstract class AbstractFBO(var width: Int,
                           var height: Int,
                           private val clearColor: Color4f,
                           protected var hasAdditionalTextures: Boolean = false,
                           attachmentsCnt: Int = 1,
                           protected val isMultiSampled: Boolean = false) {

    private val fboID: Int = glGenFramebuffers()
    private val colorAttachments: ArrayList<AbstractTexture?> = ArrayList()

    protected val colorBuffers: ArrayList<Int> = ArrayList()

    var activeTexture = 0
    val fboTexture; get() = colorAttachments[activeTexture]

    init {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)

        for (i in (0 until attachmentsCnt))
            colorAttachments += attachTexture(i)

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            cleanUp()
            throw RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO")
        }

        unbind()
    }

    protected abstract fun attachTexture(num: Int): AbstractTexture?

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)

        val buffers = IntArray(colorAttachments.size)
        for (i in 0 until colorAttachments.size) {
            buffers[i] = GL_COLOR_ATTACHMENT0 + i

            glActiveTexture(GL_TEXTURE0 + i)
            glBindTexture(GL_TEXTURE_2D, 0)
        }

        glDrawBuffers(buffers)

        glViewport(0, 0, width, height)

        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    @Suppress("NOTHING_TO_INLINE") inline fun unbind() = glBindFramebuffer(GL_FRAMEBUFFER, 0)

    @JvmOverloads fun resolve2FBO(fbo: AbstractFBO, buffers: Int = GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo.fboID)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID)
        glReadBuffer(GL_COLOR_ATTACHMENT0 + activeTexture)
        glBlitFramebuffer(0, 0, width, height,
                          0, 0, fbo.width, fbo.height,
                          buffers,
                          GL20.GL_NEAREST)
        unbind()
    }

    fun resolve2Screen() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID)
        glReadBuffer(GL_COLOR_ATTACHMENT0 + activeTexture)
        glDrawBuffer(GL20.GL_BACK)
        glBlitFramebuffer(0, 0, width, height,
                          0, 0, GdxExt.width, GdxExt.height,
                          GL20.GL_COLOR_BUFFER_BIT,
                          GL20.GL_NEAREST)
        unbind()
    }

    open fun cleanUp() {
        unbind()
        glDeleteFramebuffers(intArrayOf(fboID))

        colorAttachments.forEach{ it?.deleteTexture() }
        colorAttachments.clear()
    }
}