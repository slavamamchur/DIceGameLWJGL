package com.sadgames.gl3dengine.glrender.scene

/*import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table*/

import com.sadgames.gl3dengine.gamelogic.GameEventsCallbackInterface
import com.sadgames.gl3dengine.gamelogic.client.GameConst
import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera
import com.sadgames.gl3dengine.glrender.scene.camera.Orthogonal2DCamera
import com.sadgames.gl3dengine.glrender.scene.fbo.AbstractFBO
import com.sadgames.gl3dengine.glrender.scene.fbo.ColorBufferFBO
import com.sadgames.gl3dengine.glrender.scene.fbo.DepthBufferFBO
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.GUI2DImageObject
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.glrender.scene.postprocess.PostProcessStep
import com.sadgames.gl3dengine.glrender.scene.shaders.*
import com.sadgames.gl3dengine.manager.GDXPreferences
import com.sadgames.gl3dengine.manager.TextureCache
import com.sadgames.gl3dengine.physics.PhysicalWorld
import com.sadgames.gl3dengine.physics.PhysicalWorld.simulateStep
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import com.sadgames.vulkan.newclass.GLVersion
import com.sadgames.vulkan.newclass.GLVersion.ApplicationType
import org.luaj.vm2.Globals
import org.lwjgl.opengl.GL13.GL_MULTISAMPLE
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.glUseProgram
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengles.EXTClipCullDistance
import java.util.*
import javax.vecmath.Color4f
import javax.vecmath.Vector4f
import kotlin.math.roundToInt

open class GLScene(private val gameEventsCallBackListener: GameEventsCallbackInterface?): SceneObjectsTreeItem(), GLRendererInterface<SceneObjectsTreeItem> {

    //companion object { val lockObj = Any() }

    private var savedCamera: GLCamera? = null
    private var firstRun = true
    private val graphicsQualityLevel = GDXPreferences.graphicsQualityLevel
    private val shaders: MutableMap<GLObjectType, VBOShaderProgram> = EnumMap(GLObjectType::class.java)
    private var postEffects2DScreen: GUI2DImageObject? = null
    private var isSimulating = false
    //todo: private var fpsCounter: Label? = null
    //todo: private lateinit var table: Table
    private var spentTime: Long = 0
    private var isRenderStopped = false
    private var prevObject: AbstractGL3DObject? = null
    private var old2dModeValue = false
    //todo: private lateinit var stage: Stage

    override var moveFactor = 0f
    override var frameTime: Long = 0
    override var backgroundTextureName: String? = null
    override var shadowMapFBO: AbstractFBO? = null
    override var refractionMapFBO: AbstractFBO? = null
    override var program: VBOShaderProgram? = null
    override var lightSource: GLLightSource? = null;
    override val scene: GLScene get() = this
    //override val physicalWorldObject: DiscreteDynamicsWorld? get() = PhysicalWorld.physicalWorld
    override var luaEngine: Globals? = null
    override var glExtensions = ""

    override var camera: GLCamera? = null
        set(value) {
            val oldCam = field
            field = value ?: savedCamera!!
            savedCamera = oldCam

            if (field!!.aspectRatio == -1f) field!!.aspectRatio = mDisplayWidth * 1f / mDisplayHeight

            lightSource?.mCamera = field!!
        }

    //override val lockObject; get() = lockObj

    var mDisplayWidth = 0; private set
    var mDisplayHeight = 0; private set
    var isSceneLoaded = false; set(value) { synchronized(lockObject) { field = value } }
    var mainRenderFBO: AbstractFBO? = null; private set
    var transiteFBO: AbstractFBO? = null; private set
    var transiteFBO2: AbstractFBO? = null; private set
    var hBlurFBO: AbstractFBO? = null; private set
    var reflectionMapFBO: AbstractFBO? = null; private set
    var raysMapFBO: AbstractFBO? = null; private set
    var raysEffectFBO: AbstractFBO? = null; private set
    var hasDepthTextureExtension = checkDepthTextureExtension(); private set
    override var zoomCameraAnimation: GLAnimation? = null

    private fun initScene() {
        camera = createCamIsometric(0f, 0.1f, 3f, 0f, 0f, 0f)
        lightSource = GLLightSource(floatArrayOf(DEFAULT_LIGHT_X, DEFAULT_LIGHT_Y, DEFAULT_LIGHT_Z, 1.0f),
                                    DEFAULT_LIGHT_COLOUR,
                                    camera!!)
        gameEventsCallBackListener?.onInitGLCamera(camera)
        camera?.updateViewMatrix()
        gameEventsCallBackListener?.onInitLightSource(lightSource)
    }

    private fun initPhysics() { val instance = PhysicalWorld }

    private fun scenePrepare() {
        //initScene()
        //initPhysics()

        //graphicsQualityLevel = settingsManager.graphicsQualityLevel
        glEnable(GL_MULTISAMPLE);
        glExtensions = extractGlExtensions(extractVersion())
        hasDepthTextureExtension = checkDepthTextureExtension()

        glEnable(GL20.GL_CULL_FACE)
        glEnable(GL20.GL_DEPTH_TEST)

        loadScene()

        //todo: GUI
        /*for (control in (gameEventsCallBackListener as GameLogic).gameControls) {
            table.add(control)
            if (control is Label) fpsCounter = control
        }*/

        startSimulation()
    }

    override fun getCachedShader(type: GLObjectType): VBOShaderProgram? {
        var program = shaders[type]

        return if (program == null) {
            program = when (type) {
                GLObjectType.TERRAIN_OBJECT_32 -> Gl32TerrainRenderer()
                GLObjectType.WATER_OBJECT -> WaterRendererProgram()
                GLObjectType.GEN_TERRAIN_OBJECT -> GenTerrainProgram()
                GLObjectType.SHADOW_MAP_OBJECT -> ShadowMapProgram()
                GLObjectType.GUI_OBJECT -> GUIRendererProgram()
                GLObjectType.SKY_BOX_OBJECT -> SkyBoxProgram()
                GLObjectType.SKY_DOME_OBJECT -> SkyDomeProgram()
                GLObjectType.SUN_OBJECT -> SunRendererProgram()
                GLObjectType.FLARE_OBJECT -> SunFlareProgram()
                GLObjectType.FOREST_OBJECT -> ForestRenderer()
                GLObjectType.REFLECTION_MAP_OBJECT -> ReflectionMapRenderProgram()
                GLObjectType.REFRACTION_MAP_OBJECT -> RefractionMapRenderProgram()
                GLObjectType.RAYS_MAP_OBJECT -> RaysMapProgram()
                GLObjectType.PLANET_OBJECT -> PlanetRendererProgram()
                else -> TerrainRendererProgram()
            }

            shaders[type] = program
            program
        }
        else
            program
    }

    override fun getObject(name: String?) = getChild(name) as AbstractGL3DObject?

    private fun createPostEffects2DScreen() {
        postEffects2DScreen = GUI2DImageObject(getCachedShader(GLObjectType.GUI_OBJECT), Vector4f(-1f, 1f, 1f, -1f), false)
        postEffects2DScreen!!.loadObject()
    }

    private fun loadScene() {
        isSceneLoaded = false
        getCachedShader(GLObjectType.SHADOW_MAP_OBJECT)
        createPostEffects2DScreen()
        gameEventsCallBackListener?.onLoadSceneObjects(this)
    }

    private fun startSimulation() { isSimulating = true }
    private fun stopSimulation() { isSimulating = false }

    private fun generateShadowMapFBO() {
        val shadowMapResolutionScaleFactor = SHADOW_MAP_RESOLUTION_SCALE[graphicsQualityLevel.ordinal]
        val shadowMapWidth = (mDisplayWidth * shadowMapResolutionScaleFactor).roundToInt()
        val shadowMapHeight = (mDisplayHeight * shadowMapResolutionScaleFactor).roundToInt()

        lightSource!!.updateViewProjectionMatrix(shadowMapWidth, shadowMapHeight)
        shadowMapFBO = if (hasDepthTextureExtension) DepthBufferFBO(shadowMapWidth, shadowMapHeight) else ColorBufferFBO(shadowMapWidth, shadowMapHeight, DEPTH_BUFFER_CLEAR_COLOR)
    }

    private fun generateMainRenderFBO() {
        mainRenderFBO = ColorBufferFBO(mDisplayWidth,
                mDisplayHeight,
                Color4f(0.0f, 0.0f, 0.0f, 0.0f),
                false,  2,
                true)
            transiteFBO = ColorBufferFBO(mDisplayWidth, mDisplayHeight,
                    Color4f(0.0f, 0.7f, 1.0f, 1.0f),
                    false)
            transiteFBO2 = ColorBufferFBO(mDisplayWidth, mDisplayHeight,
                    Color4f(0.0f, 0.7f, 1.0f, 1.0f),
                    false)
            hBlurFBO = ColorBufferFBO(mDisplayWidth / 4, mDisplayHeight / 4,
                    Color4f(0.0f, 0.0f, 0.0f, 0.0f),
                    false)
    }

    private fun generateReflectionMapFBO() {
        reflectionMapFBO = ColorBufferFBO((mDisplayWidth * 0.5f).roundToInt(),
                (mDisplayHeight * 0.5f).roundToInt(),
                Color4f(0.0f, 0.0f, 0.0f, 0.0f))
        refractionMapFBO = ColorBufferFBO(mDisplayWidth.toFloat().roundToInt(),
                mDisplayHeight.toFloat().roundToInt(),
                Color4f(0.0f, 0.0f, 0.0f, 0.0f),
                true,
                2)
        gameEventsCallBackListener?.onCreateReflectionMap(reflectionMapFBO, refractionMapFBO)
    }

    private fun generateRaysMapFBO() {
        raysMapFBO = null
    }

    private fun generateRaysEffectFBO() {
        raysEffectFBO = ColorBufferFBO((mDisplayWidth * 0.25f).roundToInt(),
                (mDisplayHeight * 0.25f).roundToInt(),
                Color4f(0.0f, 0.0f, 0.0f, 1.0f))
    }

    private fun updateViewPorts(width: Int, height: Int) {
        if (firstRun) {
            firstRun = false

            initScene() // todo: ???
        }

        if (mDisplayWidth > 0) {
            mDisplayWidth = width
            mDisplayHeight = height

            camera!!.aspectRatio = mDisplayWidth * 1f / mDisplayHeight

            if (settingsManager.isIn_2D_Mode) {
                camera!!.vfov = camera!!.vfov / 1.5f
                camera!!.zoomed_vfov = camera!!.vfov
            }

            generateShadowMapFBO()

            //todo: Implement deferred lighting algorithm

            generateReflectionMapFBO()
            generateRaysMapFBO()
            generateRaysEffectFBO()
            /** for post effects  */
            generateMainRenderFBO()
        }
        else {
            mDisplayWidth = width
            mDisplayHeight = height
        }
    }

    private fun simulatePhysics(currentTime: Long) {
        if (isSimulating)
            simulateStep(currentTime)
    }

    private fun calculateCameraPosition() {
        if (zoomCameraAnimation?.isInProgress == true) {
            zoomCameraAnimation!!.animate(camera)
        }
    }

    private fun calculateWavesMovingFactor() {
        try {
            //if (!isGlES30Supported()) {
            moveFactor += WAVE_SPEED * frameTime / 1000
            moveFactor %= 1f
            //}
            //else
            //moveFactor += 0.005;
        } catch (e: Exception) {
            moveFactor = 0f
        }
    }

    private fun calculateObjectsAnimations(sceneObject: SceneObjectsTreeItem?): Void? {
        val gl3DObject = sceneObject as AbstractGL3DObject
        val animation = gl3DObject.animation

        if (animation?.isInProgress == true)
            animation.animate(gl3DObject)
        else if (isSimulating && gl3DObject is PNodeObject
                 && gl3DObject.tag == PNodeObject.MOVING_OBJECT
                 && gl3DObject._body != null) {
            val transform = gl3DObject.worldTransformActual

            if (gl3DObject.worldTransformOld != transform)
                gl3DObject.setWorldTransformMatrix(transform)
            else {
                physicalWorldObject?.removeRigidBody(gl3DObject._body)
                gl3DObject._body = null
                gameEventsCallBackListener?.onStopMovingObject(gl3DObject)
            }
        }

        return null
    }

    private fun calculateSceneTransformations() {
        calculateCameraPosition()
        calculateWavesMovingFactor()

        proceesTreeItems({calculateObjectsAnimations(it)}) {true}
    }

    private fun clearRenderBuffers() {
        glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
        glClearColor(0.48f, 0.62f, 0.68f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    private fun renderItems(fbo: AbstractFBO?,
                            renderProgram: VBOShaderProgram?,
                            itemHandler: (item: SceneObjectsTreeItem?) -> Void?,
                            condition: (item: SceneObjectsTreeItem?) -> Boolean) {
        if (fbo != null)
            fbo.bind()
        else {
            glViewport(0, 0, mDisplayWidth, mDisplayHeight)
            clearRenderBuffers()
        }

        if (program !== renderProgram) {
            program = renderProgram
            program?.useProgram()
            program?.bindGlobalParams(this)
        }

        prevObject = null
        proceesTreeItems(itemHandler, condition)

        fbo?.unbind()
    }

    private fun renderPostEffectsBuffer(fbo: AbstractFBO?, steps: ArrayList<PostProcessStep>) {
        if (fbo != null)
            fbo.bind()
        else {
            glViewport(0, 0, mDisplayWidth, mDisplayHeight)
            clearRenderBuffers()
        }

        postEffects2DScreen!!.program.useProgram()
        postEffects2DScreen!!.bindVBO()

        for (step in steps) {
            postEffects2DScreen!!.glTexture = step.map
            postEffects2DScreen!!.glBlendingMap = step.blendMap
            postEffects2DScreen!!.effects = step.effects

            postEffects2DScreen!!.program.bindLocals(this, postEffects2DScreen!!, false, true)

            if (step.params != null)
                (postEffects2DScreen!!.program as GUIRendererProgram).setAdditionalParams(step.params!!)

            glBlendFunc(GL20.GL_SRC_ALPHA, step.blendFunc)
            postEffects2DScreen!!.render()
        }
    }

    private fun drawObjectIntoRaysMap(sceneObject: SceneObjectsTreeItem): Void? {
        val gl3DObject = sceneObject as AbstractGL3DObject
        program!!.bindLocals(this, gl3DObject, false, true)

        if (gl3DObject != prevObject) {
            gl3DObject.bindVBO(program)
            prevObject = gl3DObject
        }

        gl3DObject.render()
        return null
    }

    private fun drawObjectIntoRefractionMap(sceneObject: SceneObjectsTreeItem): Void? {
        val gl3DObject = sceneObject as AbstractGL3DObject
        val tmpTex = gl3DObject.glTexture
        gl3DObject.glTexture = TextureCache.getItem(GameConst.SAND_TEXTURE_NAME)

        program!!.bindLocals(this, gl3DObject, false, true)

        if (gl3DObject != prevObject) {
            gl3DObject.bindVBO()
            prevObject = gl3DObject
        }

        gl3DObject.render()
        gl3DObject.glTexture = tmpTex
        return null
    }

    private fun drawObjectIntoReflectionMap(sceneObject: SceneObjectsTreeItem, uniqProg: VBOShaderProgram, stdProg: VBOShaderProgram): Void? {
        val gl3DObject = sceneObject as AbstractGL3DObject
        val currentProg = if (gl3DObject.program === uniqProg) uniqProg else stdProg

        if (currentProg !== program) {
            program = currentProg
            program!!.useProgram()
        }

        if (program == null)
            return null

        program!!.bindLocals(this, gl3DObject, false, true)

        if (gl3DObject != prevObject) {
            gl3DObject.bindVBO()
            prevObject = gl3DObject
        }

        gl3DObject.render()
        return null
    }

    private fun drawObjectIntoShadowMap(sceneObject: SceneObjectsTreeItem?): Void? {
        program!!.bindLocals(this, sceneObject as AbstractGL3DObject, true, false)

        if (sceneObject != prevObject) {
            sceneObject.bindVBO(program)
            prevObject = sceneObject
        }

        sceneObject.render()
        return null
    }

    private fun drawObjectIntoColorBuffer(sceneObject: SceneObjectsTreeItem?): Void? {
        val gl3DObject = sceneObject as AbstractGL3DObject
        if (gl3DObject.program !== program) {
            program = gl3DObject.program
            program!!.useProgram()
            program!!.bindGlobalParams(this)
        }

        if (program == null)
            return null

        program!!.bindLocals(this, gl3DObject, false, true)
        if (gl3DObject != prevObject) {
            gl3DObject.bindVBO()
            prevObject = gl3DObject
        }

        gl3DObject.render()
        gl3DObject.unbindTexture(0)
        return null
    }

    override fun switchTo2DMode() {
        synchronized(lockObject) {
            old2dModeValue = settingsManager.isIn_2D_Mode
            settingsManager.isIn_2D_Mode = true
            camera = Orthogonal2DCamera(LAND_SIZE_IN_WORLD_SPACE)
        }
    }

    override fun restorePrevViewMode() {
        synchronized(lockObject) {
            settingsManager.isIn_2D_Mode = old2dModeValue
            camera = null
        }
    }

    private fun drawScene() {
        if (isRenderStopped) return

        gameEventsCallBackListener?.onBeforeDrawFrame(frameTime)

        simulatePhysics(System.currentTimeMillis())
        calculateSceneTransformations()

        /** Render ShadowMap  */
        glCullFace(GL_FRONT)
        renderItems(shadowMapFBO,
                    getCachedShader(GLObjectType.SHADOW_MAP_OBJECT)!!, {drawObjectIntoShadowMap(it)},
                    {condition -> (condition as AbstractGL3DObject).isCastShadow})
        glCullFace(GL_BACK)
        glEnable(GL20.GL_BLEND)
        glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        /** Render reflection and refraction maps  */
        if (reflectionMapFBO != null && !settingsManager.isIn_2D_Mode) {
            val isClippingPlanesSupported = false //todo: glExtensions().contains(GL_EXT_clip_cull_distance);
            if (isClippingPlanesSupported) glEnable(EXTClipCullDistance.GL_CLIP_DISTANCE0_EXT)
            camera!!.flipVertical()
            val uniqProg = getCachedShader(GLObjectType.SKY_DOME_OBJECT)
            uniqProg!!.useProgram()
            uniqProg.bindGlobalParams(this)
            val stdProg = getCachedShader(GLObjectType.REFLECTION_MAP_OBJECT)
            stdProg!!.useProgram()
            stdProg.bindGlobalParams(this)

            renderItems(reflectionMapFBO,
                    null,
                    { item: SceneObjectsTreeItem? -> drawObjectIntoReflectionMap(item!!, uniqProg, stdProg) },
                    { condition -> (condition as AbstractGL3DObject).isReflected })

            camera!!.flipVertical()

            /** Render ray map  */
            renderItems(refractionMapFBO, //todo: error in ray map???
                        getCachedShader(GLObjectType.REFRACTION_MAP_OBJECT)!!,
                        { sceneObject: SceneObjectsTreeItem? -> drawObjectIntoRefractionMap(sceneObject!!) },
                        { condition ->
                            (condition === getObject(GameConst.TERRAIN_MESH_OBJECT)
                             || (condition as AbstractGL3DObject).isDrawInRaysBuffer)
                        })

            if (isClippingPlanesSupported)
                glDisable(EXTClipCullDistance.GL_CLIP_DISTANCE0_EXT)
        }

        /** render colorBuffer  */
        renderItems(mainRenderFBO, null,
                    { sceneObject: SceneObjectsTreeItem? -> drawObjectIntoColorBuffer(sceneObject!!) }, { true })

        mainRenderFBO!!.activeTexture = 1
        mainRenderFBO!!.resolve2FBO(transiteFBO!!)
        refractionMapFBO!!.activeTexture = 1

        /** for post effects image processing  */
        glDisable(GL20.GL_DEPTH_TEST)
        val steps = ArrayList<PostProcessStep>()
        if (!settingsManager.isIn_2D_Mode) {
            steps.add(PostProcessStep(raysMapFBO!!.fboTexture!!,
                             null,
                                      GameConst.GOD_RAYS_POST_EFFECT,
                                      object : HashMap<String, Any>() {
                                        init {
                                            put(LIGHT_POSITION_PARAM_NAME, lightSource!!.position2D)
                                        }
                                      },
                                      GL20.GL_ONE))

            renderPostEffectsBuffer(raysEffectFBO, steps)
            refractionMapFBO!!.activeTexture = 0

            steps.clear()
            steps.add(PostProcessStep(transiteFBO!!.fboTexture!!, null,
                       GameConst.BLUR_EFFECT or GameConst.CONTRAST_CHARGE_EFFECT,
                        object : HashMap<String, Any>() {
                            init {
                                put(CONTRAST_LEVEL_PARAM_NAME, 1.3f)
                            }
                        }))

            renderPostEffectsBuffer(hBlurFBO, steps)
        }

        mainRenderFBO!!.activeTexture = 0
        mainRenderFBO!!.resolve2FBO(transiteFBO!!)

        val extEffects = !settingsManager.isIn_2D_Mode
        steps.clear()

        if (!extEffects) {
            steps.add(PostProcessStep(transiteFBO!!.fboTexture!!, null, GameConst.NO_POST_EFFECTS))
            renderPostEffectsBuffer(null, steps)
        }
        else {
            steps.add(PostProcessStep(transiteFBO!!.fboTexture!!, hBlurFBO!!.fboTexture, GameConst.BLOOM_EFFECT))
            steps.add(PostProcessStep(raysEffectFBO!!.fboTexture!!,null, GameConst.NO_POST_EFFECTS,null,
                                      GL20.GL_ONE))

            renderPostEffectsBuffer(null, steps)

            //todo: implement correctly
            /*steps.clear()
            steps.add(PostProcessStep(transiteFBO2!!.fboTexture!!,
                    null,
                    GameConst.DOF_EFFECT,
                    object : HashMap<String, Any>() {
                        init {
                            put(ACTIVE_DEPTHMAP_SLOT_PARAM_NAME,
                                    (scene.refractionMapFBO as? ColorBufferFBO)?.depthTexture?.bind(ROAD_TILE_TEXTURE_SLOT + 2)
                                            ?: -1
                            )
                        }
                    } ))

            renderPostEffectsBuffer(null, steps)*/
        }

        steps.clear()

        /** fix for 2D-UI bug */
        glBindVertexArray(0)
        glDisable(GL20.GL_BLEND)
    }

    fun cleanUp() {
        stopSimulation()
        clearData()
    }

    private fun clearData() {
        clearFBOs()
        clearObjectsCache()
        TextureCache.clearCache()
        clearShaderCache()
    }

    private fun clearFBOs() {
        shadowMapFBO?.cleanUp()
        reflectionMapFBO?.cleanUp()
        refractionMapFBO?.cleanUp()
        raysMapFBO?.cleanUp()
        raysEffectFBO?.cleanUp()
        mainRenderFBO?.cleanUp()
        transiteFBO?.cleanUp()
        transiteFBO2?.cleanUp()
        hBlurFBO?.cleanUp()
    }

    private fun clearObjectsCache() {
        proceesTreeItems({ item: SceneObjectsTreeItem? -> (item as AbstractGL3DObject?)!!.clearData() }) { true }
        childs.clear()
        postEffects2DScreen?.clearData()
    }

    private fun clearShaderCache() {
        glUseProgram(0)
        for (program in shaders.values)
            program.deleteProgram()
        shaders.clear()
    }

    /*fun createZoomCameraAnimation(zoomLevel: Float): GLAnimation? {
        val animation = GLAnimation(zoomLevel, CAMERA_ZOOM_ANIMATION_DURATION)
        animation.luaEngine = luaEngine

        return animation
    }*/

    /*fun createTranslateAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float, fromZ: Float, toZ: Float, duration: Long): GLAnimation? {
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
    fun createVector3f(vx: Float, vy: Float, vz: Float) = Vector3f(vx, vy, vz) */

    override fun onSurfaceCreated() { create() }
    override fun onSurfaceChanged(width: Int, height: Int) { resize(width, height) }
    override fun onDrawFrame() { render() }
    override fun onDispose() { dispose() }

    fun render() {
        spentTime += frameTime
        drawScene()

        //todo: implement GUI
        /*glDisable(GL20.GL_CULL_FACE)
        glDisable(GL20.GL_DEPTH_TEST)

        stage.act(Gdx.graphics.deltaTime)

        if (fpsCounter != null && spentTime >= 250) {
            spentTime = 0
            fpsCounter!!.setText(String.format(" fps: %d", if (frameTime == 0L) 0 else 1000 / frameTime))
        }

        stage.draw()

        glEnable(GL20.GL_CULL_FACE)
        glEnable(GL20.GL_DEPTH_TEST)*/
    }

    /*fun createCamIsometric(xPos: Float, yPos: Float, zPos: Float, pitch: Float, yaw: Float, roll: Float): GLCamera? {
        return FixedIsometricCamera(xPos, yPos, zPos, pitch, yaw, roll)
    }

    fun createCam2D(landSize: Float) = Orthogonal2DCamera(landSize)*/

    fun pause() {}
    fun resume() {}

    fun resize(width: Int, height: Int) {
        updateViewPorts(width, height)
        //todo: stage.viewport.update(width, height, true)
    }

    fun create() {
        scenePrepare()

        //todo: gui
        /*stage = Stage()
        (Gdx.input.inputProcessor as MyGestureDetector)._2dUiProcessor = stage

        table = Table()
        table.setFillParent(true)
        table.width = 30f
        table.top()

        stage.addActor(table)*/
    }

    fun dispose() {
        cleanUp()
        //todo: stage.dispose()
    }

}

private fun extractVersion(): GLVersion = GLVersion(ApplicationType.Desktop, glGetString(7938), glGetString(7936), glGetString(7937))

private fun extractGlExtensions(glVersion: GLVersion): String {
    var extensions = ""

    if (glVersion.isVersionEqualToOrHigher(3, 2)) {
        val numExtensions = IntArray(16)
        glGetIntegerv(33309, numExtensions)

        for (i in 0 until numExtensions[0] + 100)
            extensions += " " + glGetStringi(7939, i)
    }
    else
        extensions = glGetString(GL20.GL_EXTENSIONS) ?: ""

    //Array<String> sextensions = new Array();
    //sextensions.addAll(GdxExt.gl.glGetString(7939).split(" "));

    return extensions
}

fun checkDepthTextureExtension() = true //GLES20JniWrapper.glExtensions().contains(UNI_DEPTH_TEXTURE_EXTENSION);

//private const val CAMERA_ZOOM_ANIMATION_DURATION: Long = 1000