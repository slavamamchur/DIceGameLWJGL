package com.sadgames.vulkan.newclass.input

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWMouseButtonCallback

/**
 * Created by Slava Mamchur on 16.05.21.
 */
class MouseButtonCallBack(private val listener: IGestureDetectorListener?): GLFWMouseButtonCallback() {

    //todo: call2dUI listener
    override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
            listener?.onTap()
    }

}