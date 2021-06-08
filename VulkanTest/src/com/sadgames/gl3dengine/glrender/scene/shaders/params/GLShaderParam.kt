package com.sadgames.gl3dengine.glrender.scene.shaders.params

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.*
import com.sadgames.sysutils.common.toArray
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL20.*
import javax.vecmath.Vector3f

open class GLShaderParam(protected val paramType: GLParamType, val paramName: String, programId: Int) {

    var value: Any? = null
        set(value) {
            field = value

            if (paramReference >= 0)
                when (paramType) {
                    FLOAT_ATTRIB_ARRAY_PARAM ->
                        if (value is VBOData)
                            setParamValue(value)
                        else
                            throw IllegalStateException("Unexpected value: $paramType")

                    FLOAT_UNIFORM_VECTOR_PARAM, FLOAT_UNIFORM_VECTOR4_PARAM, FLOAT_UNIFORM_MATRIX_PARAM ->
                        if (value is Vector3f)
                            setParamValue(value.toArray())
                        else
                            setParamValue(value as FloatArray)

                    FLOAT_UNIFORM_PARAM -> setParamValue(value as Float)

                    INTEGER_UNIFORM_PARAM -> setParamValue(value as Int)
                }
        }

    val paramReference: Int =
            if (paramType == FLOAT_ATTRIB_ARRAY_PARAM)
                glGetAttribLocation(programId, paramName)
            else
                glGetUniformLocation(programId, paramName)

    var size = 0; private set
    var stride = 0; private set
    var pos = 0; private set
    var vboPtr = 0; private set

    protected open fun internalLinkParamValue() {
        glBindBuffer(GL_ARRAY_BUFFER, vboPtr)
        glEnableVertexAttribArray(paramReference)
        glVertexAttribPointer(paramReference, size, GL_FLOAT, false, stride, pos.toLong())
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    @Throws(IllegalAccessException::class)
    private fun setParamValue(value: VBOData) {
        require(paramType == FLOAT_ATTRIB_ARRAY_PARAM)

        this.size = value.size
        this.stride = value.stride
        this.pos = value.pos
        this.vboPtr = value.vboPtr

        internalLinkParamValue()
    }

    private fun setParamValue(data: FloatArray) {
        if (paramType == FLOAT_UNIFORM_VECTOR_PARAM && data.size >= 3)
            glUniform3fv(paramReference, data)
        else if (paramType == FLOAT_UNIFORM_VECTOR4_PARAM && data.size >= 4)
            glUniform4fv(paramReference, data)
        else if (paramType == FLOAT_UNIFORM_MATRIX_PARAM && data.size == 16)
            glUniformMatrix4fv(paramReference, false, data)
        else
            throw IllegalArgumentException()
    }

    private fun setParamValue(data: Int) {
        if (paramType == INTEGER_UNIFORM_PARAM)
            glUniform1i(paramReference, data)
        else
            throw IllegalArgumentException()
    }

    private fun setParamValue(data: Float) {
        if (paramType == FLOAT_UNIFORM_PARAM && paramReference >= 0)
            glUniform1f(paramReference, data)
        //else
            //throw IllegalArgumentException()
    }

}