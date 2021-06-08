package com.sadgames.gl3dengine.glrender.scene.shaders.params

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL33.glVertexAttribDivisor

class GLInstancedShaderParam(paramName: String, programId: Int):
        GLShaderParam(FLOAT_ATTRIB_ARRAY_PARAM, paramName, programId) {

    override fun internalLinkParamValue() {
        glBindBuffer(GL_ARRAY_BUFFER, vboPtr)
        glEnableVertexAttribArray(paramReference)
        glVertexAttribPointer(paramReference, size, GL_FLOAT, false, stride, pos.toLong())
        glVertexAttribDivisor(paramReference, 1)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

}