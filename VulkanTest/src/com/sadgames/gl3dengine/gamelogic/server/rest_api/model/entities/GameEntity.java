package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.cubegames.engine.domain.entities.points.AbstractGamePoint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.gamelogic.client.GameConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.GameController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.items.InteractiveGameItem;
import com.sadgames.gl3dengine.glrender.GLRenderConsts;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.MaterialPropertiesObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject.MOVING_OBJECT;
import static com.sadgames.sysutils.common.CommonUtils.getResourceStream;

@JsonIgnoreProperties(ignoreUnknown = true) //todo: add gameLogicScript field, read textures via getImage()
public class GameEntity extends BasicNamedDbEntity {

    private static final long serialVersionUID = -4698835803284111481L;

    public static float GAME_DICE_HALF_SIZE = 0.15f;
    public static short BOX_SHAPE_TYPE = 1;
    public static short UNKNOWN_SHAPE_TYPE = 0;
    private static final float DICE_DEFAULT_WEIGHT = 10f;
    public static final String ON_DICE_OBJECT_INIT = "onDiceObjectInit";

    protected List<AbstractGamePoint> gamePoints;
    public String mapId;
    public long createdDate;
    private boolean drawGamePoints;

    protected List<InteractiveGameItem> gameItems = null; //todo:
    protected List<String> gameSounds = null;

    public GameEntity() {}

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

    public boolean isDrawGamePoints() {
        return drawGamePoints;
    }
    public void setDrawGamePoints(boolean drawGamePoints) {
        this.drawGamePoints = drawGamePoints;
    }

    public void addPoint(AbstractGamePoint point) {
        if(point == null) return;

        if (gamePoints == null) {
            gamePoints = new ArrayList<>();
        }

        gamePoints.add(point);
    }

    public long getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String actionURL() {
        return RestConst.URL_GAME;
    }

    @Override
    public AbstractController getController() {
        return new GameController();
    }

    /** for game init script from web api object -------------------------------------------------*/
    public Vector3f _getGravity() {
        return new Vector3f(0f, -9.8f, 0f);
    }

    public Vector3f _getStartSunPosition() {
        return new Vector3f(-2.2f, 1.2f, -3.2f); //y=1.7f
    }
    public Vector3f _getStartSunColor() {
        return new Vector3f(1.0f, 1.0f, 0.8f);
    }

    public InputStream getLuaScript(String name) {
        return getResourceStream("/scripts/" + name + ".lua");
    }

    //TODO: replace with REST object
    public List<String> getGameSounds() {
        if (gameSounds == null) {
            gameSounds = new ArrayList<String>(){
                {
                    add("rolling_dice.mp3");
                }
            };
        }

        return gameSounds;
    }

    //TODO: replace with REST object
    public List<InteractiveGameItem> getGameItems() {
        if (gameItems == null) {
            gameItems = new ArrayList<>();

            MaterialPropertiesObject material = new MaterialPropertiesObject(0.4f, 1.0f, 0.9f,
                    GameConst.DICE_MESH_OBJECT_1 /*+ COMPRESSED_TEXTURE_FILE_EXT*/, null, null, null);

            InteractiveGameItem dice = createNewItem(GameConst.DICE_MESH_OBJECT_1,
                                                     GameConst.TERRAIN_MESH_OBJECT,
                                                     new Vector3f(0f, 0.08f, 0f),
                                                     GAME_DICE_HALF_SIZE,
                                                     false,
                                                     BOX_SHAPE_TYPE,
                                                     DICE_DEFAULT_WEIGHT,
                                                     MOVING_OBJECT,
                                                     TERRAIN_OBJECT,
                                                     ON_DICE_OBJECT_INIT,
                                                     material);

            gameItems.add(dice);
        }

        return gameItems;
    }

    @SuppressWarnings("unused")
    public InteractiveGameItem createNewItem(String itemName,
                                             String itemParentName,
                                             float mass,
                                             int tag,
                                             int materialID) {
        return createNewItem(itemName,
                             itemParentName,
                             new Vector3f(0f, 0f, 0f),
                             1f,
                             false,
                             UNKNOWN_SHAPE_TYPE,
                             mass,
                             tag,
                             GLRenderConsts.GLObjectType.values()[materialID],
                             null,
                                     null);
    }

    public InteractiveGameItem createNewItem(String itemName,
                                             String itemParentName,
                                             Vector3f pos,
                                             float initialScale,
                                             boolean hasTwoSidedSurface,
                                             short collisionShapeType,
                                             float mass,
                                             int tag,
                                             GLRenderConsts.GLObjectType type,
                                             String onInitEventHandler,
                                             MaterialPropertiesObject material) {

        return new InteractiveGameItem(itemName,
                                       itemParentName,
                                       pos,
                                       initialScale,
                                       hasTwoSidedSurface,
                                       collisionShapeType,
                                       mass,
                                       tag,
                                       type,
                                       onInitEventHandler,
                                       material);
    }
}
