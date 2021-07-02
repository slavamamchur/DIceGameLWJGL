package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.TextureParams.TextureWrap
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.glFramebufferTexture
import org.lwjgl.opengl.GL32.glTexParameterfv

class DepthTexture(width: Int, height: Int): RGBATexture(width, height) {

  @Throws(UnsupportedOperationException::class)
  override fun loadTexture(bitmap: BitmapWrapper?) = glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0)
  override fun attach() = glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, textureId, 0)

  override fun setTextureParams() {
    textureParams.filterMode = TextureParams.TextureFilter.Linear
    textureParams.wrapMode = TextureWrap.CLampToBorder

    super.setTextureParams()

    glTexParameterfv(textureType, GL_TEXTURE_BORDER_COLOR, floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f))
    glTexParameteri(textureType, GL_TEXTURE_COMPARE_MODE,GL_COMPARE_REF_TO_TEXTURE);
    glTexParameteri(textureType, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
  }
}
