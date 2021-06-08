package com.sadgames.gl3dengine

import com.sadgames.sysutils.common.SysUtilsConsts.BYTES_IN_MB

object GLEngineConsts {
    const val MODELS_RESOURCE_FOLDER_NAME = "models/"
    const val COMPRESSED_TEXTURE_FILE_EXT = ".pkm"
    const val NOT_ENOUGH_SPACE_IN_CACHE_ERROR_MESSAGE = "Not enough space: item size > cache size."
    const val TEXTURE_CACHE_SIZE: Long = 192L * BYTES_IN_MB
    const val TEXTURE_CACHE_RAM_SIZE: Long = 1024L * BYTES_IN_MB
}
