package com.sadgames.gl3dengine.glrender.scene.lights

import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera
import com.sadgames.sysutils.common.MathUtils.*
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera.Companion.FAR_PLANE
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera.Companion.NEAR_PLANE
import com.sadgames.sysutils.common.*
import java.lang.Math.toDegrees
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f
import kotlin.math.acos
import kotlin.math.atan

open class GLLightSource(lightPos: FloatArray, var lightColour: Vector3f, camera: GLCamera) {

    var lightPosInModelSpace = lightPos
        set(value) {
            if (!field.contentEquals(value)) {
                field = value
                updateLightPosInEyeSpace()
                position2D = toPosition2D()
                lightDirection = -Vector3f(value).normalized()
            }
        }

    var mCamera = camera
        set(value) {
            field = value
            updateLightPosInEyeSpace()
            position2D = toPosition2D()
            value.lightSourceObserver = this
        }

    var lightPosInEyeSpace: FloatArray? = null
    val viewMatrix = Mat4x4(FloatArray(16))()
    val projectionMatrix = FloatArray(16)
    var lightDirection: Vector3f? = null
    var position2D: FloatArray = FloatArray(3)

    init {
        updateLightPosInEyeSpace()
        position2D = toPosition2D()
        lightDirection = -Vector3f(lightPosInModelSpace).normalized()
        mCamera.lightSourceObserver = this
    }

    fun onCameraViewMatrixChanged() {
        updateLightPosInEyeSpace()
        position2D = toPosition2D()
    }

    inline fun onCameraProjectionMatrixChanged() {
        position2D = toPosition2D()
    }

    fun updateViewProjectionMatrix(width: Int, height: Int) {
        updateViewMatrix(true)
        updateProjectionMatrix(width, height)
    }

    fun getColorByAngle(day: Vector3f, night: Vector3f): Vector3f {
        val angle = 0.0f
        /** for dynamic day-night cycle
         *toDegrees(acos(Vector2f(lightDirection.x, lightDirection.z).length().toDouble())).toFloat() - 90.0f */

        return Vector3f(day.x + (day.x - night.x) / 90f * angle,
                day.y + (day.y - night.y) / 90f * angle,
                day.z + (day.z - night.z) / 90f * angle)
    }

    inline fun updateLightPosInEyeSpace() {
        lightPosInEyeSpace = (Mat4x4(mCamera.viewMatrix) * Vector4f(lightPosInModelSpace)).toArray()
    }

    fun toScreenSpace(position: Vector3f, scale: Float) : Vector2f? {
        val model = Mat4x4(FloatArray(16))
        model()
        model += position

        val matMV = applyViewMatrix(model.toMatrix4f(), getMatrix4f(mCamera.viewMatrix))
        matMV *= scale

        return convertToScreenSpace(Vector3f(0f, 0f, 0f), getMatrix4f((Mat4x4(mCamera.projectionMatrix) * matMV).value))
    }

    inline fun toPosition2D() = (
        toScreenSpace(Vector3f(lightPosInModelSpace).normalized() * 4.5f + mCamera.cameraPosition,1f) ?:
        Vector2f(-1000f, -1000f)).toArray()

    protected open fun updateViewMatrix(useGL: Boolean) {
        Mat4x4(viewMatrix)()

        if (useGL) /** classic openGL view matrix  */
            setLookAtM(viewMatrix, 0, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2],
                       lightDirection!!.x, lightDirection!!.y, lightDirection!!.z,
                      0f, 1f, 0f)
        else { /** set view matrix via pitch/roll angles */
            val center = Vector3f(lightDirection)
            val pitch = toDegrees(acos(Vector2f(lightDirection!!.x, lightDirection!!.z).length().toDouble())).toFloat()
            val yaw = toDegrees((atan(lightDirection!!.x / lightDirection!!.z).toDouble())).toFloat()
                    - if (lightDirection!!.z > 0f) 180f else 0f

            rotateM(viewMatrix, pitch, -yaw, 0f)
            translateM(viewMatrix, 0, center.x, center.y, center.z)
        }
    }

    protected open fun updateProjectionMatrix(width: Int, height: Int) {
        Mat4x4(projectionMatrix)()
        val ratio = (if (width > height) width.toFloat() / height else height.toFloat() / width) * 1.75f

        orthoM(projectionMatrix, 0, -ratio, ratio, -1.75f, 1.75f, NEAR_PLANE, FAR_PLANE)
    }

}