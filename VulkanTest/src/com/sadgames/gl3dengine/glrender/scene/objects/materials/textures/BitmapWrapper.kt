package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

//import com.badlogic.gdx.graphics.glutils.ETC1
//import com.badlogic.gdx.graphics.glutils.ETC1.ETC1Data
//import com.sadgames.sysutils.common.ETC1Utils.ETC1Texture
import com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE
import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.vulkan.newclass.Gdx2DPixmap
import com.sadgames.vulkan.newclass.Pixmap
//import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.GlPixmap.Companion.createScaledTexture
import java.nio.ByteBuffer

class BitmapWrapper private constructor(rawData: ByteBuffer?, val width: Int, val height: Int, val isCompressed: Boolean) {
    var rawData = rawData; get() = field?.rewind() as ByteBuffer?; private set
    val imageSizeBytes = rawData?.capacity() ?: 0
    var name = ""
    val decodedRawData; get() = /*if (isCompressed) ETC1.decodeImage(ETC1Data(width, height, rawData, 0), Pixmap.Format.RGB888).pixels else*/ rawData
    val isEmpty; get() = rawData == null

    private var pixmap: Pixmap? = null

    //constructor(packedImg: ETC1Texture): this(packedImg.data, packedImg.width, packedImg.height, true)
    constructor(pixmap: Pixmap?): this(pixmap?.pixels, pixmap?.width ?: 0, pixmap?.height ?: 0, false) { this.pixmap = pixmap }
    constructor(color: Int): this(GlPixmap(2, 2, Pixmap.Format.RGBA8888, color))
    constructor(encodedImage: ByteBuffer): this((Pixmap(Gdx2DPixmap(encodedImage, TEXTURE_RESOLUTION_SCALE[GdxExt.preferences.graphicsQualityLevel.ordinal]))))

    fun release() {
        try { pixmap?.dispose() }
        catch (e: RuntimeException) {
            e.printStackTrace()
        } finally { pixmap = null }

        rawData?.limit(0)
        rawData = null
    }
}
