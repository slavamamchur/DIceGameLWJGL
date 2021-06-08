package com.sadgames.gl3dengine.glrender.scene.postprocess

import com.sadgames.gl3dengine.gamelogic.client.GameConst.NO_POST_EFFECTS
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture
import org.lwjgl.opengl.GL20

class PostProcessStep @JvmOverloads constructor(var map: AbstractTexture,
                                                     var blendMap: AbstractTexture? = null,
                                                     var effects: Int = NO_POST_EFFECTS,
                                                     var params: Map<String, Any>? = null,
                                                     var blendFunc: Int = GL20.GL_ONE_MINUS_SRC_ALPHA)
