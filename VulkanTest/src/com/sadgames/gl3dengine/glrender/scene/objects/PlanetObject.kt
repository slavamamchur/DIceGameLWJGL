package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.GLRenderConsts
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.PLANET_OBJECT
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData
import org.lwjgl.opengl.GL11.glDrawElements
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.ByteBuffer
import java.nio.ByteOrder

const val FPI = 3.141592653589793f

class PlanetObject(radius: Float, textureResName: String, scene: GLRendererInterface<SceneObjectsTreeItem>):
      SpherePrimitiveObject(textureResName, scene.getCachedShader(PLANET_OBJECT), 1f, COLLISION_OBJECT, radius, 2/*, Vector3f(0f, 0.5f, -0.5f)*/) {

    private val landScale: Float; get() = (2f*FPI * radius) / 40000f

    override fun createVertexesVBO() {
        val vertexes = vertexesArray
        val vertexData = ByteBuffer
                .allocateDirect(vertexes.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexes)

        vertexData.position(0)
        vertexVBO = VBOData(VBOData.ElementType.VERTEX, GLRenderConsts.VERTEX_SIZE, 0, 0, vertexData)
        vertexData.limit(0)
    }

    override fun createNormalsVBO() {}

    //todo: createFacesIBO()

    override fun render() { glDrawElements(GL20.GL_LINES, facesCount, GL20.GL_UNSIGNED_SHORT, NULL) } //todo: GL_TRIANGLES
}
