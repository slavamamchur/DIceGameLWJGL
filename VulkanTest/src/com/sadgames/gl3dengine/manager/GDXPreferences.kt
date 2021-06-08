package com.sadgames.gl3dengine.manager

import com.sadgames.gl3dengine.glrender.GLRenderConsts
import java.io.File
import java.io.FileOutputStream
import java.util.*


object GDXPreferences: SettingsManagerInterface {

    private const val CONFIG_FILE_NAME = "Prefs.cfg"
    private const val PARAM_AUTH_TOKEN = "authToken"
    private const val PARAM_USER_NAME = "userName"
    private const val PARAM_USER_PASS = "userPass"
    private const val BASE_URL_NAME = "baseUrl"
    private const val PARAM_STAY_LOGGED_IN = "stayLoggedIn"
    private const val PARAM_IN_2D_MODE = "in2DMode"
    private const val PARAM_GRAPHICS_QUALITY_LEVEL = "graphicsQualityLevel"
    private const val PARAM_VIEWPORT_WIDTH = "viewportWidth"
    private const val PARAM_VIEWPORT_HEIGHT = "viewportHeight"
    private const val PARAM_VSYNC = "vSync"
    private const val PARAM_FULLSCREEN = "fullScreen"
    private const val PARAM_FOREGROUND_FPS_LIMIT = "foregroundFPSLimit"

    private val lockObject = Any()
    private val preferences = Properties();

    override var authToken; get() = safeReadProperty(PARAM_AUTH_TOKEN, ""); set(value) = safeWriteProperty(PARAM_AUTH_TOKEN, value!!)
    override val isLoggedIn; get() = (safeReadProperty(PARAM_AUTH_TOKEN, "")?.length ?: 0) > 0
    override val isStayLoggedIn; get() = safeReadProperty(PARAM_STAY_LOGGED_IN, true)
    override var userName; get() = safeReadProperty(PARAM_USER_NAME, ""); set(value) = safeWriteProperty(PARAM_USER_NAME, value!!)
    override var userPass; get() = safeReadProperty(PARAM_USER_PASS, ""); set(value) = safeWriteProperty(PARAM_USER_PASS, value!!)
    override val graphicsQualityLevel; get() = GLRenderConsts.GraphicsQuality.valueOf(safeReadProperty(PARAM_GRAPHICS_QUALITY_LEVEL, GLRenderConsts.GraphicsQuality.ULTRA.name)!!)
    override var isIn_2D_Mode; get() = safeReadProperty(PARAM_IN_2D_MODE, false); set(value) = safeWriteProperty(PARAM_IN_2D_MODE, value)
    override var viewportWidth; get() = safeReadProperty(PARAM_VIEWPORT_WIDTH, 1280); set(value) = safeWriteProperty(PARAM_VIEWPORT_WIDTH, value)
    override var viewportHeight; get() = safeReadProperty(PARAM_VIEWPORT_HEIGHT, 618); set(value) = safeWriteProperty(PARAM_VIEWPORT_HEIGHT, value)
    override var vSync; get() = safeReadProperty(PARAM_VSYNC, false); set(value) = safeWriteProperty(PARAM_VSYNC, value)
    override var fullScreen; get() = safeReadProperty(PARAM_FULLSCREEN, false); set(value) = safeWriteProperty(PARAM_FULLSCREEN, value)
    override var foregroundFPSLimit; get() = safeReadProperty(PARAM_FOREGROUND_FPS_LIMIT, 0); set(value) = safeWriteProperty(PARAM_FOREGROUND_FPS_LIMIT, value)

    init {
        GDXPreferences::class.java.classLoader.getResourceAsStream(CONFIG_FILE_NAME)?.use { preferences.loadFromXML(it) }
    }

    private fun safeReadProperty(name: String, defValue: String): String? {
        synchronized(lockObject){ return preferences.getProperty(name, defValue) }
    }

    private fun safeReadProperty(name: String, defValue: Boolean) = safeReadProperty(name, defValue.toString()).toBoolean()
    private fun safeReadProperty(name: String, defValue: Int) = safeReadProperty(name, defValue.toString())!!.toInt()

    private fun flush() {
        synchronized(lockObject) {
            val path = this.javaClass.getResource("/settings").path // "/"
            FileOutputStream(File("$path/$CONFIG_FILE_NAME")).use {
                preferences.storeToXML(it, null)
            }
        }
    }

    private fun safeWriteProperty(name: String, value: String) {
        preferences.setProperty(name, value)
        flush()
    }

    private fun safeWriteProperty(name: String, value: Boolean) {
        safeWriteProperty(name, value.toString())
    }

    private fun safeWriteProperty(name: String, value: Int) {
        safeWriteProperty(name, value.toString())
    }

    override fun getWebServiceUrl(defaultValue: String?) = safeReadProperty(BASE_URL_NAME, defaultValue!!)
}