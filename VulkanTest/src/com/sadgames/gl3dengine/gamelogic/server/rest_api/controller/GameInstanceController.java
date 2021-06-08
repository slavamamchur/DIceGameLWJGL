package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.params.StartNewGameRequestParam;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceCollectionResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceStartedResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.IdResponse;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE_FINISH;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE_MOVE;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE_RESTART;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE_START;

public class GameInstanceController extends AbstractController {

    public GameInstanceController() {
        super(  URL_GAME_INSTANCE,
                GameInstanceEntity.class,
                GameInstanceCollectionResponse.class,
                AbstractController.HTTP_METHOD_GET);
    }

    public GameInstanceResponse finishInstance(GameInstanceEntity instance) {
        return (GameInstanceResponse) getController().iGetResponseWithGetParams(
                URL_GAME_INSTANCE_FINISH,
                null,
                GameInstanceResponse.class,
                instance.getId());
    }

    public IdResponse restartInstance(GameInstanceEntity instance) {
        return (IdResponse) getController().iGetResponseWithGetParams(
                URL_GAME_INSTANCE_RESTART,
                null,
                IdResponse.class,
                instance.getId());
    }

    public GameInstanceStartedResponse startNewInstance(StartNewGameRequestParam request) {
        return (GameInstanceStartedResponse) getController().iGetResponseWithPostParams(
                URL_GAME_INSTANCE_START,
                request,
                GameInstanceStartedResponse.class);
    }

    public GameInstanceEntity makeTurn(GameInstanceEntity instance) {
        return (GameInstanceEntity) getController().iGetResponseWithGetParams(
                URL_GAME_INSTANCE_MOVE,
                null,
                GameInstanceEntity.class,
                instance.getId(),
                instance.getStepsToGo());
    }

}
