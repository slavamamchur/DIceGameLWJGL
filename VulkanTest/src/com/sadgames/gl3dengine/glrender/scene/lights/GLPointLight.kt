package com.sadgames.gl3dengine.glrender.scene.lights

import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera
import com.sadgames.sysutils.common.Mat4x4
import com.sadgames.sysutils.common.MathUtils.frustumM
import javax.vecmath.Vector3f

class GLPointLight(lightPos: FloatArray, lightColour: Vector3f, camera: GLCamera) : GLLightSource(lightPos, lightColour, camera) {

    override fun updateProjectionMatrix(width: Int, height: Int) {
        Mat4x4(projectionMatrix)()
        val ratio = (if (width > height) width.toFloat() / height else height.toFloat() / width) * 0.75f

        frustumM(projectionMatrix, 0, -ratio, ratio, -ratio, ratio, 1f, 7f) //far = 12
        //perspectiveM(projectionMatrix, 0 , DEFAULT_CAMERA_VERTICAL_FOV, ratio, 1f, 100f)
    }

}