package com.sadgames.gl3dengine.input

import com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_VERTICAL_FOV
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.vulkan.newclass.input.IGestureDetectorListener

class MyGestureListener(private val renderer: GLRendererInterface<SceneObjectsTreeItem>):
    IGestureDetectorListener {

    companion object {
        const val MOUSE_WHEEL_SENSITIVITY = 0.1f
        const val TOUCH_SCALE_FACTOR = 22.5f / 320

        val lockObject = Any()
    }

    private var distance = 1f
    private val mPreviousX = 0f
    private val mPreviousY = 0f
    private var mScaleFactor = 1.0f
            var oldScaleFactor = 0f

    override fun onTap() {
        //TODO("Not yet implemented")
    }

    override fun onSwipe(dX: Float, dY: Float) {
        val camera = renderer.camera

        synchronized(lockObject) {
            camera?.rotateX(-dY * TOUCH_SCALE_FACTOR / 2)
            camera?.rotateY(dX * TOUCH_SCALE_FACTOR * 2)
            camera?.updateViewMatrix()
        }
    }

    override fun onZoom(amount: Float) {
        distance += amount * MOUSE_WHEEL_SENSITIVITY
        distance = if (distance <= 0) 0.1f else distance
        oldScaleFactor = mScaleFactor;
        mScaleFactor = distance;

        if (oldScaleFactor != mScaleFactor) {
            renderer.camera?.vFov = (DEFAULT_CAMERA_VERTICAL_FOV / mScaleFactor);
            //requestRender();
        }
    }
}