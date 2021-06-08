package com.cubegames.engine.services;

import com.cubegames.engine.dao.AbstractDao;
import com.cubegames.engine.dao.GameInstanceDao;
import com.cubegames.engine.domain.entities.DbPlayer;
import com.cubegames.engine.domain.entities.Game;
import com.cubegames.engine.domain.entities.GameInstance;
import com.cubegames.engine.domain.entities.GameInstance.State;
import com.cubegames.engine.domain.entities.players.InstancePlayer;
import com.cubegames.engine.domain.entities.points.AbstractGamePoint;
import com.cubegames.engine.domain.entities.points.AbstractGamePointFly;
import com.cubegames.engine.exceptions.BasicException;
import com.cubegames.engine.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameInstanceService extends AbstractServiceDb<GameInstance> {

  private static final int STEPS_MIN = 1;
  private static final int STEPS_MAX = 6;

  @Autowired
  GameInstanceDao dao;

  @Autowired
  GameService gameService;

  @Autowired
  DbPlayerService dbPlayerService;


  @Override
  public Class<GameInstance> getDomain() {
    return GameInstance.class;
  }

  @Override
  public AbstractDao<GameInstance> getDao() {
    return dao;
  }


  public GameInstance startGame(String gameId, List<InstancePlayer> players, String name, String tenantId) {
    if (Utils.stringIsEmpty(gameId)) {
      throw new BasicException("GameId cannot be empty");
    }

    if (players == null || players.size() <= 0) {
      throw new BasicException("Player list is empty");
    }

    Game game = gameService.get(gameId, tenantId);
    if (game == null) {
      throw new BasicException("No game with id: " + gameId);
    }

    if (game.isDeleted()) {
      throw new BasicException("Game is deleted: " + gameId);
    }

    gameService.validate(game);

    long ts = System.currentTimeMillis();
    String instanceName = name;
    if (Utils.stringIsEmpty(instanceName)) {
      instanceName = game.getName();
    }

    GameInstance instance = new GameInstance();
    instance.setGame(game);
    instance.setName(instanceName); // + "_" + Utils.longToDateTime(ts));
    instance.setCurrentPlayer(0);
    instance.setPlayers(players);
    instance.setState(State.WAIT);
    instance.setStepsToGo(0);
    instance.setStartedDate(ts);
    instance.setLastUsedDate(ts);
    resetPlayers(instance);
    return dao.saveAndReturn(instance, tenantId);
  }


  public String restartGame(String instanceId, String tenantId) {
    Utils.checkNotNullOrEmpty(instanceId, "instanceId cannot be empty");
    GameInstance instance = dao.get(instanceId, tenantId);
    Utils.checkNotNull(instance, "Wrong instanceId");

    instance.setState(State.WAIT);
    instance.setCurrentPlayer(0);
    instance.setStepsToGo(0);
    instance.setLastUsedDate(System.currentTimeMillis());

    resetPlayers(instance);

    return dao.save(instance, tenantId);
  }


  public GameInstance finishGame(String instanceId, String tenantId) {
    GameInstance instance = tryLoadGameInstance(instanceId, tenantId);

    instance.setState(State.FINISHED);
    instance.setLastUsedDate(System.currentTimeMillis());
    dao.save(instance, tenantId);

    return instance;
  }


  /**
   * Making new move on client's request
   * @param instanceId - current instance id
   * @param steps - number on the dice cube
   * @param tenantId - current user id
   * @return current instance info
   */
  public GameInstance newMove(String instanceId, int steps, String tenantId) {
    GameInstance instance = tryLoadGameInstance(instanceId, tenantId);
    instance.setLastUsedDate(System.currentTimeMillis());

    if (steps < STEPS_MIN) {
      steps = STEPS_MIN;
    }

    if (steps > STEPS_MAX) {
      steps = STEPS_MAX;
    }

    switch (instance.getState()) {
      case WAIT:
        return handleStateWait(instance, steps, tenantId);
      case MOVING:
        if (instance.getStepsToGo() > 0) {
          return handleStateMovingN(instance, tenantId);
        } else {
          return handleStateMoving0(instance, tenantId);
        }
      case FINISHED:
        throw new BasicException("This game is finished already: " + instanceId);
      default:
        throw new BasicException("Unsupported state: " + instance.getState());
    }
  }


  private GameInstance handleStateWait(GameInstance instance, int steps, String tenantId) {
    InstancePlayer player = getCurrentPlayer(instance);
    if (player.isSkipped() || player.isFinished()) {
      int index = getNextPlayerIndex(instance);
      instance.setCurrentPlayer(index);
      instance.setStepsToGo(0);
      instance.setState(State.WAIT);
      player.setSkipped(false);
    } else {
      // player is not skipped
      instance.setStepsToGo(steps);
      instance.setState(State.MOVING);
    }

    checkInstanceForFinished(instance);
    dao.save(instance, tenantId);
    return instance;
  }


  private GameInstance handleStateMoving0(GameInstance instance, String tenantId) {
    AbstractGamePoint currentPoint = getCurrentPoint(instance);
    InstancePlayer currentPlayer = getCurrentPlayer(instance);

    switch (currentPoint.getType()) {
      case START:
      case REGULAR:
        // no special point now - set next player as active
        int index = getNextPlayerIndex(instance);
        instance.setCurrentPlayer(index);
        instance.setState(State.WAIT);
        break;

      case MOVE_SKIP:
        currentPlayer.setSkipped(true);
        index = getNextPlayerIndex(instance);
        instance.setCurrentPlayer(index);
        instance.setState(State.WAIT);
        break;

      case FINISH:
        currentPlayer.setFinished(true);
        index = getNextPlayerIndex(instance);
        instance.setCurrentPlayer(index);
        instance.setState(State.WAIT);
        break;

      case MOVE_MORE:
        // additional move for current player
        instance.setState(State.WAIT);
        break;

      case FLY_BACK:
      case FLY_FORWARD:
        // move active player from special point to new position
        if (currentPoint instanceof AbstractGamePointFly) {
          AbstractGamePointFly flyPoint = (AbstractGamePointFly) currentPoint;
          int newPointIndex = flyPoint.getFlyToPoint();
          currentPlayer.setCurrentPoint(newPointIndex);

          index = getNextPlayerIndex(instance);
          instance.setCurrentPlayer(index);
          instance.setState(State.WAIT);
        } else {
          throw new BasicException("Wrong type of point");
        }
        break;

      default:
        throw new BasicException("Unknown type of point: " + currentPoint.getType());
    }  // switch

    checkInstanceForFinished(instance);
    dao.save(instance, tenantId);
    return instance;
  }


  private int getNextPlayerIndex(GameInstance instance) {
    int index = instance.getCurrentPlayer();
    int count = 0;
    List<InstancePlayer> players = instance.getPlayers();
    int playersCount = players.size();

    while (true) {
      count++;

      if (index >= playersCount - 1) {
        index = 0;  // go to beginning of list
      } else {
        index++; // go to next player in list
      }

      if (count > playersCount) {
        break;  // in case of all users are finished
      }

      InstancePlayer player = players.get(index);
      if (player.isFinished()) { // skip player in case he is finished
        continue;
      }

      break; // yahoo! we found next not-finished player!
    }

    return index;
  }


  private GameInstance handleStateMovingN(GameInstance instance, String tenantId) {
    // decrease steps to go
    int steps = instance.getStepsToGo() - 1;
    if (steps < STEPS_MIN) {
      steps = 0;
    }
    instance.setStepsToGo(steps);

    // move active player to next point
    // get current active player's point, then get next point
    InstancePlayer currentPlayer = getCurrentPlayer(instance);
    AbstractGamePoint currentPoint = getCurrentPoint(instance);
    List<AbstractGamePoint> points = instance.getGame().getGamePoints();
    int nextPointIndex;

    switch (currentPoint.getType()) {
      case START:
      case REGULAR:
      case FLY_BACK:
      case FLY_FORWARD:
      case MOVE_MORE:
      case MOVE_SKIP:
        nextPointIndex = currentPoint.getNextPointIndex();
        break;
      case FINISH:
        nextPointIndex = points.indexOf(currentPoint);
        instance.setStepsToGo(0);
        break;
      default:
        throw new BasicException("Unknown Point Type: " + currentPoint.getType());
    }

    if (nextPointIndex >= points.size()) {
      throw new BasicException("Wrong Index of next point: " + nextPointIndex + " of " + points.size());
    }

    currentPlayer.setCurrentPoint(nextPointIndex);

    checkInstanceForFinished(instance);
    dao.save(instance, tenantId);
    return instance;
  }


  private GameInstance tryLoadGameInstance(String instanceId, String tenantId) {
    if (Utils.stringIsEmpty(instanceId)) {
      throw new BasicException("InstanceId cannot be empty");
    }

    GameInstance instance = dao.get(instanceId, tenantId);
    if (instance == null) {
      throw new BasicException("Cannot find Game Instance with id: " + instanceId);
    }

    return instance;
  }

  private InstancePlayer getCurrentPlayer(GameInstance instance) {
    return instance.getPlayers().get(instance.getCurrentPlayer());
  }

  private AbstractGamePoint getCurrentPoint(GameInstance instance) {
    InstancePlayer player = getCurrentPlayer(instance);
    Game game = instance.getGame();
    List<AbstractGamePoint> points = game.getGamePoints();
    return points.get(player.getCurrentPoint());
  }


  private void resetPlayers(GameInstance instance) {
    for (InstancePlayer player : instance.getPlayers()) {
      player.setCurrentPoint(0);
      player.setFinished(false);
      player.setSkipped(false);
    }
  }

  public List<InstancePlayer> findPlayersByIds(List<String> ids, String tenantId) {
    if (ids == null || ids.size() <= 0) {
      return null;
    }

    List<InstancePlayer> res = new ArrayList<InstancePlayer>();
    for (String id : ids) {
      if (Utils.stringIsEmpty(id)) {
        continue;
      }

      DbPlayer dbPlayer = dbPlayerService.get(id, tenantId);
      if (dbPlayer != null) {
        InstancePlayer player = new InstancePlayer();
        player.setName(dbPlayer.getName());
        player.setColor(dbPlayer.getColor());
        res.add(player);
      }
    }

    return res;
  }


  private void checkInstanceForFinished(GameInstance instance) {
    List<InstancePlayer> players = instance.getPlayers();

    for (InstancePlayer player : players) {
      if (!player.isFinished()) {
        return;
      }
    }

    instance.setState(State.FINISHED);
  }

}
