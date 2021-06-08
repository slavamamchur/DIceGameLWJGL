package com.sadgames.gl3dengine.glrender.scene.camera

import com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_VERTICAL_FOV
import com.sadgames.sysutils.common.Mat4x4
import com.sadgames.sysutils.common.MathUtils.*
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation
import com.sadgames.sysutils.common.normalized
import com.sadgames.sysutils.common.unaryMinus
import java.lang.Math.*
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.experimental.and
import kotlin.math.atan
import kotlin.properties.Delegates.observable

abstract class GLCamera(cameraPosition: Vector3f, var pitch: Float, var yaw: Float, var roll: Float): GLAnimation.IAnimatedObject {
    companion object {
        const val NEAR_PLANE = 0.01f
        const val FAR_PLANE = 100f
        const val ROTATE_BY_X: Short = 1
        const val ROTATE_BY_Y: Short = 2
        const val ROTATE_BY_Z: Short = 4

    }

    var transformMatrix = Mat4x4(FloatArray(16))()
    var viewMatrix: FloatArray by observable(FloatArray(16)) { _, _, _ -> lightSourceObserver?.onCameraViewMatrixChanged()
                                  }
    var projectionMatrix: FloatArray by observable(FloatArray(16)) { _, _, _ -> lightSourceObserver?.onCameraProjectionMatrixChanged()
                                        }
    var cameraPosition: Vector3f = cameraPosition; set(value) { field = value; updateViewMatrix() }
    var vfov = DEFAULT_CAMERA_VERTICAL_FOV; set(value) { field = value; setVfovInternal() }
    var zoomed_vfov = DEFAULT_CAMERA_VERTICAL_FOV
    var aspectRatio = -1f; set(value) { field = value; updateProjectionMatrix() }
    val cameraDirection: Vector3f; get() = calcDirectionByPos(cameraPosition)
    var lightSourceObserver: GLLightSource? = null

    constructor(eyeX: Float, eyeY: Float, eyeZ: Float, pitch: Float, yaw: Float, roll: Float):
            this(Vector3f(eyeX, eyeY, eyeZ), pitch, yaw, roll)

    init {
        updateViewMatrix()
    }

    protected open fun setVfovInternal() = updateProjectionMatrix()

    open fun updateViewMatrix() {
        rotateM(viewMatrix, pitch, yaw, roll)
        translateM(viewMatrix, 0, -cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
        viewMatrix = viewMatrix
    }

    protected open fun updateProjectionMatrix() {
        setIdentityM(projectionMatrix, 0)
        perspectiveM(projectionMatrix, 0, vfov, aspectRatio, NEAR_PLANE, FAR_PLANE)
        projectionMatrix = projectionMatrix
    }

    fun updatePitch(pitch: Float) {
        this.pitch = pitch
        updateViewMatrix()
    }

    fun updateYaw(yaw: Float) {
        this.yaw = yaw
        updateViewMatrix()
    }

    fun updateRoll(roll: Float) {
        this.roll = roll
        updateViewMatrix()
    }

    inline infix fun directSetPitchByDirection(direction: Vector3f) {
        pitch = toDegrees(acos(Vector2f(direction.x, direction.z).length().toDouble())).toFloat()
    }

    inline infix fun directSetYawByDirection(direction: Vector3f) {
        yaw = -(toDegrees(atan(direction.x / direction.z.toDouble())).toFloat() - if (direction.z > 0) 180f else 0f)
    }

    inline infix fun calcDirectionByPos(cameraPosition: Vector3f?) = -Vector3f(cameraPosition).normalized()

    fun calcDirectionByAngles(): Vector3f {
        val direction = Vector3f()
        direction.x = (sin(toRadians(pitch.toDouble())) * cos(toRadians(yaw.toDouble()))).toFloat()
        direction.y = (sin(toRadians(pitch.toDouble())) * sin(toRadians(yaw.toDouble()))).toFloat()
        direction.z = cos(toRadians(pitch.toDouble())).toFloat()
        /** method #2
         * direction.x = cos(glm::radians(pitch)) * cos(glm::radians(yaw));
         * direction.y = sin(glm::radians(pitch));
         * direction.z = cos(glm::radians(pitch)) * sin(glm::radians(yaw));  */
        /** method #3
         * #apply yaw (around y)
         * x = x * cos(yaw) - z * sin(yaw)
         * z = z * cos(yaw) + x * sin(yaw)
         *
         * #apply pitch (around x)
         * y = y * cos(roll) - z * sin(roll)
         * z = z * cos(roll) + y * sin(roll)
         *
         * #apply roll (around z)
         * x = x * cos(pitch) - y * sin(pitch)
         * y = y * cos(pitch) + x * sin(pitch)  */
        return direction
    }

    fun flipVertical() {
        pitch = -pitch
        cameraPosition.y = -cameraPosition.y

        updateViewMatrix()
    }

    abstract fun rotateX(angle: Float)
    abstract fun rotateY(angle: Float)
    abstract fun rotateZ(angle: Float)


    /** IAnimatedObject implementation ----------------------------------------------------------- */
    override fun getTransformationMatrix(): FloatArray {
        return transformMatrix
    }


    override fun setRotation(angle: Float, rotationAxesMask: Short) {
        if ((rotationAxesMask and ROTATE_BY_X) != 0.toShort()) pitch += angle
        if (rotationAxesMask and ROTATE_BY_Y != 0.toShort()) yaw += angle
        if (rotationAxesMask and ROTATE_BY_Z != 0.toShort()) roll += angle

        updateViewMatrix()
    }

    override fun setZoomLevel(zoomLevel: Float) {
        vfov = zoomed_vfov / zoomLevel
    }

    override fun onAnimationEnd() {
        zoomed_vfov = vfov
    }

}