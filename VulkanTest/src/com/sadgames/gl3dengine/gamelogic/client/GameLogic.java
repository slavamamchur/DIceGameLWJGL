package com.sadgames.gl3dengine.gamelogic.client;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.cubegames.engine.domain.entities.players.InstancePlayer;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.sadgames.gl3dengine.gamelogic.GameEventsCallbackInterface;
import com.sadgames.gl3dengine.gamelogic.client.entities.GameMap;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.items.InteractiveGameItem;
import com.sadgames.gl3dengine.glrender.GLRendererInterface;
import com.sadgames.gl3dengine.glrender.GdxExt;
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;
import com.sadgames.gl3dengine.glrender.scene.fbo.AbstractFBO;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractSkyObject;
import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject;
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem;
import com.sadgames.gl3dengine.glrender.scene.objects.SkyDomeObject;
import com.sadgames.gl3dengine.glrender.scene.objects.SunObject;
import com.sadgames.gl3dengine.glrender.scene.objects.WaterObject;
import com.sadgames.gl3dengine.glrender.scene.objects.generated.ForestGenerator;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapWrapper;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.GlPixmap;
import com.sadgames.gl3dengine.manager.SettingsManagerInterface;
import com.sadgames.gl3dengine.manager.TextureCache;
import com.sadgames.gl3dengine.physics.PhysicalWorld;
import com.sadgames.sysutils.common.CommonUtils;
import com.sadgames.sysutils.common.LuaUtils;
import com.sadgames.vulkan.newclass.audio.OpenALSound;
import com.sadgames.vulkan.newclass.images.Gdx2DPixmap;
import com.sadgames.vulkan.newclass.images.Pixmap;

import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Platform;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec4.Vec4;
import imgui.Cond;
import imgui.ImGui;
import imgui.StyleVar;
import imgui.WindowFlag;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import uno.glfw.GlfwMonitor;
import uno.glfw.GlfwWindow;

import static com.sadgames.gl3dengine.gamelogic.client.GameConst.GameState;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.MAP_BACKGROUND_TEXTURE_NAME;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_BEFORE_DRAW_FRAME_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_CREATE_DYNAMIC_ITEMS_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_CREATE_REFLECTION_MAP_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_GAME_RESTARTED_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_INIT_CAMERA_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_MOVING_OBJECT_STOP_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_PLAYER_MAKE_TURN_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_PLAYER_NEXT_MOVE__EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_PLAY_TURN_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_PREPARE_MAP_TEXTURE_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_ROLLING_OBJECT_START_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_ROLLING_OBJECT_STOP_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ROAD_TEXTURE_NAME;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.SAND_TEXTURE_NAME;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.SKY_BOX_CUBE_MAP_OBJECT;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.SKY_DOME_TEXTURE_NAME;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.SUN_OBJECT;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.TERRAIN_ATLAS_TEXTURE_NAME;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.TERRAIN_MESH_OBJECT;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.WATER_MESH_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.FOREST_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.TERRAIN_OBJECT_32;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Z;
import static com.sadgames.gl3dengine.manager.LensManagerKt.generateLens;
import static com.sadgames.sysutils.common.CommonUtils.getResourceStream;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;
import static com.sadgames.sysutils.common.CommonUtils.waitForGC;
import static com.sadgames.sysutils.common.LuaUtils.javaList2LuaTable;
import static com.sadgames.sysutils.common.MathUtils.mulMatOnVec;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

public class GameLogic implements GameEventsCallbackInterface, ResourceFinder {

    private static final String LUA_GAME_LOGIC_SCRIPT = "gameLogic";

    private GameMapEntity mapEntity;
    private GameEntity gameEntity;
    private GameInstanceEntity gameInstanceEntity;
    private List<InstancePlayer> savedPlayers;
    private Map<String, OpenALSound> soundCache = new HashMap<>();
    private Globals luaEngine;
    private int prev_player_index;

    public GameLogic(String instanceId, RestApiInterface restAPI) {
        GdxExt.gameLogic = this;
        GdxExt.restAPI = restAPI;

        if (checkLogin(restAPI)) {
            GameInstanceEntity gameInst = restAPI.iGetGameInstanceEntity(instanceId);
            gameInstanceEntity = gameInst;
            savedPlayers = new ArrayList<>(gameInst.getPlayers());
            gameEntity = gameInst.getGame();;
            mapEntity = new GameMapEntity(gameEntity.getMapId());
        } else
            throw new RuntimeException("Can not login to server.");

    }

    private boolean checkLogin(RestApiInterface restClient) {
        SettingsManagerInterface settings = CommonUtils.getSettingsManager();

        if (!settings.isLoggedIn()) {
            try { restClient.login("dima", "123"); }
            catch (Exception e) { e.printStackTrace(); }

            settings.setAuthToken(restClient.getToken());
        } else restClient.setToken(settings.getAuthToken());

        return settings.isLoggedIn();
    }

    /** TEST CODE ------------------------------------------------------------------------------
     *
     * private static void testMongoDB() {
     * Mongo mongo;
     * MongoTemplate mongoTemplate;
     * try {
     * String host = getDbHost();
     * int port = getDbPort();
     * mongo = new Mongo(host, port);
     * mongoTemplate = new MongoTemplate(mongo, "cubegames");
     * DBCollection collection = mongoTemplate.getCollection("map");
     * //BasicDBObject key = new BasicDBObject(GameMap.FIELD_NAME, 1);
     * //key.put("unique", Boolean.TRUE);
     * //collection.createIndex(key);
     * long cnt = collection.count();
     * cnt +=1;
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     *
     * private static String getDbHost
     * String versionString = GL11.glGetString(7938);
     * String vendorString = GL11.glGetString(7936);
     * String rendererString = GL11.glGetString(7937);
     * GLVersion glVersion = new GLVersion(ApplicationType.Desktop, versionString, vendorString, rendererString);() {
     * Properties properties = MainConfig.getMainProperties();
     * return properties.getProperty("db.host", "localhost");
     * }
     *
     * private static int getDbPort() {
     * Properties properties = MainConfig.getMainProperties();
     * String value = properties.getProperty("db.port", "27017");
     * return Integer.parseInt(value);
     * }
     */

    public Globals initScriptEngine(GLRendererInterface<?> scene) {
        luaEngine = JsePlatform.standardGlobals();
        luaEngine.finder = this;
        luaEngine.loadfile(LUA_GAME_LOGIC_SCRIPT).call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(scene));

        return luaEngine;
    }

    @SuppressWarnings("unused") public SettingsManagerInterface iGetSettingsManager() {
        return getSettingsManager();
    }

    @SuppressWarnings("unused")
    public RestApiInterface getRestApiWrapper() {
        return GdxExt.restAPI;
    }
    @SuppressWarnings("unused") public Globals getLuaEngine() {
        return luaEngine;
    }
    @SuppressWarnings("unused") public GameMapEntity getMapEntity() {
        return mapEntity;
    }
    public void setMapEntity(GameMapEntity mapEntity) {
        this.mapEntity = mapEntity;
    }
    @SuppressWarnings("unused") public GameEntity getGameEntity() {
        return gameEntity;
    }
    public void setGameEntity(GameEntity gameEntity) {
        this.gameEntity = gameEntity;
    }
    @SuppressWarnings("unused") public GameInstanceEntity getGameInstanceEntity() {
        return gameInstanceEntity;
    }
    public void setGameInstanceEntity(GameInstanceEntity gameInstanceEntity) {
        this.gameInstanceEntity = gameInstanceEntity;
    }
    @SuppressWarnings("unused") public List<InstancePlayer> getSavedPlayers() {
        return savedPlayers;
    }
    public void setSavedPlayers(List<InstancePlayer> savedPlayers) {
        this.savedPlayers = savedPlayers;
    }
    @SuppressWarnings("unused") public OpenALSound getSoundObject(String name) {
        return soundCache.get(name);
    }
    public int getPrev_player_index() {
        return prev_player_index;
    }
    public void setPrev_player_index(int prev_player_index) {
        this.prev_player_index = prev_player_index;
    }

    @SuppressWarnings("unused") public Vector3f mulMV(Matrix4f matrix, LuaTable vector) {
        return mulMatOnVec(matrix, new Vector4f(LuaUtils.luaTable2FloatArray(vector)));
    }

    @SuppressWarnings("unused") public Vector3f mulMV(float[] matrix, LuaTable vector) {
        return mulMatOnVec(matrix, vector);
    }

    public Pixmap createPixmap(int width, int height, int fillColor, Pixmap.Format format) {
        return  new GlPixmap(width, height, format, fillColor);
    }

    @SuppressWarnings("unused")
    public void updateSavedPlayers() {
        savedPlayers.clear();
        savedPlayers = gameInstanceEntity.createPlayersList();
    }

    public void requestFinishGame() {
        GdxExt.restAPI.finishGameInstance(gameInstanceEntity);
    }

    //todo: remove old android api call when complete migration
    public void onGameFinished() {
        gameInstanceEntity.setState(GameState.FINISHED);
    }

    public void requestRestartGame() {
        GdxExt.restAPI.restartGameInstance(gameInstanceEntity, getContinuation(ON_GAME_RESTARTED_EVENT_HANDLER));
    }

    //todo: remove old android api call when complete migration
    public void onGameRestarted() {
        luaEngine.get(ON_GAME_RESTARTED_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameInstanceEntity));
    }

    public void playTurn() {
        setPrev_player_index(gameInstanceEntity.getCurrentPlayer());
        onPerformUserAction(ON_PLAY_TURN_EVENT_HANDLER, new LuaValue[]{});
    }

    @Override
    public void onPerformUserAction(String action, LuaValue[] params) {
        luaEngine.invokemethod(action, params);
    }

    @Override
    public void onStopMovingObject(PNodeObject gameObject) {
        luaEngine.get(ON_MOVING_OBJECT_STOP_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameObject),
                                                                CoerceJavaToLua.coerce(gameInstanceEntity));
    }

    @Override
    public void onRollingObjectStart(PNodeObject gameObject) {
        luaEngine.get(ON_ROLLING_OBJECT_START_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameObject));
    }

    @Override
    public void onRollingObjectStop(PNodeObject gameObject) {
        luaEngine.get(ON_ROLLING_OBJECT_STOP_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameObject));
    }

    @Override
    public void onInitGLCamera(GLCamera camera) {
        luaEngine.get(ON_INIT_CAMERA_EVENT_HANDLER).call(CoerceJavaToLua.coerce(camera));
    }

    @Override
    public void onInitLightSource(GLLightSource lightSource) {
        Vector3f sunPos = gameEntity._getStartSunPosition();
        if (lightSource != null) {
            lightSource.setLightPosInModelSpace(new float[] {sunPos.x, sunPos.y, sunPos.z, 1f});
            Vector3f col = lightSource.getColorByAngle(gameEntity._getStartSunColor(), new Vector3f(0.95f, 0.4f, 0f));
            lightSource.setLightColour(col);
        }
    }

    @Override
    public void onInitPhysics(DynamicsWorld dynamicsWorld) { if (dynamicsWorld != null) dynamicsWorld.setGravity(gameEntity._getGravity()); }

    @Override
    public void onLoadSceneObjects(GLRendererInterface<SceneObjectsTreeItem> glScene) {
        TextureCache textureCache = TextureCache.INSTANCE;
        textureCache.clearCache();
        textureCache.getItem("dice_ico.png");
        textureCache.getItem(ROAD_TEXTURE_NAME);
        textureCache.getItem(SAND_TEXTURE_NAME);
        textureCache.getItem(TERRAIN_ATLAS_TEXTURE_NAME);
        textureCache.getItem(MAP_BACKGROUND_TEXTURE_NAME);
        glScene.setBackgroundTextureName(MAP_BACKGROUND_TEXTURE_NAME);

        /** sky-dome */
        AbstractTexture skyDomeTexture = BitmapTexture.createInstance(SKY_DOME_TEXTURE_NAME, false);
        textureCache.putItem(skyDomeTexture);

        AbstractSkyObject skyDomeObject = new SkyDomeObject(skyDomeTexture, glScene);
        skyDomeObject.setItemName(SKY_BOX_CUBE_MAP_OBJECT);
        skyDomeObject.loadObject();
        glScene.getScene().putChild(skyDomeObject, skyDomeObject.getItemName());

        /** Terrain map */
        PNodeObject terrain = new GameMap(Objects.requireNonNull(glScene.getCachedShader(TERRAIN_OBJECT_32)), gameEntity);
        terrain.loadObject();
        terrain.setGlBlendingMap(createBlendingMap());
        terrain.createRigidBody();
        PhysicalWorld.INSTANCE.getPhysicalWorld().addRigidBody(terrain.get_body());
        glScene.getScene().putChild(terrain, TERRAIN_MESH_OBJECT);

        loadGameItems(glScene);
        luaEngine.get(ON_CREATE_DYNAMIC_ITEMS_HANDLER).call(CoerceJavaToLua.coerce(gameEntity), CoerceJavaToLua.coerce(gameInstanceEntity));

        ForestGenerator forest = new ForestGenerator(
                this,
                "palm",
                 glScene.getCachedShader(FOREST_OBJECT),
                100,
                0.65f);
        forest.setInitialScale(0.033f);
        forest.setParent(terrain);
        forest.loadObject();
        terrain.putChild(forest, "FOREST");
        GLAnimation wind = new GLAnimation(7f, ROTATE_BY_Z, 600);
        short rcnt = 0;
        wind.setRepeatCount(rcnt);
        wind.setRollback(true);
        forest.setAnimation(wind);

        /** Water plane -> after terrain to produce depth buffer first*/
        AbstractGL3DObject water = new WaterObject(Objects.requireNonNull(glScene.getCachedShader(WATER_OBJECT)));
        water.loadObject();
        glScene.getScene().putChild(water, WATER_MESH_OBJECT);

        /*PlanetObject testPlanet = new PlanetObject(0.75f, "earth_difuse_2k_astrolab.png", glScene);
        Transform transform = new Transform();
        transform.setIdentity();
        //transform.origin.set(0f, 1f, 0f);
        testPlanet.setWorldTransformMatrix(transform);
        testPlanet.setParent(terrain);
        testPlanet.loadObject();
        //scaleM(testPlanet.getTransformationMatrix(), 0, 2f, 2f, 2f);
        terrain.putChild(testPlanet, "PLANET");*/

        /** Lens flares */
        generateLens(glScene);

        /** Sun */
        AbstractGL3DObject sun = new SunObject(glScene, 1f);
        sun.loadObject();
        glScene.getScene().putChild(sun, SUN_OBJECT);

        /** mini-map gui-box */
        /*if (!getSettingsManager().isIn_2D_Mode()) {
            GUI2DImageObject miniMapView = new GUI2DImageObject(glScene.getCachedShader(GUI_OBJECT),
                                                                new Vector4f(-1, 1, -0.71f, 0.5f),
                                                                true);
            miniMapView.loadObject();
            miniMapView.setGlTexture(terrain.getGlTexture());
            glScene.getScene().putChild(miniMapView, MINI_MAP_OBJECT);
        }*/

        wind.startAnimation(forest, null);

        waitForGC();
        GdxExt.restAPI.removeLoadingSplash();
    }

     @Override public void onUpdateUI(@NotNull GlfwWindow glfwWindow, @NotNull GLRendererInterface<SceneObjectsTreeItem> glScene) {
         ImGui imGui = ImGui.INSTANCE;
         imGui.newFrame();

         imGui.pushStyleVar(StyleVar.Alpha, 0.5f);
         imGui.begin("Debug info", new boolean[]{false}, WindowFlag.NoMove.i | WindowFlag.NoResize.i);
         imGui.popStyleVar(1);

         Vec2i res = new GlfwMonitor(glfwGetPrimaryMonitor()).getVideoMode().getSize();
         imGui.setWindowPos(new Vec2(res.getX() - imGui.getWindowSize().getX(), 0f), Cond.None);
         imGui.setWindowFontScale(1.5f);

         imGui.text("Platform:   %s", Platform.get().getName());
         imGui.text("Runtime:    %s v%s", System.getProperty("java.vm.name"), System.getProperty("java.version"));
         imGui.text("Renderer:   OpenGl v%s", glGetString(GL_VERSION));
         imGui.text("Resolution: %dx%d", res.getX(), res.getY());
         float framerate = imGui.getIo().getFramerate();
         imGui.text("Frame time: %.3f ms (%.1f FPS)", 1_000f / framerate, framerate);

         AbstractTexture texture = Objects.requireNonNull(glScene.getShadowMapFBO()).getFboTexture();
         float tex_w = texture.getWidth();
         float tex_h = texture.getHeight();
         int tex_id = texture.getTextureId();
         imGui.image(tex_id, new Vec2(tex_w / 8f, tex_h / 8f), new Vec2(0,0), new Vec2(1,1), new Vec4(255,255,255,255), new Vec4(255,255,255,128));

         imGui.end();

         imGui.pushStyleVar(StyleVar.Alpha, 0.5f);
         imGui.begin("Panel", new boolean[]{false}, WindowFlag.NoMove.i | WindowFlag.NoResize.i | WindowFlag.NoTitleBar.i);
         imGui.popStyleVar(1);
         imGui.setWindowPos(new Vec2((res.getX() - imGui.getWindowSize().getX()) / 2, 0f), Cond.None);
         imGui.setWindowFontScale(2f);

         Vec2 defaultParam = new Vec2();
         tex_id = TextureCache.INSTANCE.getItem("dice_ico.png").getTextureId();
         if (imGui.imageButton(tex_id, new Vec2(32, 32), new Vec2(0,0), new Vec2(1,1), 1, new Vec4(0,0,0,255), new Vec4(255,255,255,255))) playTurn(); imGui.sameLine(0f, -1f);
         if (imGui.button("Restart", defaultParam)) requestRestartGame(); ; imGui.sameLine(0f, -1f);
         if (imGui.button("Exit", defaultParam)) glfwWindow.setShouldClose(true);

         imGui.end();
    }

    private AbstractTexture createBlendingMap() { //todo: increase resolution
        Pixmap blendMap;

        try (InputStream source = getResourceStream("/textures/blendingMap.png")) {
            assert source != null;
            ByteBuffer encodedData = BufferUtils.createByteBuffer(source.available());
            Channels.newChannel(source).read(encodedData);

            BufferedImage srcImage = ImageIO.read(new ByteBufferBackedInputStream(encodedData.flip()));
            encodedData.clear();
            int scaleFactor = TEXTURE_RESOLUTION_SCALE[getSettingsManager().getGraphicsQualityLevel().ordinal()];
            BufferedImage dstImage =
            Scalr.resize(srcImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, srcImage.getWidth() / scaleFactor, srcImage.getHeight() / scaleFactor, Scalr.OP_ANTIALIAS);

            onPrepareMapTexture(dstImage);

            blendMap = new Pixmap(new Gdx2DPixmap(dstImage));
            ///blendMap = new Pixmap(new Gdx2DPixmap(buffer, TEXTURE_RESOLUTION_SCALE[getSettingsManager().getGraphicsQualityLevel().ordinal()]));
        } catch (IOException e) {
            e.printStackTrace();
            blendMap = null;
        }

        BitmapWrapper bmp = new BitmapWrapper(blendMap);
        bmp.setName(GameConst.BLENDING_MAP_TEXTURE);
        AbstractTexture glTexture = BitmapTexture.createInstance(bmp);
        TextureCache.INSTANCE.putItem(glTexture);

        return glTexture;
    }

    @Override
    public void onPrepareMapTexture(/*Pixmap*/BufferedImage textureBmp) {
        luaEngine.get(ON_PREPARE_MAP_TEXTURE_EVENT_HANDLER).call(CoerceJavaToLua.coerce(textureBmp),
                                                                 CoerceJavaToLua.coerce(gameEntity));
    }

    @Override
    public void onCreateReflectionMap(AbstractFBO reflectMap, AbstractFBO refractMap) {
        luaEngine.get(ON_CREATE_REFLECTION_MAP_EVENT_HANDLER).call(CoerceJavaToLua.coerce(reflectMap),
                                                                   CoerceJavaToLua.coerce(refractMap));
    }

    @Override
    public void onBeforeDrawFrame(long frametime) {
        luaEngine.get(ON_BEFORE_DRAW_FRAME_EVENT_HANDLER).call(CoerceJavaToLua.coerce(frametime));
    }


    @Override
    public void onPlayerMakeTurn(GLAnimation.AnimationCallBack delegate) {
        luaEngine.get(ON_PLAYER_MAKE_TURN_EVENT_HANDLER).call(CoerceJavaToLua.coerce(gameInstanceEntity),
                                                              javaList2LuaTable(savedPlayers),
                                                              CoerceJavaToLua.coerce(delegate));
        savedPlayers.clear();
        savedPlayers = gameInstanceEntity.createPlayersList();
    }

    @SuppressWarnings("unused")
    public Continuation<Unit> getContinuation(String luaCallback) {
        return new Continuation<Unit>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object o) {
                luaEngine.get(luaCallback).call(CoerceJavaToLua.coerce(gameInstanceEntity));
            }
        };
    }

    @Override
    public void onPlayerContinueTurn() {
        if (!GameState.WAIT.equals(gameInstanceEntity.getState())
                && !GameState.FINISHED.equals(gameInstanceEntity.getState())
                )
            onPlayerMakeTurn(animationListener);

        else {
            if (gameInstanceEntity.getPlayers().get(getPrev_player_index()).isSkipped())
                getRestApiWrapper().showAnimatedText("Skip\nnext turn");
        }
    }

    GLAnimation.AnimationCallBack animationListener = () -> {
        ScheduledExecutorService scheduler
                = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> getLuaEngine().get(ON_PLAYER_NEXT_MOVE__EVENT_HANDLER).call(
                CoerceJavaToLua.coerce(gameInstanceEntity));

        int delay = 1;
        scheduler.schedule(task, delay, TimeUnit.SECONDS);
        scheduler.shutdown();
    };

    @Override
    public InputStream findResource(String name) {
        return getGameEntity().getLuaScript(name);
    }

    private void loadGameItems(GLRendererInterface<SceneObjectsTreeItem> glScene) {
        /** game sounds */
        soundCache.clear();
        for (String fileName : gameEntity.getGameSounds()) {
            InputStream data = getResourceStream("/sounds/" + fileName);
            if (data != null)
                soundCache.put(fileName, GdxExt.audio.newSound(data));
        }

        /** scene objects */
        Blender3DObject sceneObject;
        for (InteractiveGameItem item : gameEntity.getGameItems()) {
            sceneObject = item.createSceneObject(glScene);
            sceneObject.loadObject();

            if (item.onInitEventHandler != null)
                luaEngine.get(item.onInitEventHandler).call(CoerceJavaToLua.coerce(sceneObject));

            getParentNode(glScene, item).putChild(sceneObject, sceneObject.getItemName());
        }
    }

    private SceneObjectsTreeItem getParentNode(GLRendererInterface<SceneObjectsTreeItem> glScene, InteractiveGameItem item) {
        SceneObjectsTreeItem parentObject = glScene.getObject(item.itemParentName != null ? item.itemParentName : "");

        return parentObject != null ? parentObject : glScene.getScene();
    }

    private void releaseSoundCache() {
        if (soundCache != null) {
            for (OpenALSound sound : soundCache.values())
                sound.dispose();

            soundCache.clear();
            soundCache = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        releaseSoundCache();

        super.finalize();
    }
}
