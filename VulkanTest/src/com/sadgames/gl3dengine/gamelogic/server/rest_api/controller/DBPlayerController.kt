package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_PLAYER
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.DbPlayerEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.DBPlayerCollectionResponse

class DBPlayerController :
        AbstractController(URL_PLAYER, DbPlayerEntity::class.java, DBPlayerCollectionResponse::class.java, HTTP_METHOD_GET)
