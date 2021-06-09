package com.sadgames.gl3dengine.glrender

import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation
import com.sadgames.gl3dengine.glrender.scene.camera.FixedIsometricCamera
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera
import com.sadgames.gl3dengine.glrender.scene.camera.Orthogonal2DCamera
import com.sadgames.gl3dengine.glrender.scene.fbo.AbstractFBO
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import com.sadgames.gl3dengine.physics.PhysicalWorld
import org.luaj.vm2.Globals
import java.awt.Color
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

private const val CAMERA_ZOOM_ANIMATION_DURATION: Long = 1000L

interface GLRendererInterface<T: SceneObjectsTreeItem> {
    val scene: T
    val physicalWorldObject: DiscreteDynamicsWorld? get() = PhysicalWorld.physicalWorld
    val camera: GLCamera?
    val lightSource: GLLightSource?
    var luaEngine: Globals?
    var program: VBOShaderProgram?
    var refractionMapFBO: AbstractFBO?
    var shadowMapFBO: AbstractFBO?
    var backgroundTextureName: String?
    var moveFactor: Float
    var frameTime: Long
    val lockObject; get() = lock
    var glExtensions: String
    var zoomCameraAnimation: GLAnimation?

    companion object {val lock = Any()}

    fun getCachedShader(type: GLObjectType): VBOShaderProgram?
    fun getCachedShaderLua(type: Int) = getCachedShader(GLObjectType.values()[type])
    fun getObject(name: String?): AbstractGL3DObject?
    fun getColor(r: Float, g: Float, b: Float, a: Float = 1f)  = Color(r, g, b, a)
    fun createCamIsometric(xPos: Float, yPos: Float, zPos: Float, pitch: Float, yaw: Float, roll: Float) = FixedIsometricCamera(xPos, yPos, zPos, pitch, yaw, roll)
    fun createCam2D(landSize: Float) = Orthogonal2DCamera(landSize)
    fun switchTo2DMode()
    fun restorePrevViewMode()

    fun createZoomCameraAnimation(zoomLevel: Float): GLAnimation? {
        val animation = GLAnimation(zoomLevel, CAMERA_ZOOM_ANIMATION_DURATION)
        animation.luaEngine = luaEngine

        return animation
    }

    fun createTranslateAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float, fromZ: Float, toZ: Float, duration: Long): GLAnimation? {
        val animation = GLAnimation(fromX, toX, fromY, toY, fromZ, toZ, duration)
        animation.luaEngine = luaEngine

        return animation
    }

    fun createRotateAnimation(rotationAngle: Float, rotationAxesMask: Short, animationDuration: Long): GLAnimation? {
        val animation = GLAnimation(rotationAngle, rotationAxesMask, animationDuration)
        animation.luaEngine = luaEngine

        return animation
    }

    fun createTransform() = Matrix4f()
    fun createVector3f(vx: Float, vy: Float, vz: Float) = Vector3f(vx, vy, vz)

    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
    fun onDispose()
}