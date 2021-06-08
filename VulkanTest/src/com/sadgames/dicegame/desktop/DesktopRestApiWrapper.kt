package com.sadgames.dicegame.desktop

import com.cubegames.engine.domain.entities.GameInstance
import com.cubegames.engine.domain.entities.GameMap
import com.cubegames.vaa.client.RestClient
import com.sadgames.gl3dengine.gamelogic.client.GameConst
import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import com.sadgames.sysutils.common.DBUtils.isBitmapCached
import com.sadgames.sysutils.common.DBUtils.saveBitmap2DB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object DesktopRestApiWrapper: RestApiInterface {

    val restClient by lazy { RestClient.token = settingsManager.authToken; RestClient }
    override var token: String? get() = restClient.token; set(value) {restClient.token = value}

    override fun login(username: String?, password: String?) { restClient.login(username, password) }
    override fun iGetGameInstanceEntity(instanceId: String) = restClient.getInstance(instanceId)

    private fun convertInstance(from: GameInstance, to: GameInstanceEntity) {
        to.lastUsedDate = from.lastUsedDate
        to.players = from.players
        to.state = GameConst.GameState.values()[from.state.ordinal]
        to.setStateLua(to.state.ordinal)
        to.currentPlayer = from.currentPlayer
        to.stepsToGo = from.stepsToGo
    }

    override suspend fun moveGameInstance(gameInstanceEntity: GameInstanceEntity) {
        GlobalScope.async {
            val updated = restClient.moveInstance(gameInstanceEntity.id, gameInstanceEntity.stepsToGo)
            convertInstance(updated, gameInstanceEntity)
        }.await()
    }

    override suspend fun restartGameInstance(gameInstanceEntity: GameInstanceEntity) {
        GlobalScope.async {
            restClient.restartInstance(gameInstanceEntity.id)
        }.await()
    }

    override fun finishGameInstance(gameInstanceEntity: GameInstanceEntity) {
        GlobalScope.launch {
            restClient.finishInstance(gameInstanceEntity.id)
            gameInstanceEntity.setState(GameConst.GameState.FINISHED)
        }
    }

    override fun showTurnInfo(gameInstanceEntity: GameInstanceEntity) {
        //todo: show message
    }

    override fun showAnimatedText(text: String) {
        //todo: show message
    }

    override fun removeLoadingSplash() {

    }

    override fun iGetEntityController(action: String, entityType: Class<out BasicEntity>, listType: Class<out GenericCollectionResponse<*>>, method: Int): EntityControllerInterface? {
        return null
    }

    override fun iDownloadBitmapIfNotCached(textureResName: String, isRelief: Boolean) {
        val map = restClient.getGameMap(textureResName)

        try {
             if (map?.id?.isEmpty() == false)
                if (isRelief)
                    internalSavePicture(map, "rel_", "Relief map is empty.")
                else
                    internalSavePicture(map, "", "GameEntity map is empty.")
        }
        catch (e: Exception) {}
    }

    @Throws(NoSuchFieldException::class)
    private fun internalSavePicture(map: GameMap, namePrefix: String, errorMessage: String) {
        val name = "$namePrefix${map.id}"

        if (!isBitmapCached(name, map.lastUsedDate)) {
            val mapArray = restClient.getBinaryData(RestClient.getMapImagePostfix(map.id, "rel_" != namePrefix)) //todo: call async (for kotlin only)

            if (mapArray == null)
                throw NoSuchFieldException(errorMessage)
            else
                try {
                    saveBitmap2DB(mapArray, name, map.lastUsedDate)
                } catch (e: Exception) {
                    throw RuntimeException(errorMessage)
                }
        }
    }
}
