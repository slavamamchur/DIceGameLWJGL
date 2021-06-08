package com.cubegames.vaa.client

object Consts {
    const val HEADER_USER_NAME = "user-name"
    const val HEADER_USER_PASS = "user-pass"
    const val HEADER_USER_TOKEN = "user-token"
    const val PARAM_USER_TOKEN = "?$HEADER_USER_TOKEN="
    const val BASE_URL = "http://localhost:8080/engine" //"http://192.168.0.109:8080/engine"
    const val LOGIN_URL = "/login"
    const val REGISTER_URL = "/register"
    const val LIST_URL = "/list"
    const val DELETE_URL = "/delete/"
    const val FIND_URL = "/find/"
    const val CREATE_URL = "/create"
    const val UPDATE_URL = "/update"
    const val MAP_URL = "/map"
    const val GAME_URL = "/game"
    const val INSTANCE_URL = "/instance"
    const val PLAYER_URL = "/player"
    const val MAP_LIST_URL = MAP_URL + LIST_URL
    const val MAP_GET_URL = MAP_URL + FIND_URL
    const val MAP_DELETE_URL = MAP_URL + DELETE_URL
    const val MAP_IMAGE_URL = "/image/"
    const val MAP_RELIEF_URL = "/relief/"
    const val MAP_UPDATE_URL = MAP_URL + UPDATE_URL
    const val MAP_CREATE_URL = MAP_URL + CREATE_URL
    const val MAP_UPLOAD_IMAGE_JSON_URL = "$MAP_URL/file/image"
    const val MAP_UPLOAD_RELIEF_JSON_URL = "$MAP_URL/file/relief"
    const val INSTANCE_LIST_URL = INSTANCE_URL + LIST_URL
    const val INSTANCE_DELETE_URL = INSTANCE_URL + DELETE_URL
    const val INSTANCE_FINISH_URL = "$INSTANCE_URL/finish/"
    const val INSTANCE_GET_URL = INSTANCE_URL + FIND_URL
    const val INSTANCE_RESTART_URL = "$INSTANCE_URL/restart/"
    const val INSTANCE_MOVE_URL = "$INSTANCE_URL/move/"
    const val INSTANCE_START_URL = "$INSTANCE_URL/start"
    const val GAME_LIST_URL = GAME_URL + LIST_URL
    const val GAME_DELETE_URL = GAME_URL + DELETE_URL
    const val GAME_CREATE_URL = GAME_URL + CREATE_URL
    const val GAME_UPDATE_URL = GAME_URL + UPDATE_URL
    const val PLAYER_LIST_URL = PLAYER_URL + LIST_URL
    const val PLAYER_DELETE_URL = PLAYER_URL + DELETE_URL
    const val PLAYER_CREATE_URL = PLAYER_URL + CREATE_URL
    const val PLAYER_UPDATE_URL = PLAYER_URL + UPDATE_URL
}