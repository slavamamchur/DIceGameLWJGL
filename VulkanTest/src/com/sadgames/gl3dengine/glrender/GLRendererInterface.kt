package com.sadgames.gl3dengine.glrender

import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera
import com.sadgames.gl3dengine.glrender.scene.fbo.AbstractFBO
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import org.luaj.vm2.Globals

interface GLRendererInterface<T: SceneObjectsTreeItem> {
    val scene: T
    val physicalWorldObject: DiscreteDynamicsWorld?
    val camera: GLCamera?
    val lightSource: GLLightSource?
    var luaEngine: Globals?
    var program: VBOShaderProgram?
    var refractionMapFBO: AbstractFBO?
    var shadowMapFBO: AbstractFBO?
    var backgroundTextureName: String?
    var moveFactor: Float
    var frameTime: Long
    val lockObject: Any
    var glExtensions: String

    fun getCachedShader(type: GLObjectType): VBOShaderProgram?
    fun getObject(name: String?): AbstractGL3DObject?

    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
    fun onDispose()
}