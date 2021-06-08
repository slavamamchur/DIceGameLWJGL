package com.cubegames.engine.domain.entities.points;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

import javax.vecmath.Vector2f;

import static com.cubegames.engine.consts.RestCommonConsts.URL_GAME_POINT;

/**
 * Abstract game point
 */

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "_class")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GamePointFinish.class, name = "com.cubegames.engine.domain.entities.points.GamePointFinish"),
    @JsonSubTypes.Type(value = GamePointStart.class, name = "com.cubegames.engine.domain.entities.points.GamePointStart"),
    @JsonSubTypes.Type(value = GamePointFlyBack.class, name = "com.cubegames.engine.domain.entities.points.GamePointFlyBack"),
    @JsonSubTypes.Type(value = GamePointFlyForward.class, name = "com.cubegames.engine.domain.entities.points.GamePointFlyForward"),
    @JsonSubTypes.Type(value = GamePointMoveMore.class, name = "com.cubegames.engine.domain.entities.points.GamePointMoveMore"),
    @JsonSubTypes.Type(value = GamePointMoveSkip.class, name = "com.cubegames.engine.domain.entities.points.GamePointMoveSkip"),
    @JsonSubTypes.Type(value = GamePointRegular.class, name = "com.cubegames.engine.domain.entities.points.GamePointRegular")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractGamePoint implements Serializable {

  private static final long serialVersionUID = 593713937939910727L;

  public static String urlForActionName(){
    return URL_GAME_POINT;
  }

  public int xPos;
  public int yPos;
  public PointType type;
  public int nextPointIndex;

  public int getxPos() {
    return xPos;
  }

  public void setxPos(int xPos) {
    this.xPos = xPos;
  }

  public int getyPos() {
    return yPos;
  }

  public void setyPos(int yPos) {
    this.yPos = yPos;
  }

  public PointType getType() {
    return type;
  }

  public void setType(PointType type) {
    this.type = type;
  }

  public int getNextPointIndex() {
    return nextPointIndex;
  }

  public void setNextPointIndex(int nextPointIndex) {
    this.nextPointIndex = nextPointIndex;
  }

  public Vector2f asVector2f() {
    return new Vector2f(xPos, yPos);
  }

  public Vector2f asVector2fLua(float scaleFactor) {
    return new Vector2f(xPos * scaleFactor, yPos * scaleFactor);
  }

}
