package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLParamType.FLOAT_ATTRIB_ARRAY_PARAM
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractInstancedObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLInstancedShaderParam
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam
import com.sadgames.sysutils.common.Mat4x4
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import java.util.*

abstract class VBOShaderProgram {

    companion object {
        private   val lockObject = Any()
                  val BIAS = floatArrayOf(0.5f, 0.0f, 0.0f, 0.0f,
                                          0.0f, 0.5f, 0.0f, 0.0f,
                                          0.0f, 0.0f, 0.5f, 0.0f,
                                          0.5f, 0.5f, 0.5f, 1.0f)
    }

    var programId = 0; private set
    val params: MutableMap<String, GLShaderParam?> = HashMap()

    private val shaderProgram = getShaderProgram()

    init {
        if (shaderProgram.isCompiled) {
            programId = shaderProgram.programId
            createParams()
        } else {
            programId = 0
            throw RuntimeException(shaderProgram.log)
        }
    }

    protected abstract fun getVertexShaderResId(): String?
    protected abstract fun getFragmentShaderResId(): String?

    abstract fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>)
    abstract fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject)

    protected open fun getGeometryShaderResId(): String? = null

    protected open fun getShaderProgram() = MyShaderProgram(
                object: HashMap<Int, String>() {
                    init {
                        put(GL20.GL_VERTEX_SHADER, getVertexShaderResId()!!)
                        if (getGeometryShaderResId()?.isNotEmpty() == true)
                            put(GL_GEOMETRY_SHADER, getGeometryShaderResId()!!)
                        put(GL20.GL_FRAGMENT_SHADER, getFragmentShaderResId()!!)
                    }}
                )

    fun useProgram() = shaderProgram.begin()
    fun deleteProgram() = shaderProgram.dispose()
    inline fun paramByName(name: String): GLShaderParam? = params[name]

    private fun createParams() {
        params.clear()
        createAttributes()
        createUniforms()
    }

    protected open fun createAttributes() {
        shaderProgram.attributeNames?.forEach {
            params[it!!] = if(it == OFFSETS_PARAM_NAME)
                                GLInstancedShaderParam(it, programId)
                            else
                                GLShaderParam(FLOAT_ATTRIB_ARRAY_PARAM, it, programId)
        }
    }

    protected open fun createUniforms() {
        shaderProgram.uniformTypes.forEach {
            params[it.key] = GLShaderParam(GL_PARAM_TYPES[it.value]!!, it.key, programId)
        }
    }

    open fun linkVBOData(renderable: AbstractGL3DObject) {
        try {
            params[VERTEXES_PARAM_NAME]?.value = renderable.vertexVBO
            params[TEXELS_PARAM_NAME]?.value = renderable.texelVBO
            params[NORMALS_PARAM_NAME]?.value = renderable.normalVBO
            params[OFFSETS_PARAM_NAME]?.value = (renderable as? AbstractInstancedObject)?.offsetVBO
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    inline fun setMVPMatrixData(data: FloatArray) {
        params[MVP_MATRIX_PARAM_NAME]?.value = data
    }

    fun setMVMatrixData(data: FloatArray) {
        params[MV_MATRIX_PARAM_NAME]?.value = data
        params[MV_MATRIXF_PARAM_NAME]?.value = data
    }

    open fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        val mMVMatrix = Mat4x4(viewMatrix) * Mat4x4(renderable.modelMatrix)
        setMVMatrixData(mMVMatrix.value)
        setMVPMatrixData((Mat4x4(projectionMatrix) * mMVMatrix).value)
    }

    fun bindLocals(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject, useLightSourceView: Boolean, useMaterials: Boolean) {
        synchronized(lockObject) {
            bindMVPMatrix(renderable,
                          if (useLightSourceView) scene.lightSource!!.viewMatrix else scene.camera!!.viewMatrix,
                          if (useLightSourceView) scene.lightSource!!.projectionMatrix else scene.camera!!.projectionMatrix)
        }

        bindAdditionalParams(scene, renderable)

        if (useMaterials)
            renderable.bindMaterial(this)
    }

    protected open fun bindLightSourceMVP(renderable: AbstractGL3DObject, ls: GLLightSource?, hasDepthTextureExtension: Boolean) {
        params[LIGHT_MVP_MATRIX_PARAM_NAME]?.value =
            (Mat4x4(BIAS) * (Mat4x4(ls!!.projectionMatrix) * (Mat4x4(ls.viewMatrix) * Mat4x4(renderable.modelMatrix)))).value
    }
}