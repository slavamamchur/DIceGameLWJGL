package com.sadgames.gl3dengine.gamelogic.client.entities

import com.sadgames.gl3dengine.gamelogic.client.GameConst.*
import com.sadgames.gl3dengine.gamelogic.server.rest_api.LinkedRESTObjectInterface
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity
import com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import com.sadgames.gl3dengine.manager.TextureCache
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import com.sadgames.sysutils.common.DBUtils.loadBitmapFromDB
import com.sadgames.vulkan.newclass.Gdx2DPixmap
import com.sadgames.vulkan.newclass.Pixmap
import org.lwjgl.BufferUtils
import java.io.IOException
import java.nio.ByteBuffer
import java.sql.SQLException

class GameMap(program: VBOShaderProgram, private val gameEntity: GameEntity?) :
        TopographicMapObject(program, /*"rel_" + */gameEntity?.getMapId()),
        LinkedRESTObjectInterface {

    init {
        isCubeMap = false //todo: true
        //castShadow = true

        glCubeMap = TextureCache[SAND_TEXTURE_NAME]
        glNormalMap = TextureCache[NORMALMAP_TERRAIN_ATLAS_TEXTURE_NAME]
        glDUDVMap = TextureCache[DISTORTION_TERRAIN_ATLAS_TEXTURE_NAME]
    }

    override fun getLinkedRESTObject() = gameEntity

    override fun loadReliefMap(): ByteBuffer? {
        return try {
            val data = loadBitmapFromDB(textureResName, true) //false
            if (data != null) {
                BufferUtils.createByteBuffer(data.size).put(data).rewind()
            }
            else
                null
        } catch (e: SQLException) {
            //e.printStackTrace()
            null
        } catch (e: IOException) {
            //e.printStackTrace()
            null
        }
    }

}
