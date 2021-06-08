package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller

import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse

abstract class AbstractController protected constructor(action: String,
                                                        responseType: Class<out BasicEntity>,
                                                        listType: Class<out GenericCollectionResponse<*>>,
                                                        method: Int) {
    companion object {
        const val HTTP_METHOD_GET = 0
        const val HTTP_METHOD_POST = 1
    }

    protected var controller: EntityControllerInterface? = null

    val responseList: Collection<*>?
        get() = controller?.iGetEntityList()

    init {
        controller = GdxExt.restAPI.iGetEntityController(action, responseType, listType, method)
    }

    fun delete(entity: BasicNamedDbEntity) {
        controller?.iDeleteEntity(entity)
    }

    fun update(entity: BasicNamedDbEntity) = controller?.iUpdateEntity(entity)

    fun find(id: String) = controller?.iGetEntity(id)

}
