package com.sadgames.gl3dengine.glrender.scene.camera

import javax.vecmath.Vector3f

class FPVCamera(eyeX: Float, eyeY: Float, eyeZ: Float, pitch: Float, yaw: Float, roll: Float):
        GLCamera(eyeX, eyeY, eyeZ, pitch, yaw, roll) {

    override fun rotateX(angle: Float) {
        pitch += angle
    }

    override fun rotateY(angle: Float) {
        yaw += angle
    }

    override fun rotateZ(angle: Float) {
        roll += angle
    }

    override fun setPosition(position: Vector3f) {
        cameraPosition = position
    }
}