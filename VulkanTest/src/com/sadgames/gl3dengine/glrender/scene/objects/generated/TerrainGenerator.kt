package com.sadgames.gl3dengine.glrender.scene.objects.generated

import com.sadgames.gl3dengine.gamelogic.client.GameConst.*
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractTerrainObject
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import com.sadgames.gl3dengine.manager.TextureCache
import com.sadgames.sysutils.common.MathUtils.cos
import com.sadgames.sysutils.common.pow
import java.util.*

class TerrainGenerator(textureResName: String, program: VBOShaderProgram, gridX: Int, gridZ: Int, vertexCount: Int,
                       seed: Int): AbstractTerrainObject(textureResName, program) {
    companion object {
        private const val AMPLITUDE = 10f
        private const val OCTAVES = 3
        private const val ROUGHNESS = 0.3f

        val random = Random()
    }

    private val seed: Int
    private val xOffset: Int
    private val zOffset: Int

    constructor(gameEntity: GameEntity, program: VBOShaderProgram):
            this(gameEntity.getMapId(), program, 0, 0, 0, -1 /*666*/)

    init {
        isCubeMap = true //todo: fix shader -> change to terr32

        glCubeMap = TextureCache[SAND_TEXTURE_NAME]
        glNormalMap = TextureCache[NORMALMAP_TERRAIN_ATLAS_TEXTURE_NAME]
        glDUDVMap = TextureCache[DISTORTION_TERRAIN_ATLAS_TEXTURE_NAME]

        this.seed = if (seed == -1) random.nextInt(1000000000) else seed
        xOffset = gridX * (vertexCount - 1)
        zOffset = gridZ * (vertexCount - 1)
    }

    override fun getHeightValue(x: Int, z: Int): Float {
        var total = 0f

        for (i in 0 until OCTAVES) {
            val freq = 2.0f pow i.toFloat() / (2.0f pow (OCTAVES - 1.0f))
            val amp = ROUGHNESS pow i.toFloat() * AMPLITUDE

            total += getInterpolatedNoise((x + xOffset) * freq, (z + zOffset) * freq, seed) * amp
        }

        return total * landScale * 2f
    }

    override fun getDimension() = 249
    override fun disposeTempData() {}
}

fun getNoise(x: Int, z: Int, seed: Int): Float {
    TerrainGenerator.random.setSeed(x * 49632 + z * 325176 + seed.toLong())
    return TerrainGenerator.random.nextFloat() * 2f - 1f
}

fun getSmoothNoise(x: Int, z: Int, seed: Int): Float {
    val corners = (getNoise(x - 1, z - 1, seed) + getNoise(x + 1, z - 1, seed)
            + getNoise(x - 1, z + 1, seed) + getNoise(x + 1, z + 1, seed)) / 16f
    val sides = (getNoise(x - 1, z, seed) + getNoise(x + 1, z, seed) + getNoise(x, z - 1, seed)
            + getNoise(x, z + 1, seed)) / 8f
    val center = getNoise(x, z, seed) / 4f

    return corners + sides + center
}

fun interpolate(a: Float, b: Float, blend: Float): Float {
    val f = (1f - cos((blend * Math.PI).toFloat())) * 0.5f
    return a * (1f - f) + b * f
}

fun getInterpolatedNoise(x: Float, z: Float, seed: Int): Float {
    val intX = x.toInt()
    val intZ = z.toInt()
    val fracX = x - intX
    val fracZ = z - intZ
    val v1 = getSmoothNoise(intX, intZ, seed)
    val v2 = getSmoothNoise(intX + 1, intZ, seed)
    val v3 = getSmoothNoise(intX, intZ + 1, seed)
    val v4 = getSmoothNoise(intX + 1, intZ + 1, seed)
    val i1 = interpolate(v1, v2, fracX)
    val i2 = interpolate(v3, v4, fracX)

    return interpolate(i1, i2, fracZ)
}
