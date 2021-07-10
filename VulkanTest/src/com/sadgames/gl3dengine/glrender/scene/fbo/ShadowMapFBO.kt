package com.sadgames.gl3dengine.glrender.scene.fbo

import com.sadgames.gl3dengine.glrender.GLRenderConsts
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.PSSMTexture
import org.lwjgl.opengl.GL30.*

class ShadowMapFBO(width: Int, height: Int): DepthBufferFBO(width, height) {
    override fun attachTexture(num: Int) = PSSMTexture(width, height)(GLRenderConsts.FBO_TEXTURE_SLOT, true)

    override fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)

        for (i in 0 until 3)
            glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, fboTexture!!.textureId, 0, i)

        glReadBuffer(GL_NONE)
        glDrawBuffer(GL_NONE)

        glViewport(0, 0, width, height)
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)
        glClear(GL_DEPTH_BUFFER_BIT)
    }
}