package com.cubegames.vaa.client

import com.cubegames.engine.consts.RestCommonConsts
import com.cubegames.engine.domain.entities.DbPlayer
import com.cubegames.engine.domain.entities.Game
import com.cubegames.engine.domain.entities.GameInstance
import com.cubegames.engine.domain.entities.GameMap
import com.cubegames.engine.domain.entities.players.InstancePlayer
import com.cubegames.engine.domain.rest.PaginationInfo
import com.cubegames.engine.domain.rest.requests.RegisterRequest
import com.cubegames.engine.domain.rest.requests.StartNewGameRequest
import com.cubegames.engine.domain.rest.responses.BasicResponse
import com.cubegames.engine.domain.rest.responses.GameInstanceResponse
import com.cubegames.engine.domain.rest.responses.GameInstanceStartedResponse
import com.cubegames.engine.domain.rest.responses.IdResponse
import com.cubegames.vaa.client.Consts.INSTANCE_START_URL
import com.cubegames.vaa.client.Consts.MAP_IMAGE_URL
import com.cubegames.vaa.client.Consts.MAP_RELIEF_URL
import com.cubegames.vaa.client.Consts.MAP_URL
import com.cubegames.vaa.client.Consts.PARAM_USER_TOKEN
import com.cubegames.vaa.client.exceptions.EmptyTokenException
import com.cubegames.vaa.client.responces.CollectionResponseDbPlayer
import com.cubegames.vaa.client.responces.CollectionResponseGame
import com.cubegames.vaa.client.responces.CollectionResponseGameMap
import com.cubegames.vaa.client.responces.CollectionResponseInstance
import com.cubegames.vaa.utils.UtilsUI.checkServiceError
import com.google.common.base.Strings
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity
import com.sadgames.sysutils.common.CommonUtils.settingsManager
import org.springframework.data.domain.Sort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.util.logging.Logger

object RestClient {

    private val log = Logger.getLogger(RestClient::class.java.name)
    private val baseUrl: String? = settingsManager.getWebServiceUrl(Consts.BASE_URL)

    var token: String? = null

    inline val mapList: MutableCollection<GameMap?>?; get() = runRestGet(
            Consts.MAP_LIST_URL,
            CollectionResponseGameMap::class.java,
            createPaging(GameMap.FIELD_LAST_USED_DATE, Sort.Direction.DESC)
    ).collection

    inline val instanceList: MutableCollection<GameInstance?>?; get() = runRestGet(
            Consts.INSTANCE_LIST_URL,
            CollectionResponseInstance::class.java,
            createPaging(GameInstance.FIELD_LAST_USED_DATE, Sort.Direction.DESC)
    ).collection

    inline val gameList: MutableCollection<Game?>?; get() = runRestGet(
            Consts.GAME_LIST_URL,
            CollectionResponseGame::class.java,
            createPaging(Game.FIELD_CREATED_DATE, Sort.Direction.DESC)
    ).collection

    inline val dbPlayerList: MutableCollection<DbPlayer?>?; get() = runRestGet(
            Consts.PLAYER_LIST_URL,
            CollectionResponseDbPlayer::class.java,
            createPaging(DbPlayer.FIELD_NAME, Sort.Direction.ASC)
    ).collection

    fun login(username: String?, password: String?) {
        val headers = HttpHeaders()
        headers[Consts.HEADER_USER_NAME] = username
        headers[Consts.HEADER_USER_PASS] = password

        token = RestTemplate().exchange(getUrl(Consts.LOGIN_URL),
                                        HttpMethod.GET,
                                        HttpEntity("", headers),
                                        IdResponse::class.java)?.body?.id
    }

    fun logout() { token = null }

    fun register(username: String?, password: String?, email: String?, language: String?) {
        val request = RegisterRequest()
        request.userName = username
        request.userPass = password
        request.email = email
        request.language = language

        runRestWithoutTokenCheck(Consts.REGISTER_URL, HttpMethod.POST, Void::class.java, request, null)
    }

    inline fun finishInstance(instanceId: String) = runRestGet(Consts.INSTANCE_FINISH_URL + instanceId, GameInstanceResponse::class.java)
    inline fun deleteInstance(instanceId: String) = runRestDelete(Consts.INSTANCE_DELETE_URL + instanceId, Void::class.java)
    inline fun deleteGame(gameId: String) = runRestDelete(Consts.GAME_DELETE_URL + gameId, Void::class.java)
    inline fun deleteMap(mapId: String) = runRestDelete(Consts.MAP_DELETE_URL + mapId, Void::class.java)
    inline fun deleteDbPlayer(playerId: String) = runRestDelete(Consts.PLAYER_DELETE_URL + playerId, Void::class.java)
    inline fun getInstance(id: String) = runRestGet(Consts.INSTANCE_GET_URL + id, GameInstanceEntity::class.java)
    inline fun getGameMap(id: String) = runRestGet(Consts.MAP_GET_URL + id, GameMap::class.java)
    inline fun restartInstance(id: String) = runRestGet(Consts.INSTANCE_RESTART_URL + id, IdResponse::class.java)
    inline fun moveInstance(id: String, steps: Int) = runRestGet(Consts.INSTANCE_MOVE_URL + id + "/" + steps, GameInstance::class.java)

    fun createDbPlayer(name: String?, color: Int): IdResponse {
        val dbPlayer = DbPlayer()
        dbPlayer.name = name
        dbPlayer.color = color

        return runRestPost(Consts.PLAYER_CREATE_URL, IdResponse::class.java, dbPlayer)
    }

    fun updateDbPlayer(id: String?, name: String?, color: Int): IdResponse {
        val dbPlayer = DbPlayer()
        dbPlayer.id = id
        dbPlayer.name = name
        dbPlayer.color = color

        return runRestPut(Consts.PLAYER_UPDATE_URL, IdResponse::class.java, dbPlayer)
    }

    fun createMap(name: String?): IdResponse {
        val map = GameMap()
        map.name = name

        return runRestPost(Consts.MAP_CREATE_URL, IdResponse::class.java, map)
    }

    fun updateMap(id: String?, name: String?): IdResponse {
        val map = GameMap()
        map.id = id
        map.name = name

        return runRestPut(Consts.MAP_UPDATE_URL, IdResponse::class.java, map)
    }

    fun uploadMapImage(id: String?, bytes: ByteArray?) {
        val map = GameMap()
        map.id = id
        map.binaryData = bytes

        runRestPut(Consts.MAP_UPLOAD_IMAGE_JSON_URL, Void::class.java, map)
    }

    fun uploadMapRelief(id: String?, bytes: ByteArray?) {
        val map = GameMap()
        map.id = id
        map.binaryDataRelief = bytes

        runRestPut(Consts.MAP_UPLOAD_RELIEF_JSON_URL, Void::class.java, map)
    }

    fun getMapImageUrl(mapId: String, isImage: Boolean): String {
        return baseUrl +
                MAP_URL + (if (isImage) MAP_IMAGE_URL else MAP_RELIEF_URL) + mapId +
                PARAM_USER_TOKEN + token
    }

    inline fun getBinaryData(url: String) = runRestGet(url, ByteArray::class.java)
    inline fun createGame(game: Game) = runRestPost(Consts.GAME_CREATE_URL, IdResponse::class.java, game)
    inline fun updateGame(game: Game) = runRestPut(Consts.GAME_UPDATE_URL, IdResponse::class.java, game)

    fun startNewInstance(instanceName: String?, gameId: String?, players: List<InstancePlayer?>?, playerIds: List<String?>?): GameInstance {
        val startRequest = StartNewGameRequest()
        startRequest.name = instanceName
        startRequest.gameId = gameId
        startRequest.players = players
        startRequest.playersId = playerIds

        return runRestPost(INSTANCE_START_URL, GameInstanceStartedResponse::class.java, startRequest).instance
    }

    private fun <R> runRestWithoutTokenCheck(urlPostfix: String, method: HttpMethod, responseClass: Class<R>, body: Any, paging: PaginationInfo?): R {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers[Consts.HEADER_USER_TOKEN] = token

        if (paging != null) {
            val sortByField = paging.sortBy
            if (!Strings.isNullOrEmpty(sortByField)) {
                headers[RestCommonConsts.PAGE_SORT_BY_HEADER] = sortByField
                val sort = paging.sort
                if (sort != null) {
                    val order = paging.sort.getOrderFor(sortByField)
                    if (order != null) {
                        val dir = order.direction
                        if (dir != null) {
                            headers[RestCommonConsts.PAGE_SORT_HEADER] = dir.toString()
                        }
                    }
                }
            }
            //TODO: paging (if needed?)
        }

        val resp = restTemplate.exchange(getUrl(urlPostfix), method, HttpEntity(body, headers), responseClass).body
        if (resp is BasicResponse) {
            checkServiceError(resp)
        }

        return resp
    }

    fun <R> runRest(urlPostfix: String, method: HttpMethod, responseClass: Class<R>, body: Any, paging: PaginationInfo?): R {
        checkToken(token)
        return runRestWithoutTokenCheck(urlPostfix, method, responseClass, body, paging)
    }

    inline fun <R> runRest(urlPostfix: String, method: HttpMethod, responseClass: Class<R>, paging: PaginationInfo?): R {
        return runRest(urlPostfix, method, responseClass, "", paging)
    }

    inline fun <R> runRestGet(urlPostfix: String, responseClass: Class<R>, paging: PaginationInfo?): R {
        return runRest(urlPostfix, HttpMethod.GET, responseClass, paging)
    }

    inline fun <R> runRestGet(urlPostfix: String, responseClass: Class<R>): R {
        return runRestGet(urlPostfix, responseClass, null)
    }

    inline fun <R> runRestDelete(urlPostfix: String, responseClass: Class<R>): R {
        return runRest(urlPostfix, HttpMethod.DELETE, responseClass, null)
    }

    inline fun <R> runRestPost(urlPostfix: String, responseClass: Class<R>, body: Any): R {
        return runRest(urlPostfix, HttpMethod.POST, responseClass, body, null)
    }

    inline fun <R> runRestPut(urlPostfix: String, responseClass: Class<R>, body: Any): R {
        return runRest(urlPostfix, HttpMethod.PUT, responseClass, body, null)
    }

    private fun getUrl(postfix: String) = baseUrl + postfix

    @JvmStatic fun getMapImagePostfix(mapId: String, isImage: Boolean) = MAP_URL + (if (isImage) MAP_IMAGE_URL else MAP_RELIEF_URL) + mapId

    @JvmStatic private fun checkToken(token: String?) {
        if (Strings.isNullOrEmpty(token)) {
            throw EmptyTokenException()
        }
    }

    @JvmStatic fun createPaging(fieldName: String, direction: Sort.Direction): PaginationInfo {
            val paging = PaginationInfo()
            paging.sortBy = fieldName
            paging.sortOrder = direction
            return paging
    }
}
