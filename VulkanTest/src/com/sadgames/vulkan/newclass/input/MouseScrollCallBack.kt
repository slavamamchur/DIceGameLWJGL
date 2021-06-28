package com.sadgames.vulkan.newclass.input

import org.lwjgl.glfw.GLFWScrollCallback


class MouseScrollCallBack(private val listener: IGestureDetectorListener?) : GLFWScrollCallback() {

    private var xScroll = 0f
    private var yScroll = 0f

    override fun invoke(window: Long, xoffset: Double, yoffset: Double) {
        xScroll = xoffset.toFloat()
        yScroll = yoffset.toFloat()

        listener?.onZoom(yScroll)
    }

}