package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_PING
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.PingResponse

class PingRequest : AbstractController(URL_PING, PingResponse::class.java, GenericCollectionResponse::class.java, HTTP_METHOD_GET) {

    fun doPing() =
        try {
                (controller?.iGetResponse("") as PingResponse?)?.name?.isEmpty() != true
        } catch (e: Exception) {
                 false
        }
}
