package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.glFramebufferTexture

class DepthTexture(width: Int, height: Int): RGBATexture(width, height) {

  @Throws(UnsupportedOperationException::class)
  override fun loadTexture(bitmap: BitmapWrapper?) = glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT16, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0)

  override fun attach() = glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, textureId, 0)
}
