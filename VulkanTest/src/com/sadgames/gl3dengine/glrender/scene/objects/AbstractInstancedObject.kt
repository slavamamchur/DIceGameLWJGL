package com.sadgames.gl3dengine.glrender.scene.objects

import org.lwjgl.opengl.GL30.*

import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData.ElementType
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import com.sadgames.sysutils.common.Mat4x4
import org.lwjgl.opengl.GL31.glDrawElementsInstanced
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.vecmath.Vector4f

abstract class AbstractInstancedObject(objFileName: String,
                                       program: VBOShaderProgram?,
                                       protected val itemCount: Int): Blender3DObject(objFileName, program, 0f, 1) {

    private var vertexInstances: FloatArray? = null
    private var normalInstances: FloatArray? = null
    private var offsetInstances: FloatArray? = null
    private var uvInstances: FloatArray? = null
    private var faceInstances: ShortArray? = null

    protected val modelWidth: Float
        get() {
            var maxX = Float.MIN_VALUE
            var minX = Float.MAX_VALUE
            val vertexes = raw3DModel.vertexes
            val count = vertexes.size

            var i = 0
            while (i < count) {
                maxX = if (vertexes[i] > maxX) vertexes[i] else maxX
                minX = if (vertexes[i] < minX) vertexes[i] else minX
                i += 3
            }

            return maxX - minX
        }

    var offsetVBO: VBOData? = null; private set

    protected abstract fun getNextTransform(number: Int): Vector4f

    private fun generate(raw3DModel: Raw3DModel): Raw3DModel {
        val numElements = itemCount * raw3DModel.vertexes.size
        val numFaces = itemCount * raw3DModel.faces.size
        val numUV = itemCount * raw3DModel.textureCoords.size

        vertexInstances = FloatArray(numElements)
        offsetInstances = FloatArray(numElements / 3 * 4)
        normalInstances = FloatArray(numElements)
        uvInstances = FloatArray(numUV)
        faceInstances = ShortArray(numFaces)

        for (numberOfInstances in 0 until itemCount) {
            putInstanceData(vertexInstances, raw3DModel.vertexes, numberOfInstances)
            putInstanceData(normalInstances, raw3DModel.normals, numberOfInstances)
            putInstanceData(uvInstances, raw3DModel.textureCoords, numberOfInstances)
            putInstanceData(offsetInstances, createOffsets(numElements / itemCount / 3 * 4, getNextTransform(numberOfInstances)), numberOfInstances)
            putInstanceData(faceInstances, transformFaces(raw3DModel.faces, numberOfInstances, numElements / itemCount), numberOfInstances)
        }

        return raw3DModel
    }

    private fun generateInstanced(raw3DModel: Raw3DModel): Raw3DModel {
        offsetInstances = FloatArray(itemCount * 4)
        vertexInstances = raw3DModel.vertexes
        normalInstances = raw3DModel.normals
        uvInstances = raw3DModel.textureCoords
        faceInstances = raw3DModel.faces

        for (numberOfInstances in 0 until itemCount)
            putInstanceData(offsetInstances, createOffsets(4, getNextTransform(numberOfInstances)), numberOfInstances)

        return raw3DModel
    }

    override fun getFacesCount() = super.getFacesCount() * itemCount
    override fun getVertexesArray() = vertexInstances
    override fun getNormalsArray() = normalInstances
    override fun getFacesArray() = faceInstances
    override fun getTextureCoordsArray() = uvInstances

    override fun getRaw3DModel(): Raw3DModel {
        raw3DModel = super.getRaw3DModel()
        val result = generateInstanced(raw3DModel)
        Mat4x4(modelMatrix)()

        return result
    }

    private fun createOffsetsVBO() {
        val vertexData = ByteBuffer
                .allocateDirect(offsetInstances!!.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(offsetInstances)

        vertexData.position(0)
        offsetVBO = VBOData(ElementType.VERTEX, 4, 0, 0, vertexData)
        vertexData.limit(0)
    }

    override fun loadObject() {
        glBindVertexArray(objectVAO)

        createVertexesVBO()
        createTexelsVBO()
        createNormalsVBO()
        createOffsetsVBO()
        createFacesIBO()

        program.params[VERTEXES_PARAM_NAME]?.value = vertexVBO
        program.params[TEXELS_PARAM_NAME]?.value = texelVBO
        program.params[NORMALS_PARAM_NAME]?.value = normalVBO
        program.params[OFFSETS_PARAM_NAME]?.value = offsetVBO
        if (facesIBO != null) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, facesIBO.vboPtr)
            glBindVertexArray(0)
        }

        glTexture = loadTexture()

        vertexInstances = null
        offsetInstances = null
        normalInstances = null
        uvInstances = null
        faceInstances = null
    }

    override fun render() {
        if (!settingsManager.isIn_2D_Mode)
            glDrawElementsInstanced(GL_TRIANGLES, getFacesCount(), GL_UNSIGNED_SHORT, 0, itemCount)
    }

    override fun clearData(): Void? {
        clearVBOPtr(offsetVBO)
        super.clearData()

        return null
    }

}

fun transformFaces(data: ShortArray, index: Int, size: Int): ShortArray =
        if (index == 0)
            data
        else {
            val count = data.size
            val transformed = ShortArray(count)

            for (i in 0 until count)
                transformed[i] = (data[i] + index * size / 3).toShort()

            transformed
        }

fun createOffsets(count: Int, transform: Vector4f): FloatArray {
    val offsets = FloatArray(count)

    var i = 0
    while (i < count) {
        offsets[i] = transform.x
        offsets[i + 1] = transform.y
        offsets[i + 2] = transform.z
        offsets[i + 3] = transform.w

        i += 4
    }

    return offsets
}

inline fun putInstanceData(elements: FloatArray?, instance: FloatArray, index: Int) =
        System.arraycopy(instance, 0, elements!!, instance.size * index, instance.size)

inline fun putInstanceData(elements: ShortArray?, instance: ShortArray, index: Int) =
        System.arraycopy(instance, 0, elements!!, instance.size * index, instance.size)
