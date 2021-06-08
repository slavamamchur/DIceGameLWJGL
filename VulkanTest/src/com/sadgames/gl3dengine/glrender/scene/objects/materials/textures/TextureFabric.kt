package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures

//import com.sadgames.gl3dengine.gamelogic.client.GameConst.CUBE_TEXTURE_PREFIX

fun createTexture(key: String) =
        /*if (key.startsWith(CUBE_TEXTURE_PREFIX)) CubeMapTexture(key) else */BitmapTexture.createInstance(key)
