package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameCollectionResponse
import java.io.Serializable

class GameController :
        AbstractController(URL_GAME, GameEntity::class.java, GameCollectionResponse::class.java, HTTP_METHOD_GET) {

    fun removeChild(parentId: String, childName: String, childIndex: Int) {
        controller?.iRemoveChild(parentId, childName, childIndex)
    }

    fun addChild(parentId: String, childName: String, childEntity: Serializable) {
        controller?.iAddChild(parentId, childName, childEntity)
    }
}
