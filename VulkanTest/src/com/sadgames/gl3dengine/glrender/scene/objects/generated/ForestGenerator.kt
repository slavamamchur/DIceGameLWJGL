package com.sadgames.gl3dengine.glrender.scene.objects.generated

import com.sadgames.gl3dengine.gamelogic.GameEventsCallbackInterface
import com.sadgames.gl3dengine.glrender.scene.objects.InstancedRandomizer
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram

class ForestGenerator(logic: GameEventsCallbackInterface?,
                      objFileName: String,
                      program: VBOShaderProgram?,
                      itemCount: Int,
                      density: Float):
        InstancedRandomizer(logic, objFileName, program, itemCount, density, true, false) {

    init {
        textureResName = "$objFileName.png"
    }
}