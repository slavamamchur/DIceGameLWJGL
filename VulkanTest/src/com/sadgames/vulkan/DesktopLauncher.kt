package com.sadgames.vulkan

import com.sadgames.dicegame.desktop.DesktopRestApiWrapper
import com.sadgames.gl3dengine.gamelogic.client.GameLogic
import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
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
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL

object DesktopLauncher {
    private const val TEST_GAME_INSTANCE_ID = "5bb9b8ed674b7d1ff899ca75"

    private var window: Long = NULL;
    private var renderer: GLRendererInterface<SceneObjectsTreeItem>? = null

    private fun initWindow(isFullScreen: Boolean) {
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_SAMPLES, if (GDXPreferences.graphicsQualityLevel == GraphicsQuality.ULTRA) 8 else 0)

        val monitor = if (isFullScreen) glfwGetPrimaryMonitor() else NULL
        window = glfwCreateWindow(1920, 1080, "Dice game!", monitor, NULL)
        if (window == NULL)
            throw RuntimeException("Failed to create the GLFW window")

        if (!isFullScreen) {
            stackPush().use { stack ->
                val pWidth = stack.mallocInt(1) // int*
                val pHeight = stack.mallocInt(1) // int*

                glfwGetWindowSize(window, pWidth, pHeight)
                val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

                // Center the window
                glfwSetWindowPos(
                    window,
                    (vidmode!!.width() - pWidth[0]) / 2,
                    (vidmode.height() - pHeight[0]) / 2
                )
            }
        }

        glfwMakeContextCurrent(window)
        glfwSwapInterval(0)
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        glfwShowWindow(window)
    }

    private fun initCallBacks() {
        glfwSetKeyCallback(window) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true)
            /*else if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
                val testSound1 = GdxExt.audio.newSound(CommonUtils.getResourceStream("/sounds/rolling_dice.mp3"))
                testSound1.play()
            }*/
        }

        glfwSetWindowSizeCallback(window, object : GLFWWindowSizeCallback() {
            override fun invoke(window: Long, width: Int, height: Int) {
                if (GdxExt.width != width || GdxExt.height != height)
                    renderer!!.onSurfaceChanged(width, height)
            }
        })

        val gestureListener = MyGestureListener(renderer!!)
        glfwSetScrollCallback(window, MouseScrollCallBack(gestureListener))
        glfwSetCursorPosCallback(window, MouseMoveCallBack(gestureListener))
        glfwSetMouseButtonCallback(window, MouseButtonCallBack(gestureListener))
    }

    private fun init() {
        System.out.println("Hello LWJGL " + Version.getVersion().toString() + "!")
        GLFWErrorCallback.createPrint(System.err).set()

        check(glfwInit()) { "Unable to initialize GLFW" }

        initWindow(true)

        GL.createCapabilities()
        renderer = /*NGL4xRenderer()*/ //todo: check pathMap
        produceRenderByType(RenderType.GL4_RENDER,
            GameLogic(TEST_GAME_INSTANCE_ID, DesktopRestApiWrapper)
        )

        initCallBacks()

        GdxExt.audio = OpenALLwjglAudio()

        glfwPollEvents()
    }

    private fun renderLoop() {
        var currentTime = System.currentTimeMillis()
        while (!glfwWindowShouldClose(window)) {
            renderer!!.onDrawFrame()

            glfwSwapBuffers(window)
            glfwPollEvents()

            val newTime = System.currentTimeMillis()
            renderer!!.frameTime = newTime - currentTime
            currentTime = newTime
        }
    }

    fun runGame() {
        init()

        renderer!!.onSurfaceCreated()
        renderLoop()
        renderer!!.onDispose()

        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runGame()
    }

}