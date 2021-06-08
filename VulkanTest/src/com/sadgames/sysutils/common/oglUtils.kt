package com.sadgames.sysutils.common

import org.lwjgl.BufferUtils.*
import java.nio.FloatBuffer
import javax.vecmath.Matrix4f
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

    fun convertToScreenSpace(worldPos: Vector3f, MVP: Matrix4f): Vector2f? {
        val coords = Vector4f(worldPos.x, worldPos.y, worldPos.z, 1f)
        MVP.transform(coords)

        return if (coords.w <= 0) {
                    null
               }
               else {
                    Vector2f((coords.x / coords.w + 1f) / 2f, 1f - (coords.y / coords.w + 1f) / 2f)
               }
    }

    fun FloatArray.toBuffer() = createFloatBuffer(this.size).put(this).rewind() as FloatBuffer?