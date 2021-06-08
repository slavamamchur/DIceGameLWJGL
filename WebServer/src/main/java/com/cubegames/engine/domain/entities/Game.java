package com.cubegames.engine.domain.entities;

import com.cubegames.engine.domain.entities.points.AbstractGamePoint;
import com.cubegames.engine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Game extends BasicSizedNamedDbEntity {

  public static final String FIELD_CREATED_DATE = "createdDate";

  private List<AbstractGamePoint> gamePoints;
  private String mapId;
  private long createdDate;
  private boolean drawGamePoints;

  @Override
  public int calculateEntitySize() {
    return calculateCollectionSize(gamePoints);
  }

  public List<AbstractGamePoint> getGamePoints() {
    return gamePoints;
  }

  public void setGamePoints(List<AbstractGamePoint> gamePoints) {
    this.gamePoints = gamePoints;
  }

  public String getMapId() {
    return mapId;
  }

  public void setMapId(String mapId) {
    this.mapId = mapId;
  }

  public void addPoint(AbstractGamePoint point) {
    Utils.checkNotNull(point);

    if (gamePoints == null) {
      gamePoints = new ArrayList<AbstractGamePoint>();
    }

    gamePoints.add(point);
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }

  public boolean isDrawGamePoints() {
    return drawGamePoints;
  }

  public void setDrawGamePoints(boolean drawGamePoints) {
    this.drawGamePoints = drawGamePoints;
  }
}
