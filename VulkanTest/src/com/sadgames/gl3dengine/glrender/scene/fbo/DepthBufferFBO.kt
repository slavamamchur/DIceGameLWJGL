package com.sadgames.gl3dengine.glrender.scene.fbo

import com.sadgames.gl3dengine.glrender.GLRenderConsts.DEPTH_BUFFER_CLEAR_COLOR
import com.sadgames.gl3dengine.glrender.GLRenderConsts.FBO_TEXTURE_SLOT
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.DepthTexture
import org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
import javax.vecmath.Color4f

class DepthBufferFBO(width: Int, height: Int, color: Color4f = DEPTH_BUFFER_CLEAR_COLOR): AbstractFBO(width, height, color) {
    override fun attachTexture(num: Int) = DepthTexture(width, height)(FBO_TEXTURE_SLOT, true)
    override fun getBltMask() = GL_DEPTH_BUFFER_BIT
}