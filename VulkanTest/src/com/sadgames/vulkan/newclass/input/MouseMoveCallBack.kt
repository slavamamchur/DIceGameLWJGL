package com.sadgames.vulkan.newclass.input

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCursorPosCallback

class MouseMoveCallBack(private val listener: IGestureDetectorListener?): GLFWCursorPosCallback() {
    private var mouseLocked = false
    private var oldX = -1.0
    private var oldY = -1.0

    override fun invoke(window: Long, xpos: Double, ypos: Double) {
        mouseLocked = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS
        if (mouseLocked) {
            val dX = if(oldX < 0) 0f else (xpos - oldX).toFloat()
            val dY = if(oldY < 0) 0f else (ypos - oldY).toFloat()
            oldX = xpos
            oldY = ypos
            listener?.onSwipe(dX,dY)
        }
        else {
            oldX = -1.0
            oldY = -1.0
        }
    }
}