package com.sadgames.gl3dengine.glrender.scene.shaders.params

import org.lwjgl.opengl.GL15.*
import java.nio.Buffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.util.*

class VBOData(val type: ElementType, val size: Int, val stride: Int, val pos: Int, data: Buffer) {

    enum class ElementType {VERTEX, INDEX}

    @JvmField val sizes: MutableMap<ElementType, Int> = object: EnumMap<ElementType, Int>(ElementType::class.java) {
        init {
            put(ElementType.VERTEX, Float.SIZE_BITS / 8)
            put(ElementType.INDEX, Short.SIZE_BYTES)
            //todo: add long_index
        }
    }

    @JvmField val types: MutableMap<ElementType, Int> = object: EnumMap<ElementType, Int>(ElementType::class.java) {
        init {
            put(ElementType.VERTEX,  GL_ARRAY_BUFFER)
            put(ElementType.INDEX, GL_ELEMENT_ARRAY_BUFFER)
        }
    }

    var vboPtr = glGenBuffers(); private set

    constructor(data: Buffer): this(ElementType.INDEX, Short.SIZE_BYTES, 0, 0, data)

    init {
        val glType = types[type]

        glBindBuffer(glType!!, vboPtr)
        if (data is ShortBuffer)
            glBufferData(glType, data, GL_STATIC_DRAW)
        else if (data is FloatBuffer)
            glBufferData(glType, data, GL_STATIC_DRAW)
        glBindBuffer(glType, 0)
    }

    fun clear() {
        if (vboPtr != 0) {
            glDeleteBuffers(intArrayOf(vboPtr))
            vboPtr = 0
        }
    }
}