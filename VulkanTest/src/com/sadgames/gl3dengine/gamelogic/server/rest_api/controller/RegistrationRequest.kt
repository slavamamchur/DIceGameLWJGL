package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_REGISTER
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.ErrorEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.UserEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse
import java.util.*

class RegistrationRequest(private val user: UserEntity) : AbstractController(URL_REGISTER, ErrorEntity::class.java, GenericCollectionResponse::class.java, HTTP_METHOD_POST) {

    init {
        controller?.iSetParams(HashMap())
    }

    fun doRegister() {
        controller?.iSendPOSTRequest("", user)
    }

}
