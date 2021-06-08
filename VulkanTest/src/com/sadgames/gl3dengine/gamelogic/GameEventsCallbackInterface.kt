package com.sadgames.gl3dengine.gamelogic

import com.bulletphysics.dynamics.DynamicsWorld
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.fbo.AbstractFBO
import com.sadgames.vulkan.newclass.Pixmap
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
//import com.sadgames.gl3dengine.glrender.scene.GLScene
//import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject todo: -> automatic set shader params
import org.luaj.vm2.LuaValue

interface GameEventsCallbackInterface {
    fun onStopMovingObject(gameObject: PNodeObject?)
    fun onRollingObjectStart(gameObject: PNodeObject?)
    fun onRollingObjectStop(gameObject: PNodeObject?)
    fun onInitGLCamera(camera: GLCamera?)
    fun onInitLightSource(lightSource: GLLightSource?)
    fun onInitPhysics(dynamicsWorld: DynamicsWorld?)
    fun onLoadSceneObjects(glSceneObject: GLRendererInterface<SceneObjectsTreeItem>)
    fun onPrepareMapTexture(textureBmp: Pixmap?)
    fun onCreateReflectionMap(reflectMap: AbstractFBO?, refractMap: AbstractFBO?)
    fun onBeforeDrawFrame(frametime: Long)
    fun onPlayerMakeTurn(delegate: GLAnimation.AnimationCallBack?)
    fun onPlayerContinueTurn()
    fun onPerformUserAction(action: String?, params: Array<LuaValue?>?)
}