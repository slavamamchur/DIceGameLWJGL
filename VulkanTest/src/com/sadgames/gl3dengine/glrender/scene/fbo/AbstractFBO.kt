package com.sadgames.gl3dengine.glrender.scene.fbo

import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import javax.vecmath.Color4f

abstract class AbstractFBO(var width: Int,
                           var height: Int,
                           private val clearColor: Color4f,
                           protected var hasAdditionalTextures: Boolean = false,
                           attachmentsCnt: Int = 1,
                           protected val isMultiSampled: Boolean = false) {

    private val fboID: Int
    private val colorAttachments: ArrayList<AbstractTexture?> = ArrayList()
    protected val colorBuffers: ArrayList<Int> = ArrayList()

    var activeTexture = 0
    val fboTexture; get() = colorAttachments[activeTexture]

    init {
        fboID = createFBO(attachmentsCnt)
    }

    fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)
        glDrawBuffer(colorAttachments.size)
        glViewport(0, 0, width, height)
        glEnable(GL_CULL_FACE)
        /*Gdx.gl30.glEnable(GL30.GL_DEPTH_TEST);
        Gdx.gl30.glDepthFunc(GL30.GL_LEQUAL);
        Gdx.gl30.glDepthMask(true);*/
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    inline fun unbind() = glBindFramebuffer(GL_FRAMEBUFFER, 0)

    @JvmOverloads fun resolve2FBO(fbo: AbstractFBO, buffers: Int = GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo.fboID)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID)
        glReadBuffer(if (isMultiSampled) colorBuffers[activeTexture] else colorAttachments[activeTexture]?.textureId ?: 0)
        glBlitFramebuffer(0, 0, width, height,
                          0, 0, fbo.width, fbo.height,
                          buffers,
                          GL20.GL_NEAREST)
        unbind()
    }

    fun resolve2Screen() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID)
        glReadBuffer(if (isMultiSampled) colorBuffers[activeTexture] else colorAttachments[activeTexture]?.textureId ?: 0)
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
        colorAttachments.forEach { it?.deleteTexture() }
        colorAttachments.clear()
    }

    protected abstract fun attachTexture(num: Int): AbstractTexture?

    private fun createFBO(attachmentsCnt: Int): Int {
        val fbos = IntArray(1)
        fbos[0] = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fbos[0])

        for (i in (0 until attachmentsCnt))
            colorAttachments.add(attachTexture(i))

        try {
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) throw RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO")
        } finally {
            unbind()
        }

        return fbos[0]
    }
}