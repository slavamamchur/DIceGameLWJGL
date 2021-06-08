package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.gamelogic.GameEventsCallbackInterface
import com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_KM
import com.sadgames.gl3dengine.glrender.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject.ColorType
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject.MAX_HEIGHT_VALUES
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import com.sadgames.sysutils.common.clamp
import com.sadgames.sysutils.common.plus
import com.sadgames.sysutils.common.times
import com.sadgames.sysutils.common.unaryMinus
import com.sadgames.vulkan.newclass.Gdx2DPixmap
import com.sadgames.vulkan.newclass.Pixmap
import java.util.*
import javax.vecmath.Vector2f
import javax.vecmath.Vector4f

open class InstancedRandomizer(logic: GameEventsCallbackInterface?,
                          objFileName: String,
                          program: VBOShaderProgram?,
                          itemCount: Int,
                          protected val density: Float,
                          repeated: Boolean,
                          protected val underWater: Boolean): AbstractInstancedObject(objFileName, program, itemCount)  {

    private val positions = ArrayList<Vector2f>()
    private val path = createPathMap(logic)
    private val rnd = Random(if (repeated) 666 else System.currentTimeMillis())

    private fun getClosestDistance(place: Vector2f): Float {
        var result = Float.MAX_VALUE

        for (item in positions) {
            val len = (place + -item).length()
            result = if (len < result) len else result
        }

        return result
    }

    private fun checkPosition(path: Pixmap, place: Vector2f, height: Float): Boolean {
        val mapPlace = world2Map(path, place)
        val color = path.getPixel(mapPlace.x.toInt(), mapPlace.y.toInt())

        return if (underWater)
                    height < 0 && getClosestDistance(place) >= modelWidth / (2f * density)
               else
                    height > 0
                    && height <= MAX_HEIGHT_VALUES[ColorType.GREEN.ordinal] * LAND_SIZE_IN_WORLD_SPACE / LAND_SIZE_IN_KM * 3f
                    && getClosestDistance(place) >= modelWidth / (2f * density)
                    && (color == 0 || color == -16777216)
    }

    override fun getNextTransform(number: Int): Vector4f {
        val scaleFactor = 0.75f + rnd.nextFloat() / 2.0f //-> [0.75 : 1.25]
        //val rotationY = rnd.nextFloat() * 360.0f;
        var place: Vector2f
        var height: Float

        do {
            place = Vector2f(rnd.nextFloat() * LAND_SIZE_IN_WORLD_SPACE - LAND_SIZE_IN_WORLD_SPACE / 2.0f,
                    rnd.nextFloat() * LAND_SIZE_IN_WORLD_SPACE - LAND_SIZE_IN_WORLD_SPACE / 2.0f)
            height = parent?.getPlaceHeight(place) ?: 0f
        } while (!checkPosition(path, place, height))

        positions.add(place)

        return Vector4f(place.x, height, place.y, scaleFactor)

        /*val transform = Mat4x4(modelMatrix)
        transform()
        transform *= Vector3f(0, rotationY, 0)
        transform.value[12] += place.x
        transform.value[13] += height
        transform.value[14] += place.y
        transform *= scaleFactor*/
    }

    override fun getRaw3DModel(): Raw3DModel {
        val result =  super.getRaw3DModel()
        path.dispose()

        return result
    }

    override fun loadObject() {
        super.loadObject()
        positions.clear()
    }

    private fun createPathMap(logic: GameEventsCallbackInterface?): Pixmap {
        val blendMap = Pixmap(Gdx2DPixmap(256, 256, Gdx2DPixmap.GDX2D_FORMAT_RGBA8888, 0))
        blendMap.setColor(0)

        logic?.onPrepareMapTexture(blendMap)

        return blendMap
    }

    private fun world2Map(path: Pixmap, place: Vector2f) =
            (Vector2f(place) + Vector2f(LAND_SIZE_IN_WORLD_SPACE / 2.0f, LAND_SIZE_IN_WORLD_SPACE / 2.0f)) *
                    (path.width / LAND_SIZE_IN_WORLD_SPACE) clamp Vector2f(0f, path.width - 1.0f)
}