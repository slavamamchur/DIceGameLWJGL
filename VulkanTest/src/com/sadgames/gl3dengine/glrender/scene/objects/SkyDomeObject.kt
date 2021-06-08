package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType
import com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData.ElementType
import org.lwjgl.opengl.GL11.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SkyDomeObject(cubeTexture: AbstractTexture?, glScene: GLRendererInterface<SceneObjectsTreeItem>):
        AbstractSkyObject(cubeTexture, glScene.getCachedShader(GLObjectType.SKY_DOME_OBJECT)) {

    override fun createSkyPrimitive(halfSize: Float) =
        SpherePrimitiveObject(null, program, 1f, COLLISION_OBJECT, halfSize)

    override fun createVertexesVBO() {
        val vertexes = vertexesArray

        val vertexData = ByteBuffer
                .allocateDirect(vertexes.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexes)

        vertexData.position(0)
        vertexVBO = VBOData(ElementType.VERTEX, VERTEX_SIZE, 0, 0, vertexData)
        vertexData.limit(0)
    }

    override fun createNormalsVBO() {}

    override fun render() {
        glCullFace(GL_FRONT)
        glDrawElements(GL_TRIANGLES, facesCount, GL_UNSIGNED_SHORT, 0)
        glCullFace(GL_BACK)
    }
}