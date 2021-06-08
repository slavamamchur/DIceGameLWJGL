package com.cubegames.engine.rest;

import com.cubegames.engine.domain.entities.Game;
import com.cubegames.engine.domain.entities.points.AbstractGamePoint;
import com.cubegames.engine.domain.entities.points.GamePointFinish;
import com.cubegames.engine.domain.entities.points.GamePointFlyBack;
import com.cubegames.engine.domain.entities.points.GamePointFlyForward;
import com.cubegames.engine.domain.entities.points.GamePointMoveMore;
import com.cubegames.engine.domain.entities.points.GamePointMoveSkip;
import com.cubegames.engine.domain.entities.points.GamePointRegular;
import com.cubegames.engine.domain.entities.points.GamePointStart;
import com.cubegames.engine.domain.entities.points.PointType;
import com.cubegames.engine.domain.rest.requests.NewPointRequest;
import com.cubegames.engine.exceptions.ValidationBasicException;
import com.cubegames.engine.services.AbstractServiceDb;
import com.cubegames.engine.services.GameService;
import com.cubegames.engine.utils.Utils;

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


@Controller
@RequestMapping(value = RestConst.URL_GAME, produces = MediaType.APPLICATION_JSON_VALUE)
public class GameController extends AbstractController<Game> {

  @Autowired
  private GameService service;

  @Override
  protected AbstractServiceDb<Game> getService() {
    return service;
  }


  @RequestMapping(method = RequestMethod.POST, value = RestConst.URL_GAME_NEW_POINT_EX)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void newPointPost(@RequestBody NewPointRequest request) {
    Utils.checkNotNull(request, "New Point request cannot be empty!");
    newPointInternal(request.getGameId(), request.getxPos(), request.getyPos(), request.getType(), request.getNextIndex(),
        request.getFlyIndex());
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_GAME_NEW_POINT)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void newPoint(@PathVariable String gameId,
                       @PathVariable int xPos,
                       @PathVariable int yPos,
                       @PathVariable PointType type,
                       @PathVariable int nextIndex,
                       @PathVariable int flyIndex) {
    newPointInternal(gameId, xPos, yPos, type, nextIndex, flyIndex);
  }

  private void newPointInternal(String gameId, int xPos, int yPos, PointType type, int nextIndex, int flyIndex) {
    String tenantId = getTenantId();
    Game game = service.get(gameId, tenantId);
    Utils.checkNotNull(game);

    AbstractGamePoint point;
    switch (type) {
      case START:
        point = new GamePointStart();
        break;
      case REGULAR:
        point = new GamePointRegular();
        break;
      case MOVE_SKIP:
        point = new GamePointMoveSkip();
        break;
      case MOVE_MORE:
        point = new GamePointMoveMore();
        break;
      case FLY_FORWARD:
        point = new GamePointFlyForward();
        ((GamePointFlyForward) point).setFlyToPoint(flyIndex);
        break;
      case FLY_BACK:
        point = new GamePointFlyBack();
        ((GamePointFlyBack) point).setFlyToPoint(flyIndex);
        break;
      case FINISH:
        point = new GamePointFinish();
        break;
      default:
        throw new ValidationBasicException("Unknown point type: " + type);
    }

    point.setNextPointIndex(nextIndex);
    point.setxPos(xPos);
    point.setyPos(yPos);

    game.addPoint(point);
    service.saveExisting(game, tenantId);
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_GAME_REMOVE_POINT)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void removePoint(@PathVariable String gameId,
                          @PathVariable int index) {
    String tenantId = getTenantId();
    Game game = service.get(gameId, tenantId);
    Utils.checkNotNull(game);

    List<AbstractGamePoint> points = game.getGamePoints();
    if (points != null && points.size() > index) {
      points.remove(index);
      service.saveExisting(game, tenantId);
    }
  }

}
