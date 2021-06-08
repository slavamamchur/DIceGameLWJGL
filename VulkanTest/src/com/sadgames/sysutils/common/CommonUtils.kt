package com.sadgames.sysutils.common

//import com.badlogic.gdx.graphics.glutils.ETC1
//import com.sadgames.vulkan.newclass.Pixmap
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.GameMapController
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapWrapper
import com.sadgames.gl3dengine.manager.GDXPreferences
import com.sadgames.gl3dengine.manager.SettingsManagerInterface
import org.lwjgl.BufferUtils
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.nio.channels.Channels
import java.util.*

object CommonUtils {

    @JvmStatic val settingsManager: SettingsManagerInterface; get() = GDXPreferences

    @JvmStatic fun convertStreamToString(stream: InputStream?): String {
        return if (stream == null) ""
               else {
                    val s = Scanner(stream).useDelimiter("\\A")
                    if (s.hasNext()) s.next()
                    else ""
               }
    }

    @JvmStatic fun getResourceStream(fileName: String?): InputStream? {
        return try {
                    FileInputStream(this.javaClass.getResource(fileName).path)
               } catch (e: FileNotFoundException) {
                    null
               } catch (e: NullPointerException) {
                    null
               }
    }

    @JvmStatic fun readTextFromFile(fileName: String?) = convertStreamToString(getResourceStream(fileName))

    @JvmStatic fun waitForGC() {
        var obj: Any? = Any()
        val ref: WeakReference<*> = WeakReference(obj)
        obj = null

        System.gc()
        System.runFinalization()

        while (ref.get() != null) try { Thread.sleep(100) } catch (ignored: InterruptedException) {} //System.gc();
    }

    @JvmStatic fun downloadBitmapIfNotCached(textureResName: String?, isRelief: Boolean) {
        val gmc = GameMapController()
        val map = gmc.find(textureResName!!) as? GameMapEntity?

        if (map?.id?.isEmpty() == false)
            try { if (isRelief) gmc.saveMapRelief(map) else gmc.saveMapImage(map) } catch (ignored: Exception) {}
    }

    @JvmStatic fun getBitmapFromFile(file: String?, isRelief: Boolean): BitmapWrapper? {
        var result = try {
                        getResourceStream("/textures/$file").use {
                        /*if (file!!.endsWith("pkm"))
                            result = BitmapWrapper(ETC1Utils.ETC1Texture.createFromStream(it))
                        else {*/
                            val output = BufferUtils.createByteBuffer(it!!.available())
                            Channels.newChannel(it).read(output);

                            BitmapWrapper(output.rewind())
                        //}
                        }
                    } catch (exception: Exception) {
                        null }

        result = result ?:
        try {
                val bitmapArray = DBUtils.loadBitmapFromDB(file, isRelief)
                if (bitmapArray != null) BitmapWrapper(BufferUtils.createByteBuffer(bitmapArray.size).put(bitmapArray).rewind()) else null
        } catch (exception: Exception) {
            null }

        result = result ?: try { BitmapWrapper(file!!.toInt()) } catch (exception: Exception) { null }

        result?.name = file ?: ""
        return result
    }

    /*@JvmStatic private fun compressTexture(input: ByteBuffer, width: Int, height: Int): BitmapWrapper {
        val pixmap: Pixmap = GlPixmap(input, width, height, Pixmap.Format.RGB888)
        val compressedImage = ETC1.encodeImage(pixmap).compressedData

        pixmap.dispose()

        return BitmapWrapper(ETC1Utils.ETC1Texture(width, height, compressedImage))
    }

    @JvmStatic fun packToETC1(bitmap: BitmapWrapper): BitmapWrapper {
        val width = bitmap.width
        val height = bitmap.height
        val bb = ByteBuffer.allocateDirect(width * height * 3).order(ByteOrder.nativeOrder())
        val rawImage = bitmap.rawData!!.rewind() as ByteBuffer

        for (i in 0 until height * width) {
            val value = rawImage.int

            bb.put((value shr 24).toByte())
            bb.put((value shr 16).toByte())
            bb.put((value shr 8).toByte())
        }

        bb.rewind()
        val texture = compressTexture(bb, width, height)
        bb.limit(0)

        return texture
    }*/
}