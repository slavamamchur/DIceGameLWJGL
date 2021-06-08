package com.sadgames.gl3dengine.gamelogic.client;

import com.sadgames.sysutils.common.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class GameConst {

    public static final String SEA_BOTTOM_TEXTURE     = "5bae5d26f2675fb54e7cb7dc";
    public static final String NORMALMAP_TEXTURE      = "5bae5be1f2675fb54e7cb7da";
    public static final String DUDVMAP_TEXTURE        = "5bae5df2f2675fb54e7cb7df";
    public static final String BLENDING_MAP_TEXTURE   = "BLENDING_MAP_TEXTURE";
    public static final String SKY_BOX_TEXTURE_NAME   = "SKY_BOX_CUBE_MAP_TEXTURE";
    public static final String SKY_DOME_TEXTURE_NAME   = "5bae5eb9f2675fb54e7cb7e8";
    public static final String MAP_BACKGROUND_TEXTURE_NAME = "5bae5f0cf2675fb54e7cb7ee";
    public static final String TERRAIN_ATLAS_TEXTURE_NAME = "terrainAtlas4.png";
    public static final String EARTH_DISP_MAP_TEXTURE_NAME = "Earth_Disp_Map.png";
    public static final String NORMALMAP_TERRAIN_ATLAS_TEXTURE_NAME = "normalTerrainAtlas2.png";
    public static final String DISTORTION_TERRAIN_ATLAS_TEXTURE_NAME = "distortionTerrainAtlas2.jpg";
    public static final String ROAD_TEXTURE_NAME = "stone.png";
    public static final String DIRT_TEXTURE_NAME = "dirt.png";
    public static final String GRASS_TEXTURE_NAME = "grass.png";
    public static final String SAND_TEXTURE_NAME = "sand.jpg";
    public static final String SUN_TEXTURE_NAME = "sun2.png";
    public static final String CUBE_TEXTURE_PREFIX = "CUBE_TEXTURE_";

    public static final String ROLLING_DICE_SOUND     = "rolling_dice.mp3";

    public static final String TERRAIN_MESH_OBJECT = "TERRAIN_MESH_OBJECT";
    public static final String WATER_MESH_OBJECT = "WATER_MESH_OBJECT";
    public static final String CHIP_MESH_OBJECT = "CHIP_MESH_OBJECT";
    public static final String DICE_MESH_OBJECT_1 = "5bae5fc2f2675fb54e7cb7f5";
    public static final String SKY_BOX_CUBE_MAP_OBJECT = "SKY_BOX_CUBE_MAP_OBJECT";
    public static final String MINI_MAP_OBJECT = "MINI_MAP_OBJECT";
    public static final String SUN_OBJECT = "SUN_OBJECT";

    public static final int    PATH_COLOR             = ColorUtils.argb(255, 0, 255, 0);
    public static final int    WAY_POINT_COLOR        = ColorUtils.argb(255, 255, 0, 0);

    public static final int    NO_POST_EFFECTS         = 0x00;
    public static final int    GOD_RAYS_POST_EFFECT    = 0x01;
    public static final int    CONTRAST_CHARGE_EFFECT  = 0x02;
    public static final int    BLUR_EFFECT             = 0x04;
    public static final int    BLOOM_EFFECT            = 0x10;
    public static final int    DOF_EFFECT              = 0x20;

    public static final String ACTION_LOGIN = "com.sadgames.dicegame.api.action.LOGIN";
    public static final String ACTION_RELOGIN = "com.sadgames.dicegame.api.action.RELOGIN";
    public static final String ACTION_LOGIN_RESPONSE = "com.sadgames.dicegame.api.action.LOGIN_RESPONSE";
    public static final String ACTION_RELOGIN_RESPONSE = "com.sadgames.dicegame.api.action.RELOGIN_RESPONSE";
    public static final String ACTION_REGISTRATION = "com.sadgames.dicegame.api.action.REGISTRATION";
    public static final String ACTION_REGISTRATION_RESPONSE = "com.sadgames.dicegame.api.action.REGISTRATION_RESPONSE";
    public static final String ACTION_PING = "com.sadgames.dicegame.api.action.PING";
    public static final String ACTION_PING_RESPONSE = "com.sadgames.dicegame.api.action.PING_RESPONSE";
    public static final String ACTION_GET_GAME_MAP_LIST = "com.sadgames.dicegame.api.action.GET_MAP_LIST";
    public static final String ACTION_LIST_RESPONSE = "com.sadgames.dicegame.api.action.LIST_RESPONSE";
    public static final String ACTION_GET_GAME_LIST = "com.sadgames.dicegame.api.action.GET_GAME_LIST";
    public static final String ACTION_GET_GAME_INSTANCE_LIST = "com.sadgames.dicegame.api.action.GET_GAME_INSTANCE_LIST";
    public static final String ACTION_GET_PLAYER_LIST = "com.sadgames.dicegame.api.action.GET_PLAYER_LIST";
    public static final String ACTION_PLAYER_LIST_RESPONSE = "com.sadgames.dicegame.api.action.PLAYER_LIST_RESPONSE";
    public static final String ACTION_GET_MAP_IMAGE = "com.sadgames.dicegame.api.action.GET_MAP_IMAGE";
    public static final String ACTION_MAP_IMAGE_RESPONSE = "com.sadgames.dicegame.api.action.MAP_IMAGE_RESPONSE";
    public static final String ACTION_UPLOAD_MAP_IMAGE = "com.sadgames.dicegame.api.action.UPLOAD_MAP_IMAGE";
    public static final String ACTION_UPLOAD_IMAGE_RESPONSE = "com.sadgames.dicegame.api.action.UPLOAD_IMAGE_RESPONSE";
    public static final String ACTION_DELETE_ENTITY = "com.sadgames.dicegame.api.action.DELETE_ENTITY";
    public static final String ACTION_DELETE_ENTITY_RESPONSE = "com.sadgames.dicegame.api.action.DELETE_ENTITY_RESPONSE";
    public static final String ACTION_SAVE_ENTITY = "com.sadgames.dicegame.api.action.SAVE_ENTITY";
    public static final String ACTION_SAVE_ENTITY_RESPONSE = "com.sadgames.dicegame.api.action.SAVE_ENTITY_RESPONSE";
    public static final String ACTION_FINISH_GAME_INSTANCE = "com.sadgames.dicegame.api.action.FINISH_GAME_INSTANCE";
    public static final String ACTION_FINISH_GAME_INSTANCE_RESPONSE = "com.sadgames.dicegame.api.action.FINISH_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_START_GAME_INSTANCE = "com.sadgames.dicegame.api.action.START_GAME_INSTANCE";
    public static final String ACTION_START_GAME_INSTANCE_RESPONSE = "com.sadgames.dicegame.api.action.START_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_RESTART_GAME_INSTANCE = "com.sadgames.dicegame.api.action.RESTART_GAME_INSTANCE";
    public static final String ACTION_RESTART_GAME_INSTANCE_RESPONSE = "com.sadgames.dicegame.api.action.RESTART_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_MOOVE_GAME_INSTANCE = "com.sadgames.dicegame.api.action.MOOVE_GAME_INSTANCE";
    public static final String ACTION_MOOVE_GAME_INSTANCE_RESPONSE = "com.sadgames.dicegame.api.action.MOOVE_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_REMOVE_CHILD = "com.sadgames.dicegame.api.action.ACTION_REMOVE_CHILD";
    public static final String ACTION_REMOVE_CHILD_RESPONSE = "com.sadgames.dicegame.api.action.ACTION_REMOVE_CHILD_RESPONSE";
    public static final String ACTION_ADD_CHILD = "com.sadgames.dicegame.api.action.ACTION_ADD_CHILD";
    public static final String ACTION_ADD_CHILD_RESPONSE = "com.sadgames.dicegame.api.action.ACTION_ADD_CHILD_RESPONSE";
    public static final String ACTION_SHOW_TURN_INFO = "com.sadgames.dicegame.api.action.ACTION_SHOW_TURN_INFO";
    public static final String ACTION_SHOW_ANIMATED_TEXT = "com.sadgames.dicegame.api.action.ACTION_SHOW_ANIMATED_TEXT";
    public static final String ACTION_REMOVE_LOADING_SPLASH = "com.sadgames.dicegame.api.action.ACTION_REMOVE_LOADING_SPLASH";

    public static final String EXTRA_USER_NAME = "USER_NAME";
    public static final String EXTRA_USER_PASS = "USER_PASS";
    public static final String EXTRA_LOGIN_RESPONSE_OBJECT = "LOGIN_RESPONSE_OBJECT";
    public static final String EXTRA_REGISTRATION_PARAMS_OBJECT = "LOGIN_RESPONSE_OBJECT";
    public static final String EXTRA_REGISTRATION_RESPONSE_TEXT = "REGISTRATION_RESPONSE_TEXT";
    public static final String EXTRA_BOOLEAN_RESULT = "BOOLEAN_RESULT";
    public static final String EXTRA_GAME_MAP_OBJECT = "GAME_MAP_OBJECT";
    public static final String EXTRA_GAME_MAP_FILE = "GAME_MAP_FILE";
    public static final String EXTRA_GAME_MAP_LIST = "GAME_MAP_LIST";
    public static final String EXTRA_GAME_INSTANCE_LIST = "GAME_INSTANCE_LIST";
    public static final String EXTRA_GAME_INSTANCE = "GAME_INSTANCE";
    public static final String EXTRA_GAME_LIST = "GAME_LIST";
    public static final String EXTRA_PLAYER_LIST = "PLAYER_LIST";
    public static final String EXTRA_ENTITY_OBJECT = "ENTITY_OBJECT";
    public static final String EXTRA_ERROR_OBJECT = "ERROR_OBJECT";
    public static final String EXTRA_RESPONSE_ACTION = "RESPONSE_ACTION";
    public static final String EXTRA_PARENT_ID = "PARENT_ID";
    public static final String EXTRA_CHILD_NAME = "CHILD_NAME";
    public static final String EXTRA_CHILD_INDEX = "CHILD_INDEX";
    public static final String EXTRA_DICE_VALUE = "DICE_VALUE";
    public static final String EXTRA_ANIMATED_TEXT = "ANIMATED_TEXT";

    public static final String ON_BEFORE_DRAW_FRAME_EVENT_HANDLER = "beforeDrawFrame";
    public static final String ON_ROLLING_OBJECT_START_EVENT_HANDLER = "onRollingObjectStart";
    public static final String ON_ROLLING_OBJECT_STOP_EVENT_HANDLER = "onRollingObjectStop";
    public static final String ON_MOVING_OBJECT_STOP_EVENT_HANDLER = "onMovingObjectStop";
    public static final String ON_PLAY_TURN_EVENT_HANDLER = "onPlayTurn";
    public static final String ON_PLAYER_MAKE_TURN_EVENT_HANDLER = "onPlayerMakeTurn";
    public static final String ON_PLAYER_CONTINUE_TURN_EVENT_HANDLER = "onPlayerContinueTurn";
    public static final String ON_PLAYER_NEXT_MOVE__EVENT_HANDLER = "playerNextMove";
    public static final String ON_CREATE_DYNAMIC_ITEMS_HANDLER = "onCreateDynamicItems";
    public static final String ON_GAME_RESTARTED_EVENT_HANDLER = "onGameRestarted";
    public static final String ON_PREPARE_MAP_TEXTURE_EVENT_HANDLER = "drawPath";
    public static final String ON_CREATE_REFLECTION_MAP_EVENT_HANDLER = "onCreateReflectionMap";
    public static final String ON_INIT_CAMERA_EVENT_HANDLER = "onCameraInit";


    public static final List<String> ACTION_LIST = new ArrayList<String>() {{
        add(ACTION_LOGIN);
        add(ACTION_LOGIN_RESPONSE);
        add(ACTION_RELOGIN);
        add(ACTION_RELOGIN_RESPONSE);
        add(ACTION_REGISTRATION);
        add(ACTION_REGISTRATION_RESPONSE);
        add(ACTION_PING);
        add(ACTION_PING_RESPONSE);
        add(ACTION_GET_GAME_MAP_LIST);
        add(ACTION_LIST_RESPONSE);
        add(ACTION_GET_GAME_LIST);
        add(ACTION_GET_GAME_INSTANCE_LIST);
        add(ACTION_GET_PLAYER_LIST);
        add(ACTION_PLAYER_LIST_RESPONSE);
        add(ACTION_GET_MAP_IMAGE);
        add(ACTION_MAP_IMAGE_RESPONSE);
        add(ACTION_UPLOAD_MAP_IMAGE);
        add(ACTION_UPLOAD_IMAGE_RESPONSE);
        add(ACTION_DELETE_ENTITY);
        add(ACTION_DELETE_ENTITY_RESPONSE);
        add(ACTION_SAVE_ENTITY);
        add(ACTION_SAVE_ENTITY_RESPONSE);
        add(ACTION_FINISH_GAME_INSTANCE);
        add(ACTION_FINISH_GAME_INSTANCE_RESPONSE);
        add(ACTION_START_GAME_INSTANCE);
        add(ACTION_START_GAME_INSTANCE_RESPONSE);
        add(ACTION_RESTART_GAME_INSTANCE);
        add(ACTION_RESTART_GAME_INSTANCE_RESPONSE);
        add(ACTION_MOOVE_GAME_INSTANCE);
        add(ACTION_MOOVE_GAME_INSTANCE_RESPONSE);
        add(ACTION_REMOVE_CHILD);
        add(ACTION_REMOVE_CHILD_RESPONSE);
        add(ACTION_ADD_CHILD);
        add(ACTION_ADD_CHILD_RESPONSE);
        add(ACTION_SHOW_TURN_INFO);
        add(ACTION_SHOW_ANIMATED_TEXT);
        add(ACTION_REMOVE_LOADING_SPLASH);
    }};
    
    public enum UserActionType {
        LOGIN,
        LOGIN_RESPONSE,
        RELOGIN,
        RELOGIN_RESPONSE,
        REGISTRATION,
        REGISTRATION_RESPONSE,
        PING,
        PING_RESPONSE,
        GET_GAME_MAP_LIST,
        LIST_RESPONSE,
        GET_GAME_LIST,
        GET_GAME_INSTANCE_LIST,
        GET_PLAYER_LIST,
        PLAYER_LIST_RESPONSE,
        GET_MAP_IMAGE,
        MAP_IMAGE_RESPONSE,
        UPLOAD_MAP_IMAGE,
        UPLOAD_IMAGE_RESPONSE,
        DELETE_ENTITY,
        DELETE_ENTITY_RESPONSE,
        SAVE_ENTITY,
        SAVE_ENTITY_RESPONSE,
        FINISH_GAME_INSTANCE,
        FINISH_GAME_INSTANCE_RESPONSE,
        START_GAME_INSTANCE,
        START_GAME_INSTANCE_RESPONSE,
        RESTART_GAME_INSTANCE,
        RESTART_GAME_INSTANCE_RESPONSE,
        MOOVE_GAME_INSTANCE,
        MOOVE_GAME_INSTANCE_RESPONSE,
        REMOVE_CHILD,
        REMOVE_CHILD_RESPONSE,
        ADD_CHILD,
        ADD_CHILD_RESPONSE,
        SHOW_TURN_INFO,
        SHOW_ANIMATED_TEXT,
        REMOVE_LOADING_SPLASH
    }

    public enum GameState { WAIT, MOVING, FINISHED }
}
