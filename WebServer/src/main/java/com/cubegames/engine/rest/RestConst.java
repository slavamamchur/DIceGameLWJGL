package com.cubegames.engine.rest;

public class RestConst {

  // main controller
  public static final String URL_PING = "/ping";
  public static final String URL_LOGIN = "/login";
  public static final String URL_REGISTER = "/register";

  // game controller
  public static final String URL_GAME = "/game";
  public static final String URL_GAME_NEW_POINT = "/{gameId}/point/{xPos}/{yPos}/{type}/{nextIndex}/{flyIndex}";
  public static final String URL_GAME_NEW_POINT_EX = "/{gameId}/point";
  public static final String URL_GAME_REMOVE_POINT = "/{gameId}/point/{index}";

  // instance controller
  public static final String URL_GAME_INSTANCE = "/instance";
  public static final String URL_GAME_INSTANCE_START = "/start";
  public static final String URL_GAME_INSTANCE_RESTART = "/restart/{instanceId}";
  public static final String URL_GAME_INSTANCE_MOVE = "/move/{instanceId}/{steps}";
  public static final String URL_GAME_INSTANCE_FINISH = "/finish/{instanceId}";

  // map controller
  public static final String URL_GAME_MAP = "/map";
  public static final String URL_GAME_MAP_IMAGE = "/image/{mapId}";
  public static final String URL_GAME_MAP_IMAGE_SMALL = "/image/small/{mapId}";
  public static final String URL_GAME_MAP_RELIEF = "/relief/{mapId}";
  public static final String URL_GAME_MAP_IMAGE_SIMPLE = "/upload";
  public static final String URL_GAME_MAP_IMAGE_RELIEF = "/relief";
  public static final String URL_GAME_MAP_IMAGE_JSON = "/file/image";
  public static final String URL_GAME_MAP_RELIEF_JSON = "/file/relief";

  // player controller
  public static final String URL_PLAYER = "/player";

  // common URLs
  public static final String URL_COUNT = "/count";
  public static final String URL_CREATE = "/create";
  public static final String URL_NEW = "/new";
  public static final String URL_FIND = "/find/{itemId}";
  public static final String URL_DELETE = "/delete/{itemId}";
  public static final String URL_UPDATE = "/update";
  public static final String URL_LIST = "/list";
}
