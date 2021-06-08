package com.sadgames.sysutils.common

import java.io.File

object SysUtilsConsts {
    const val BYTES_IN_MB = 1024 * 1024
    const val DB_NAME = "CACHED_IMAGES_DB"
    const val DB_TABLE_NAME = "CACHED_IMAGES"
    const val MAP_ID_DB_FIELD = "MAP_ID"
    const val CHUNK_NUMBER_DB_FIELD = "CHUNK_NUMBER"
    const val MAP_IMAGE_DB_FIELD = "MAP_IMAGE"
    const val MAP_UPDATED_DATE_DB_FIELD = "MAP_UPDATED_DATE"

    val EXTERNAL_PATH = System.getProperty("user.home") + File.separator
    val LOCAL_PATH = File("").absolutePath + File.separator
}