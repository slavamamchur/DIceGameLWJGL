package com.sadgames.gl3dengine.manager

import com.sadgames.gl3dengine.glrender.GLRenderConsts

interface SettingsManagerInterface {
    var authToken: String?
    val isLoggedIn: Boolean
    val isStayLoggedIn: Boolean
    var userName: String?
    var userPass: String?
    val graphicsQualityLevel: GLRenderConsts.GraphicsQuality
    var isIn_2D_Mode: Boolean
    var viewportWidth: Int
    var viewportHeight: Int
    var vSync: Boolean
    var fullScreen: Boolean
    var foregroundFPSLimit: Int

    fun getWebServiceUrl(defaultValue: String?): String?
}