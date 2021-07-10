package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.sysutils.common.CommonUtils.readTextFromFile
import com.sadgames.vulkan.newclass.reflection.ObjectIntMap
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER
import org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class MyShaderProgram internal constructor(private val programList: Map<Int, String>) {

    companion object {
        @JvmField var prependCode = "#version 410\n#define GLES330\n"

        @JvmField val programTypes: MutableMap<Int, String> = object: HashMap<Int, String>() {
            init {
                put(GL_VERTEX_SHADER, "Vertex")
                put(GL_TESS_CONTROL_SHADER, "Tessellation Control")
                put(GL_TESS_EVALUATION_SHADER, "Tessellation Evaluation")
                put(GL_GEOMETRY_SHADER, "Geometry")
                put(GL_FRAGMENT_SHADER, "Fragment")
            }
        }

        /** String defineClipPlane = glExtensions().contains(GL_EXT_clip_cull_distance) ? "#define CLIP_PLANE\n" : "";
        MyShaderProgram.prependVertexCode += defineClipPlane; */
    }

    private val handlesList: MutableList<Int> = ArrayList()

    var log = ""
        get(): String {
            if (isCompiled && field.isEmpty())
                field = glGetProgramInfoLog(programId)

            return field
        }

    var programId = 0
    var isCompiled = false

    var attributeNames: Array<String?>? = null; private set
    val uniformTypes =
        ObjectIntMap<String>()

    init {
        require(programList[GL_VERTEX_SHADER]?.isNotEmpty() ?: false) { "vertex shader must not be null" }
        require(programList[GL_FRAGMENT_SHADER]?.isNotEmpty() ?: false) { "fragment shader must not be null" }

        compileShaders()

        if(isCompiled) {
            fetchAttributes()
            fetchUniforms()
        }
    }

    private fun createProgram(): Int {
        programId = glCreateProgram()
        return if (programId != 0) programId else -1
    }

    private fun loadShader(type: Int, source: String?): Int {
            var shader = glCreateShader(type)

            if (shader != 0) {
                val buffer = BufferUtils.createIntBuffer(1)

                glShaderSource(shader, source)
                glCompileShader(shader)
                glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, buffer)

                if (buffer[0] == 0) {
                    val sType = programTypes[type]
                    log += "$sType program:\n" + glGetShaderInfoLog(shader)
                    shader = 0
                }
            }

            return if (shader == 0) -1 else shader
    }

    private fun linkProgram(program: Int): Int {
        var mProgram = program

        if (mProgram > -1) {
            handlesList.forEach {
                glAttachShader(mProgram, it)
            }

            glLinkProgram(mProgram)

            val tmp = ByteBuffer.allocateDirect(4)
            tmp.order(ByteOrder.nativeOrder())
            val intbuf = tmp.asIntBuffer()
            glGetProgramiv(mProgram, GL_LINK_STATUS, intbuf)

            if (intbuf[0] == 0) {
                val log = glGetProgramInfoLog(mProgram)
                mProgram = -1
            }
        }

        return mProgram
    }

    private fun compileShaders() {
        isCompiled = true

        run loop@{
            programList.forEach() {
                val handle = loadShader(it.key, prependCode + readTextFromFile("/" + it.value))
                isCompiled = handle >= 0

                if (isCompiled)
                    handlesList.add(handle)
                else
                    return@loop
            }
        }

        programId = if (isCompiled) linkProgram(createProgram()) else -1

        isCompiled = programId > -1
    }

    private fun fetchAttributes() {
        val params: IntBuffer = BufferUtils.createIntBuffer(1)
        val type: IntBuffer = BufferUtils.createIntBuffer(1)

        params.clear()
        glGetProgramiv(programId, GL20.GL_ACTIVE_ATTRIBUTES, params)
        val numAttributes = params[0]
        attributeNames = arrayOfNulls(numAttributes)
        for (i in 0 until numAttributes) {
            params.clear()
            params.put(0, 1)
            type.clear()
            val name = glGetActiveAttrib(programId, i, params, type)
            attributeNames!![i] = name
        }
    }

    private fun fetchUniforms() {
        val params: IntBuffer = BufferUtils.createIntBuffer(1)
        val type: IntBuffer = BufferUtils.createIntBuffer(1)

        params.clear()
        glGetProgramiv(programId, GL20.GL_ACTIVE_UNIFORMS, params)
        val numUniforms: Int = params.get(0)
        for (i in 0 until numUniforms) {
            params.clear()
            params.put(0, 1)
            type.clear()
            val name = glGetActiveUniform(programId, i, params, type)
            uniformTypes.put(name, type.get(0))
        }
    }

    inline fun begin() = glUseProgram(programId)
    inline fun end() = glUseProgram(0)

    fun dispose() {
            glUseProgram(0)

            handlesList.forEach {
                glDetachShader(programId, it)
                glDeleteShader(it)
            }

            glDeleteProgram(programId)
        }

}