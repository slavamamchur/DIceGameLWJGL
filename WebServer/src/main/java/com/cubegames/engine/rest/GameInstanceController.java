package com.cubegames.engine.rest;

import com.cubegames.engine.domain.entities.GameInstance;
import com.cubegames.engine.domain.entities.players.InstancePlayer;
import com.cubegames.engine.domain.rest.requests.StartNewGameRequest;
import com.cubegames.engine.domain.rest.responses.GameInstanceResponse;
import com.cubegames.engine.domain.rest.responses.GameInstanceStartedResponse;
import com.cubegames.engine.domain.rest.responses.IdResponse;
import com.cubegames.engine.services.AbstractServiceDb;
import com.cubegames.engine.services.GameInstanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = RestConst.URL_GAME_INSTANCE, produces = MediaType.APPLICATION_JSON_VALUE)
public class GameInstanceController extends AbstractController<GameInstance> {

  @Autowired
  private GameInstanceService service;


  @Override
  protected AbstractServiceDb<GameInstance> getService() {
    return service;
  }


  @RequestMapping(method = RequestMethod.POST, value = RestConst.URL_GAME_INSTANCE_START)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public GameInstanceStartedResponse create(@RequestBody StartNewGameRequest request) {
    String tenantId = getTenantId();
    String gameId = request.getGameId();
    String name = request.getName();

    List<InstancePlayer> players = request.getPlayers();
    if (players == null) {
      players = service.findPlayersByIds(request.getPlayersId(), tenantId);
    }

    return new GameInstanceStartedResponse(service.startGame(gameId, players, name, tenantId));
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_GAME_INSTANCE_RESTART)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public IdResponse recreate(@PathVariable String instanceId) {
    return new IdResponse(service.restartGame(instanceId, getTenantId()));
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_GAME_INSTANCE_MOVE)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public GameInstance move(@PathVariable String instanceId, @PathVariable int steps) {
    return service.newMove(instanceId, steps, getTenantId());
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_GAME_INSTANCE_FINISH)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public GameInstanceResponse finish(@PathVariable String instanceId) {
    return new GameInstanceResponse(service.finishGame(instanceId, getTenantId()));
  }


  @Override
  protected Set<RestUsage> getRestUsageOperations() {
    Set<RestUsage> res = super.getRestUsageOperations();
    res.remove(RestUsage.CREATE);
    res.remove(RestUsage.NEW);
    res.remove(RestUsage.UPDATE);
    return res;
  }
}
