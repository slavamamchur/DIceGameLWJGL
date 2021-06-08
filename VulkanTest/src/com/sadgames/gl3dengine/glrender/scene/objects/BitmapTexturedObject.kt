package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram

internal abstract class BitmapTexturedObject(type: GLObjectType?, textureResName: String?, program: VBOShaderProgram?):
        AbstractGL3DObject(type, program) {

    constructor(type: GLObjectType?, program: VBOShaderProgram?, color: Int): this(type, color.toString(), program)

    init { this.textureResName = textureResName }
}
