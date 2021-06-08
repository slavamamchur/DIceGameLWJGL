package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.*
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.AuthTokenEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse
import java.util.*

class LoginRequest(userName: String, userPass: String) :
        AbstractController(URL_LOGIN, AuthTokenEntity::class.java, GenericCollectionResponse::class.java, HTTP_METHOD_GET) {

    init {
        val params = HashMap<String, String>()
        params[PARAM_LOGIN_USER_NAME] = userName
        params[PARAM_LOGIN_USER_PASS] = userPass

        controller?.iSetParams(params)
    }

    fun doLogin() = controller?.iGetResponse("") as AuthTokenEntity
}
