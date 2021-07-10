package com.sadgames.gl3dengine.glrender.scene.fbo

import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import javax.vecmath.Color4f

@Suppress("LeakingThis", "NOTHING_TO_INLINE")
abstract class AbstractFBO(var width: Int,
                           var height: Int,
                           protected val clearColor: Color4f,
                           protected var hasAdditionalTextures: Boolean = false,
                           attachmentsCnt: Int = 1,
                           protected val isMultiSampled: Boolean = false,
                           protected val isFloat32: Boolean = false) {

    protected val fboID: Int = glGenFramebuffers()
    protected val colorBuffers: ArrayList<Int> = ArrayList()
    protected val blitMask; get() = getBltMask()

            var activeTexture = 0
    val colorAttachments: MutableList<AbstractTexture?> = ArrayList()
    val fboTexture; get() = colorAttachments[activeTexture]

    init {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)

        for (i in 0 until attachmentsCnt)
            colorAttachments += attachTexture(i)

        //val error = GL11.glGetError()
        //println(error)

        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            cleanUp()
            throw RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO")
        }

        unbind()
    }

    inline operator fun get(index: Int) = colorAttachments[index]
    inline infix fun blit(target: AbstractFBO) { this.resolve2FBO(target) }

    operator fun invoke(index: Int): AbstractFBO {
        activeTexture = index;
        return this
    }

    protected abstract fun attachTexture(num: Int): AbstractTexture?
    protected abstract fun getBltMask(): Int

    open fun bind() {
        glBindTexture(GL_TEXTURE_2D, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)

        val buffers = IntArray(colorAttachments.size)
        for (i in 0 until colorAttachments.size) {
            buffers[i] = GL_COLOR_ATTACHMENT0 + i
        }

        glDrawBuffers(buffers)

        glViewport(0, 0, width, height)

        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    inline fun unbind() = glBindFramebuffer(GL_FRAMEBUFFER, 0)

    @JvmOverloads fun resolve2FBO(fbo: AbstractFBO, texture: Int = activeTexture) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo.fboID)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID)
        glReadBuffer(GL_COLOR_ATTACHMENT0 + texture)
        glBlitFramebuffer(0, 0, width, height,
                          0, 0, fbo.width, fbo.height,
                          blitMask,
                          GL20.GL_NEAREST)
        unbind()
    }

    fun resolve2Screen(texture: Int = activeTexture) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID)
        glReadBuffer(GL_COLOR_ATTACHMENT0 + texture)
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