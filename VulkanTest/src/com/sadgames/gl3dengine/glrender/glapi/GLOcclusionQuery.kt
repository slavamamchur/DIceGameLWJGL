package com.sadgames.gl3dengine.glrender.glapi

import org.lwjgl.opengl.GL15.*
import org.lwjgl.system.MemoryStack

class GLOcclusionQuery {

    val id = glGenQueries()
    var isInUse = false; private set
    val isResultReady: Boolean; get() {
        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocInt(1)

            glGetQueryObjectuiv(id, GL_QUERY_RESULT_AVAILABLE, buffer)

            return buffer.rewind().hasRemaining() && buffer.get() == GL_TRUE
        }
    }

    val result: Int; get() {
        isInUse = false

        MemoryStack.stackPush().use { stack ->
        val buffer = stack.mallocInt(1)

        glGetQueryObjectuiv(id, GL_QUERY_RESULT, buffer)

        return if (buffer.rewind().hasRemaining()) buffer.get() else -1
        }
    }

    fun start() {
        glBeginQuery(GL_SAMPLES_PASSED, id)
        isInUse = true
    }

    inline fun end() = glEndQuery(GL_SAMPLES_PASSED)
    inline fun delete() = glDeleteQueries(id)

}
