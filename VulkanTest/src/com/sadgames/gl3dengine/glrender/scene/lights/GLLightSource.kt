package com.sadgames.gl3dengine.glrender.scene.lights

import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera.Companion.FAR_PLANE
import com.sadgames.sysutils.common.*
import com.sadgames.sysutils.common.MathUtils.*
import java.lang.Math.toDegrees
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f
import kotlin.math.acos
import kotlin.math.atan
import kotlin.properties.Delegates.observable

open class GLLightSource(lightPos: FloatArray, var lightColour: Vector3f, camera: GLCamera) {

    companion object {
        @JvmStatic fun getPosByDirection(direction: Vector3f) = Vector3f(direction) * (FAR_PLANE * 100f)
        @JvmStatic fun getUpDirection(dir: Vector3f) = Vector3f(dir.x, (-dir.x * dir.x - dir.z * dir.z) / dir.y, dir.z)
        @JvmStatic fun getRightDirection(dir: Vector3f, up: Vector3f) = up cross dir
    }

    var lightPosInModelSpace = FloatArray(4)
        set(value) {
            lightDirection = Vector3f(value).normalized()
            up = getUpDirection(lightDirection)
            /** val check = lightDirection dot up -> check true orthogonal view */
            getPosByDirection(lightDirection) to field

            updateLightPosInEyeSpace()
            toPosition2D(mCamera.cameraPosition)
        }

    var mCamera by observable(camera) { _, _, newValue ->
        updateLightPosInEyeSpace()
        toPosition2D(newValue.cameraPosition)
        newValue.lightSourceObserver = this
    }

    val lightPosInEyeSpace: FloatArray = FloatArray(4)
    val position2D: FloatArray = FloatArray(3)
    val viewMatrix: FloatArray = Mat4x4(FloatArray(16))()
    val projectionMatrix = FloatArray(16)

    private lateinit var lightDirection: Vector3f
    private lateinit var up: Vector3f

    init {
        lightPosInModelSpace = lightPos
        mCamera.lightSourceObserver = this
    }

    fun onCameraViewMatrixChanged() {
        updateLightPosInEyeSpace()
        onCameraProjectionMatrixChanged()
    }

    inline fun onCameraProjectionMatrixChanged() { toPosition2D(mCamera.cameraPosition) }

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

    inline fun updateLightPosInEyeSpace() { (Mat4x4(mCamera.viewMatrix) * Vector4f(lightPosInModelSpace)) to lightPosInEyeSpace }

    fun toScreenSpace(position: Vector3f, scale: Float) : Vector2f? {
        val model = Mat4x4(FloatArray(16))
        model()
        model += position

        val matMV = applyViewMatrix(model.toMatrix4f(), getMatrix4f(mCamera.viewMatrix))
        matMV *= scale

        return convertToScreenSpace(Vector3f(0f, 0f, 0f), getMatrix4f((Mat4x4(mCamera.projectionMatrix) * matMV).value))
    }

    fun toPosition2D(cameraPosition: Vector3f) =
        (toScreenSpace(Vector3f(lightPosInModelSpace).normalized() * 4.5f + cameraPosition,1f) ?: Vector2f(-1000f, -1000f)) to position2D

    protected open fun updateViewMatrix(useGL: Boolean) {
        if (useGL) {/** classic openGL view matrix  */
            val right = getRightDirection(lightDirection, up)

            viewMatrix[0] = right.x
            viewMatrix[1] = up.x
            viewMatrix[2] = lightDirection.x
            viewMatrix[3] = 0.0f

            viewMatrix[4] = right.y
            viewMatrix[5] = up.y
            viewMatrix[6] = lightDirection.y
            viewMatrix[7] = 0.0f

            viewMatrix[8] = right.z
            viewMatrix[9] = up.z
            viewMatrix[10] = lightDirection.z
            viewMatrix[11] = 0.0f

            viewMatrix[12] = 0.0f
            viewMatrix[13] = 0.0f
            viewMatrix[14] = 0.0f
            viewMatrix[15] = 1.0f

            /*setLookAtM(viewMatrix, 0, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2],
                       0f, 0f, 0f,
                       up.x, up.y, up.z)*/
        }
        else { /** set view matrix via pitch/roll angles */
            val center = Vector3f(lightDirection)
            val pitch = toDegrees(acos(Vector2f(center.x, center.z).length().toDouble())).toFloat()
            val yaw = toDegrees((atan(center.x / center.z).toDouble())).toFloat() - if (center.z > 0f) 180f else 0f

            Mat4x4(viewMatrix)()
            rotateM(viewMatrix, pitch, -yaw, 0f)
            translateM(viewMatrix, 0, center.x, center.y, center.z)
        }
    }

    protected open fun updateProjectionMatrix(width: Int, height: Int) {
        Mat4x4(projectionMatrix)()
        val ratio = width.toFloat() / height.toFloat()

        orthoM(projectionMatrix, 0, -8f * ratio, 8f * ratio, -10f, 10f, -FAR_PLANE, FAR_PLANE)
    }

}