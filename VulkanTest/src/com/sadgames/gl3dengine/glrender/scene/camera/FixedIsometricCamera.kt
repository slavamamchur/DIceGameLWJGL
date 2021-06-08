package com.sadgames.gl3dengine.glrender.scene.camera

import com.sadgames.sysutils.common.MathUtils.cos
import com.sadgames.sysutils.common.MathUtils.sin
import javax.vecmath.Vector3f

class FixedIsometricCamera(eyeX: Float, eyeY: Float, eyeZ: Float, pitch: Float, yaw: Float, roll: Float):
        GLCamera(eyeX, eyeY, eyeZ, pitch, yaw, roll) {

    override fun rotateX(byAngle: Float) {
        var angle = byAngle
        val cameraPos = Vector3f(cameraPosition)
        val direction = cameraDirection
        val oldPitch = pitch

        if (yaw > -45f && yaw <= 45f || yaw in 135f..225f) {
            angle = if (yaw > -45f && yaw <= 45f) angle else -angle
            cameraPos.y = cos(angle) * (cameraPos.y - direction.y) - sin(angle) * (cameraPos.z - direction.z) + direction.y
            cameraPos.z = sin(angle) * (cameraPos.y - direction.y) + cos(angle) * (cameraPos.z - direction.z) + direction.z
        } else {
            angle = if (yaw <= -45f && yaw >= -89.999f || yaw in 224.999f..270f) -angle else angle
            cameraPos.x = cos(angle) * (cameraPos.x - direction.x) - sin(angle) * (cameraPos.y - direction.y) + direction.x
            cameraPos.y = sin(angle) * (cameraPos.x - direction.x) + cos(angle) * (cameraPos.y - direction.y) + direction.y
        }

        directSetPitchByDirection(calcDirectionByPos(cameraPos))

        if (pitch in 1.5f..90.0f)
            cameraPosition = cameraPos
        else
            pitch = oldPitch
    }

    override fun rotateY(angle: Float) {
        val direction = cameraDirection

        cameraPosition.x =
            cos(angle) * (cameraPosition.x - direction.x) - sin(angle) * (cameraPosition.z - direction.z) + direction.x
        cameraPosition.z =
            sin(angle) * (cameraPosition.x - direction.x) + cos(angle) * (cameraPosition.z - direction.z) + direction.z

        directSetYawByDirection(cameraDirection)
    }

    override fun rotateZ(angle: Float) {}
    override fun setPosition(position: Vector3f) {
        cameraPosition = position
    }
}