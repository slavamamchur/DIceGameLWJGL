package com.sadgames.vulkan

import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.sadgames.gl3dengine.gamelogic.client.GameConst
import com.sadgames.gl3dengine.gamelogic.client.GameConst.SKY_BOX_CUBE_MAP_OBJECT
import com.sadgames.gl3dengine.gamelogic.client.entities.GameMap
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Z
import com.sadgames.gl3dengine.glrender.scene.camera.FixedIsometricCamera
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera
import com.sadgames.gl3dengine.glrender.scene.fbo.AbstractFBO
import com.sadgames.gl3dengine.glrender.scene.fbo.ColorBufferFBO
import com.sadgames.gl3dengine.glrender.scene.fbo.DepthBufferFBO
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.objects.*
import com.sadgames.gl3dengine.glrender.scene.objects.generated.ForestGenerator
import com.sadgames.gl3dengine.glrender.scene.shaders.*
import com.sadgames.gl3dengine.manager.TextureCache
import com.sadgames.gl3dengine.physics.PhysicalWorld
import com.sadgames.sysutils.common.ColorUtils.argb
import com.sadgames.sysutils.common.toArray
import org.luaj.vm2.Globals
import org.lwjgl.opengl.GL20.*
import java.awt.Color
import java.util.*
import javax.vecmath.Color4f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f


class NGL4xRenderer: SceneObjectsTreeItem(), GLRendererInterface<SceneObjectsTreeItem> {
    private val shaders: MutableMap<GLObjectType, VBOShaderProgram> = EnumMap(GLObjectType::class.java)

             var renderBuffer: AbstractFBO? = null
             var transiteFBO: AbstractFBO? = null
    lateinit var postEffectsScreen: GUI2DImageObject
    lateinit var skyDomeObject: AbstractSkyObject
    lateinit var sun: AbstractGL3DObject
    lateinit var terrain: GameMap
    lateinit var forest: ForestGenerator

    override var program: VBOShaderProgram? = null
    override val camera: GLCamera = FixedIsometricCamera(0f, 3f, 3f, 45f, 0f, 0f)
    override val lightSource: GLLightSource = GLLightSource(Vector3f(-2.2f, 1.2f, -3.2f).toArray(), Vector3f(1.0f, 1.0f, 0.8f), camera)
    //override val physicalWorldObject: DiscreteDynamicsWorld get() = PhysicalWorld.physicalWorld
    override var luaEngine: Globals? = null //todo: init
    override val scene; get() = this
    override var refractionMapFBO: AbstractFBO?  = null
    override var shadowMapFBO: AbstractFBO? =null
    override var backgroundTextureName: String? = argb(255, 0, 64, 255).toString()
    override var moveFactor = -1f
    override var frameTime: Long = 0
    //override val lockObject; get() = locker
    override var glExtensions = ""
    override var zoomCameraAnimation: GLAnimation? = null

    //companion object { val locker: Any = Any() }

    init {
        camera.rotateX(32.5f)
        camera.rotateY(-7.5f)
        lightSource.lightPosInModelSpace = floatArrayOf(-2.2f, 0.7f, -3.2f, 1f)
    }

    private fun drawObject2ColorBuffer(obj: AbstractGL3DObject) {
        if (program == null || program != obj.program) {
            program = obj.program
            program?.useProgram()
            program?.bindGlobalParams(this)
        }
        program?.bindLocals(this, obj, false, true)
        with(obj) {
            bindVBO()
            render()
            unbindTexture(0)
        }
    }

    private fun clearScreen() {
        glClearColor(0f, 0.0f, 0.0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    override fun getCachedShader(type: GLObjectType): VBOShaderProgram? = shaders[type]
    override fun getObject(name: String?) = getChild(name) as AbstractGL3DObject?
    override fun switchTo2DMode() {
        //TODO("Not yet implemented")
    }

    override fun restorePrevViewMode() {
        //TODO("Not yet implemented")
    }

    override fun onSurfaceCreated() {
        glEnable(GL_CULL_FACE)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glBlendEquation(GL_FUNC_ADD)

        shaders[GLObjectType.GUI_OBJECT] = GUIRendererProgram()
        shaders[GLObjectType.SKY_DOME_OBJECT] = SkyDomeProgram()
        shaders[GLObjectType.REFRACTION_MAP_OBJECT] = RefractionMapRenderProgram()
        shaders[GLObjectType.SUN_OBJECT] = SunRendererProgram()
        shaders[GLObjectType.GEN_TERRAIN_OBJECT] = TerrainRendererProgram()
        shaders[GLObjectType.FOREST_OBJECT] = ForestRenderer()

        TextureCache[GameConst.TERRAIN_ATLAS_TEXTURE_NAME]

        skyDomeObject = SkyDomeObject(TextureCache[argb(255, 0, 64, 255).toString()], this)
        skyDomeObject.itemName = SKY_BOX_CUBE_MAP_OBJECT
        skyDomeObject.loadObject()

        sun = SunObject(this, 1f)
        sun.loadObject()

        val game = GameEntity()
        game.mapId = "5a65e0c2066710df597d328a"
        terrain = GameMap(shaders[GLObjectType.GEN_TERRAIN_OBJECT]!!, game)
        terrain.loadObject()
        terrain.createRigidBody()
        physicalWorldObject?.addRigidBody(terrain._body)

        forest = ForestGenerator(
            null,
            "palm",
            shaders[GLObjectType.FOREST_OBJECT],
            100,
            0.65f
        )
        forest.initialScale = 0.033f
        forest.parent = terrain
        forest.loadObject()
        terrain.putChild(forest, "FOREST")
        val wind = GLAnimation(7f, ROTATE_BY_Z, 600)
        val rcnt: Short = 0
        wind.setRepeatCount(rcnt)
        wind.setRollback(true)
        forest.animation = wind

        postEffectsScreen = GUI2DImageObject(getCachedShader(GLObjectType.GUI_OBJECT),
                                             Vector4f(-1f, 1f, 1f, -1f), false)
        postEffectsScreen.loadObject()
        postEffectsScreen.glTexture = transiteFBO!!.fboTexture

        wind.startAnimation(forest, null)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        if (GdxExt.width >= 0 ) {
            camera.aspectRatio = width * 1f / height

            shadowMapFBO?.cleanUp()
            transiteFBO?.cleanUp()
            renderBuffer?.cleanUp()

            shadowMapFBO = DepthBufferFBO(width, height)
            renderBuffer = ColorBufferFBO(width, height, Color4f(0f, 0f, 0f, 0f), isMultiSampled = true)
            transiteFBO = ColorBufferFBO(width, height, Color4f(0f, 0f, 0f, 0f))
        }

        GdxExt.width = width
        GdxExt.height = height
    }

    override fun onDrawFrame() {
        glEnable(GL_MULTISAMPLE)
        glEnable(GL_DEPTH_TEST)

        shadowMapFBO?.bind()

        renderBuffer!!.bind()

        glCullFace(GL_BACK)

        if (forest.animation.isInProgress)
            forest.animation.animate(forest)

        drawObject2ColorBuffer(skyDomeObject)
        drawObject2ColorBuffer(sun)
        drawObject2ColorBuffer(terrain)
        drawObject2ColorBuffer(forest)

        glDisable(GL_DEPTH_TEST)

        renderBuffer!!.resolve2FBO(transiteFBO!!)

        glDisable(GL_MULTISAMPLE)

        drawObject2ColorBuffer(postEffectsScreen)
    }

    override fun onDispose() {
        postEffectsScreen.clearData()
        forest.clearData()
        terrain.clearData()
        sun.clearData()
        skyDomeObject.clearData()

        TextureCache.clearCache()

        shaders.forEach{ it.value.deleteProgram() }

        shadowMapFBO?.cleanUp()
        transiteFBO?.cleanUp()
        renderBuffer?.cleanUp()
    }

}