package com.sadgames.gl3dengine.glrender;

import com.sadgames.gl3dengine.gamelogic.GameEventsCallbackInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.sysutils.common.GdxDbInterface;
import com.sadgames.dicegame.desktop.DesktopGdxDbWrapper;
import com.sadgames.gl3dengine.manager.GDXPreferences;
import com.sadgames.gl3dengine.manager.SettingsManagerInterface;
import com.sadgames.vulkan.newclass.audio.OpenALLwjglAudio;

public class GdxExt {

    public static GdxDbInterface dataBase = DesktopGdxDbWrapper.INSTANCE;
    public static SettingsManagerInterface preferences = GDXPreferences.INSTANCE;
    public static int width = -1;
    public static int height = -1;
    public static OpenALLwjglAudio audio;
    public static RestApiInterface restAPI;
    public static GameEventsCallbackInterface gameLogic;

}
