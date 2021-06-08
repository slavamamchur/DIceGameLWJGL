package com.sadgames.gl3dengine.gamelogic.server.rest_api

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse


interface RestApiInterface {

    var token: String?
    fun login(username: String?, password: String?)

    suspend fun moveGameInstance(gameInstanceEntity: GameInstanceEntity)
    fun finishGameInstance(gameInstanceEntity: GameInstanceEntity)
    suspend fun restartGameInstance(gameInstanceEntity: GameInstanceEntity)

    fun showTurnInfo(gameInstanceEntity: GameInstanceEntity)
    fun showAnimatedText(text: String)
    fun removeLoadingSplash()

    fun iGetEntityController(action: String,
                             entityType: Class<out BasicEntity>,
                             listType: Class<out GenericCollectionResponse<*>>,
                             method: Int): EntityControllerInterface?

    fun iDownloadBitmapIfNotCached(textureResName: String, isRelief: Boolean)
    fun iGetGameInstanceEntity(instanceId: String): GameInstanceEntity
}
