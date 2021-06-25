package com.sadgames.vulkan

import com.sadgames.dicegame.desktop.DesktopRestApiWrapper
import com.sadgames.gl3dengine.gamelogic.client.GameLogic
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GraphicsQuality
import com.sadgames.gl3dengine.glrender.GLRenderConsts.RenderType
import com.sadgames.gl3dengine.glrender.GLRendererFabric.produceRenderByType
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.input.MyGestureListener
import com.sadgames.gl3dengine.manager.GDXPreferences
import com.sadgames.vulkan.newclass.MouseButtonCallBack
import com.sadgames.vulkan.newclass.MouseMoveCallBack
import com.sadgames.vulkan.newclass.MouseScrollCallBack
import com.sadgames.vulkan.newclass.audio.OpenALLwjglAudio
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import imgui.ImGui
import imgui.WindowFlag
import imgui.classes.Context
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import org.lwjgl.Version
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.Platform
import uno.glfw.GlfwMonitor
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw
import uno.glfw.windowHint.Profile.core

object DesktopLauncher {
    private const val TEST_GAME_INSTANCE_ID = "5bb9b8ed674b7d1ff899ca75"

    private var currentTime = System.currentTimeMillis()
    private lateinit var glfwWindow: GlfwWindow
    private lateinit var ctx: Context
    private lateinit var implGlfw: ImplGlfw
    private lateinit var implGl3: ImplGL3
    private lateinit var renderer: GLRendererInterface<SceneObjectsTreeItem>

    private fun initWindow(isFullScreen: Boolean) {
        glfwDefaultWindowHints()
        glfw.windowHint {
            visible = false
            //resizable = true

            context.version = if (Platform.get() == Platform.MACOSX) "3.2" else "4.1"
            profile = core

            samples = if (GDXPreferences.graphicsQualityLevel == GraphicsQuality.ULTRA) 8 else 0
        }

        val monitor = GlfwMonitor(if (isFullScreen) glfwGetPrimaryMonitor() else NULL)
        val currentMode = monitor.videoMode.size
        glfwWindow = GlfwWindow(currentMode.x, currentMode.y, "Dice game!", monitor)

        if (!isFullScreen)
            glfwWindow.pos = Vec2i((currentMode.x - glfwWindow.size.x) / 2,
                                   (currentMode.y - glfwWindow.size.y) / 2)

        glfwWindow.makeContextCurrent()
        glfw.swapInterval = VSync.OFF
        //todo: glfwWindow.cursorMode = GlfwWindow.CursorMode.disabled
        glfwWindow.show()
    }

    private fun initGUI() {
        ctx = Context()
        ImGui.styleColorsDark()
        //ImGui.styleColorsClassic()

        // Setup Platform/Renderer backend
        implGlfw = ImplGlfw(glfwWindow, false)
        implGl3 = ImplGL3()
    }

    private fun initCallBacks() {
        glfwSetKeyCallback(glfwWindow.handle.value) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true)
            /*else if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
                val testSound1 = GdxExt.audio.newSound(CommonUtils.getResourceStream("/sounds/rolling_dice.mp3"))
                testSound1.play()
            }*/
        }

        glfwSetWindowSizeCallback(glfwWindow.handle.value, object : GLFWWindowSizeCallback() {
            override fun invoke(window: Long, width: Int, height: Int) {
                if (GdxExt.width != width || GdxExt.height != height)
                    renderer.onSurfaceChanged(width, height)
            }
        })

        val gestureListener = MyGestureListener(renderer)
        glfwSetScrollCallback(glfwWindow.handle.value, MouseScrollCallBack(gestureListener))
        glfwSetCursorPosCallback(glfwWindow.handle.value, MouseMoveCallBack(gestureListener))
        glfwSetMouseButtonCallback(glfwWindow.handle.value, MouseButtonCallBack(gestureListener))
    }

    private fun initGame() {
        System.out.println("Hello LWJGL " + Version.getVersion().toString() + "!")
        //GLFWErrorCallback.createPrint(System.err).set()

        //check(glfwInit()) { "Unable to initialize GLFW" }

        glfw {
            errorCallback = { error, description -> println("Glfw Error $error: $description") }
            init()
        }

        initWindow(true)

        GL.createCapabilities()

        initGUI()

        renderer = produceRenderByType(RenderType.GL41_RENDER, GameLogic(TEST_GAME_INSTANCE_ID, DesktopRestApiWrapper))!!

        initCallBacks()

        GdxExt.audio = OpenALLwjglAudio()

        glfwPollEvents()
    }

    private fun renderLoop(stack: MemoryStack) {
        //currentTime = System.currentTimeMillis()
        //while (!glfwWindowShouldClose(window)) {
        implGlfw.newFrame()

        ImGui.run {
            newFrame()

            run {
                begin("Debug info", booleanArrayOf(true), WindowFlag.NoMove or WindowFlag.NoResize)

                //todo: move out from loop to renderer ???
                val res = GlfwMonitor(glfwGetPrimaryMonitor()).videoMode.size
                setWindowPos(Vec2(res.x - windowSize.x, 0f))
                setWindowFontScale(1.5f)

                text("Platform: %s",Platform.get().getName())
                text("Runtime env: %s v%s", System.getProperty("java.vm.name"), System.getProperty("java.version"))
                text("Renderer: %s", "OpenGL 4.1")
                text("Resolution: %dx%d", res.x, res.y)
                text("Frame time: %.3f ms (%.1f FPS)", 1_000f / io.framerate, io.framerate)
                //text("Frame time average: %.3f ms/frame (%.1f FPS)", 1f * renderer.frameTime, 1000f / renderer.frameTime)

                end()
            }
        }
        ImGui.render()

        renderer.onDrawFrame()

        implGl3.renderDrawData(ImGui.drawData!!)

            //glfwSwapBuffers(window)
            //glfwPollEvents()
            val newTime = System.currentTimeMillis()
            renderer.frameTime = newTime - currentTime
            currentTime = newTime
        //}
    }

    private fun runGame() {
        implGl3.newFrame()

        renderer.onSurfaceCreated()

        //renderLoop()
        currentTime = System.currentTimeMillis()
        glfwWindow.loop(::renderLoop)

        renderer.onDispose()

        implGl3.shutdown()
        implGlfw.shutdown()
        ctx.destroy()

        glfwWindow.destroy()
        glfw.terminate()
    }

    init { initGame() }

    @JvmStatic fun main(args: Array<String>) { runGame() }
}