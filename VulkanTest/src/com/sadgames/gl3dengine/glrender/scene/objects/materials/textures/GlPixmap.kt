package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

import com.sadgames.vulkan.newclass.Gdx2DPixmap
import com.sadgames.vulkan.newclass.Pixmap
import com.sadgames.sysutils.common.ColorUtils.convert2libGDX
import java.nio.ByteBuffer

class GlPixmap: Pixmap {
    @JvmOverloads constructor(width: Int, height: Int, format: Format?, fillColor: Int = 0): super(width, height, format, fillColor)
    constructor(pixels: ByteBuffer, width: Int, height: Int, format: Format?): super(width, height, format, 0) { setPixels(pixels) }
    constructor(encodedData: ByteBuffer?, offset: Int, length: Int): super(encodedData, offset, length)
    constructor(dst: Gdx2DPixmap?): super(dst)

    override fun setColor(color: Int) = super.setColor(convert2libGDX(color))
    override fun getPixel(x: Int, y: Int) = convert2libGDX(super.getPixel(x, y))


/* companion object { //todo: implement
        @JvmStatic fun createScaledTexture(src: Gdx2DPixmap, scale: Int): Pixmap {
            var dst = src
            if (scale != 1) {
                val srcWidth = src.width
                val srcHeight = src.height
                val dstWidth = srcWidth / scale
                val dstHeight = srcHeight / scale
                dst = Gdx2DPixmap(dstWidth, dstHeight, src.format)
                dst.drawPixmap(src, 0, 0, srcWidth, srcHeight, 0, 0, dstWidth, dstHeight)
                src.dispose()
            }

            return Pixmap(dst)
        }
    }*/
}