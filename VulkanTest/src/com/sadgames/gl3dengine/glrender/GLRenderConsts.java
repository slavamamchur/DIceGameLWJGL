package com.sadgames.gl3dengine.glrender;

import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

public  class GLRenderConsts {

    public enum GLParamType {
        FLOAT_ATTRIB_ARRAY_PARAM,
        FLOAT_UNIFORM_VECTOR_PARAM,
        FLOAT_UNIFORM_VECTOR4_PARAM,
        FLOAT_UNIFORM_MATRIX_PARAM,
        FLOAT_UNIFORM_PARAM,
        INTEGER_UNIFORM_PARAM
    }

    public enum GLObjectType {
        WATER_OBJECT,
        TERRAIN_OBJECT,
        TERRAIN_OBJECT_32,
        GEN_TERRAIN_OBJECT,
        SKY_BOX_OBJECT,
        SKY_DOME_OBJECT,
        LIGHT_OBJECT,
        GAME_ITEM_OBJECT,
        SHADOW_MAP_OBJECT,
        GUI_OBJECT,
        SUN_OBJECT,
        FLARE_OBJECT,
        FOREST_OBJECT,
        REFLECTION_MAP_OBJECT,
        REFRACTION_MAP_OBJECT,
        RAYS_MAP_OBJECT,
        POST_PROCESS_OBJECT,
        PLANET_OBJECT,
        UNKNOWN_OBJECT
    }

    public enum GLAnimationType {
        TRANSLATE_ANIMATION,
        ROTATE_ANIMATION,
        ZOOM_ANIMATION
    }

    public enum GraphicsQuality {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA
    }

    public enum RenderType {
        GL41_RENDER,
        VULKAN_RENDER,
        VULKAN_RTX_RENDER,
        METAL_RENDER
    }

    public static final Map<Integer, GLParamType> GL_PARAM_TYPES = new HashMap<Integer, GLParamType>() {{
        put(GL20.GL_INT, GLParamType.INTEGER_UNIFORM_PARAM);
        put(GL20.GL_SAMPLER_2D, GLParamType.INTEGER_UNIFORM_PARAM);
        put(GL20.GL_FLOAT, GLParamType.FLOAT_UNIFORM_PARAM);
        put(GL20.GL_FLOAT_MAT4, GLParamType.FLOAT_UNIFORM_MATRIX_PARAM);
        put(GL20.GL_FLOAT_VEC3, GLParamType.FLOAT_UNIFORM_VECTOR_PARAM);
        put(GL20.GL_FLOAT_VEC4, GLParamType.FLOAT_UNIFORM_VECTOR4_PARAM);
    }};

    public static final float[] SHADOW_MAP_RESOLUTION_SCALE = new float[] {0.5f, 1.0f, 1.5f, 2.0f}; //todo: ???
    public static final int[] TEXTURE_RESOLUTION_SCALE = new int[] {4, 2, 1, 1};

    public static final int VERTEX_SIZE = 3;
    public static final int TEXEL_UV_SIZE = 2;
    public static final int VBO_ITEM_SIZE = (VERTEX_SIZE + TEXEL_UV_SIZE);
    public static final int VBO_STRIDE = VBO_ITEM_SIZE * 4;

    public static final float LAND_SIZE_IN_WORLD_SPACE = 10.0f;
    public static final float LAND_SIZE_IN_KM = 242.0f; //242Km
    public static final float SEA_SIZE_IN_WORLD_SPACE = 10.0f;

    public final static float    DEFAULT_LIGHT_X          = -2.20F;
    public final static float    DEFAULT_LIGHT_Y          =  1.70F;
    public final static float    DEFAULT_LIGHT_Z          = -3.20F;
    public final static Vector3f DEFAULT_LIGHT_COLOUR     = new Vector3f(1.0f, 1.0f, 0.8f);
    public final static Color4f  DEPTH_BUFFER_CLEAR_COLOR = new Color4f(1.0f, 1.0f, 1.0f, 1.0f);
    
    public static final Vector3f DEFAULT_GRAVITY_VECTOR = new Vector3f(0f, -9.8f, 0f);
    public  final static float    SIMULATION_FRAMES_IN_SEC = 60f; /** FPS */

    public final static float    DEFAULT_CAMERA_X       = 0f;
    public final static float    DEFAULT_CAMERA_Y       = 3f;
    public final static float    DEFAULT_CAMERA_Z       = 3f;
    public final static float    DEFAULT_CAMERA_PITCH   = 45.0f;
    public final static float    DEFAULT_CAMERA_YAW     = 0.0f;
    public final static float    DEFAULT_CAMERA_ROLL    = 0.0f;
    public static final float    DEFAULT_CAMERA_VERTICAL_FOV = 45.0f;

    public final static float    WAVE_SPEED             = 0.04f;

    public static final String VERTEXES_PARAM_NAME = "a_Position";
    public static final String TEXELS_PARAM_NAME = "a_Texture";
    public static final String NORMALS_PARAM_NAME = "a_Normal";
    public static final String OFFSETS_PARAM_NAME = "aOffset";
    public static final String ACTIVE_TEXTURE_SLOT_PARAM_NAME = "u_TextureUnit";
    public static final String ACTIVE_REFRACTION_MAP_SLOT_PARAM_NAME = "u_RefractionMapUnit";
    public static final String ACTIVE_SKYBOX_MAP_SLOT_PARAM_NAME = "u_ReflectionMapUnit";
    public static final String ACTIVE_NORMALMAP_SLOT_PARAM_NAME = "u_NormalMapUnit";
    public static final String ACTIVE_DUDVMAP_SLOT_PARAM_NAME = "u_DUDVMapUnit";
    public static final String ACTIVE_BLENDING_MAP_SLOT_PARAM_NAME = "u_BlendingMapUnit";
    public static final String ACTIVE_SHADOWMAP_SLOT_PARAM_NAME = "uShadowTexture";
    public static final String ACTIVE_DEPTHMAP_SLOT_PARAM_NAME = "depthMap";
    public static final String ACTIVE_BACKGROUND_SLOT_PARAM_NAME = "u_BackgroundUnit";
    public static final String ACTIVE_TERRAIN_TEXTURE_SLOT_PARAM_NAME = "u_TerrainAtlas";
    public static final String ACTIVE_ROAD_TEXTURE_SLOT_PARAM_NAME = "u_RoadUnit";
    public static final String ACTIVE_DIRT_TEXTURE_SLOT_PARAM_NAME = "u_DirtUnit";
    public static final String ACTIVE_GRASS_TEXTURE_SLOT_PARAM_NAME = "u_GrassUnit";
    public static final String ACTIVE_SAND_TEXTURE_SLOT_PARAM_NAME = "u_SandUnit";
    public static final String IS_CUBEMAP_PARAM_NAME = "u_isCubeMap";
    public static final String IS_CUBEMAPF_PARAM_NAME = "u_isCubeMapF";
    public static final String IS_2D_MODE_PARAM_NAME = "u_is2DMode";
    public static final String IS_LIGHT_SOURCE_PARAM_NAME = "u_isLightSource";
    public static final String IS_2D_MODEF_PARAM_NAME = "u_is2DModeF";
    public static final String HAS_REFLECT_MAP_PARAM_NAME = "u_hasReflectMap";
    public static final String IS_NORMALMAP_PARAM_NAME = "u_isNormalMap";
    public static final String MVP_MATRIX_PARAM_NAME = "u_MVP_Matrix";
    public static final String LIGHT_MVP_MATRIX_PARAM_NAME = "uShadowProjMatrix";
    public static final String MV_MATRIX_PARAM_NAME = "u_MV_Matrix";
    public static final String MV_MATRIXF_PARAM_NAME = "u_MV_MatrixF";
    public static final String MODEL_MATRIX_PARAM_NAME = "uMMatrix";
    public static final String SKY_BOX_MV_MATRIXF_PARAM_NAME = "u_SkyboxMV_MatrixF";
    public static final String LIGHT_POSITION_PARAM_NAME = "u_lightPosition";
    public static final String LIGHT_POSITIONF_PARAM_NAME = "u_lightPositionF";
    public static final String LIGHT_COLOUR_PARAM_NAME = "u_lightColour";
    public static final String CAMERA_POSITION_PARAM_NAME = "u_camera";
    public static final String RND_SEED__PARAM_NAME = "u_RndSeed";
    public static final String IS_OBJECT_GROUP_PARAM_NAME = "u_isObjectGroup";
    public static final String IS_OBJECT_GROUPF_PARAM_NAME = "u_isObjectGroupF";
    public static final String AMBIENT_RATE_PARAM_NAME = "u_AmbientRate";
    public static final String DIFFUSE_RATE_PARAM_NAME = "u_DiffuseRate";
    public static final String SPECULAR_RATE_PARAM_NAME = "u_SpecularRate";
    public static final String UX_PIXEL_OFFSET_PARAM_NAME = "uxPixelOffset";
    public static final String UY_PIXEL_OFFSET_PARAM_NAME = "uyPixelOffset";
    public static final String TIME_PARAM_NAME = "uTime";
    public static final String ALPHA_SCALE_PARAM_NAME = "uAlphaScale";
    public static final String EFFECTS_PARAM_NAME = "uEffects";
    public static final String CONTRAST_LEVEL_PARAM_NAME = "uContrastLevel";
    public static final String TARGET_WIDTH_PARAM_NAME = "targetWidth";
    public static final String TARGET_HEIGHT_PARAM_NAME = "targetHeight";
    public static final String TESSELLATION_PARAMS_PARAM_NAME = "tess_params";

    public static final String OES_DEPTH_TEXTURE_EXTENSION = "OES_depth_texture";
    public static final String ARB_DEPTH_TEXTURE_EXTENSION = "GL_ARB_depth_texture";
    public static final String UNI_DEPTH_TEXTURE_EXTENSION = "_depth_texture";

    public static final String MAIN_RENDERER_VERTEX_SHADER = "shaders/vertex_shader.glsl";
    public static final String MAIN_RENDERER_FRAGMENT_SHADER = "shaders/fragment_shader.glsl";
    public static final String TERRAIN_RENDERER_VERTEX_SHADER = "shaders/vs_terrain32.glsl";
    public static final String TERRAIN_RENDERER_GEOMETRY_SHADER = "shaders/gs_terrain32.glsl";
    public static final String TERRAIN_RENDERER_FRAGMENT_SHADER = "shaders/fs_terrain32.glsl";
    public static final String PLANET_RENDERER_VERTEX_SHADER = "shaders/vs_planet.glsl";
    public static final String PLANET_RENDERER_TC_SHADER = "shaders/tc_planet.glsl";
    public static final String PLANET_RENDERER_TE_SHADER = "shaders/te_planet.glsl";
    public static final String PLANET_RENDERER_GEOMETRY_SHADER = "shaders/gs_planet.glsl";
    public static final String PLANET_RENDERER_FRAGMENT_SHADER = "shaders/fs_planet.glsl";
    public static final String WATER_RENDERER_VERTEX_SHADER = "shaders/vs_water.glsl";
    public static final String WATER_RENDERER_VERTEX_SHADER_ES32 = "shaders/vs_water_es32.glsl";
    public static final String WATER_RENDERER_FRAGMENT_SHADER = "shaders/fs_water.glsl";
    public static final String WATER_RENDERER_GEOMETRY_SHADER = "shaders/gs_water.glsl";
    public static final String GEN_TERRAIN_FRAGMENT_SHADER = "shaders/fs_genTerrain.glsl";
    public static final String SHADOWMAP_VERTEX_SHADER = "shaders/v_depth_map.glsl";
    public static final String SHADOWMAP_VERTEX_SHADER_DEPTH_SUPPORT = "shaders/depth_tex_v_depth_map.glsl";
    public static final String SHADOWMAP_FRAGMENT_SHADER = "shaders/f_depth_map.glsl";
    public static final String SHADOWMAP_FRAGMENT_SHADER_DEPTH_SUPPORT = "shaders/depth_tex_f_depth_map.glsl";
    public static final String GUI_VERTEX_SHADER = "shaders/gui_vertex.glsl";
    public static final String SUN_VERTEX_SHADER = "shaders/sun_vertex.glsl";
    public static final String GUI_FRAGMENT_SHADER = "shaders/gui_fragment.glsl";
    public static final String SUN_FRAGMENT_SHADER = "shaders/sun_fragment.glsl";
    public static final String FLARE_FRAGMENT_SHADER = "shaders/flare_fragment.glsl";
    public static final String FOREST_VERTEX_SHADER = "shaders/forest_vertex.glsl";
    public static final String SKYBOX_VERTEX_SHADER = "shaders/skybox_vertex.glsl";
    public static final String SKYBOX_FRAGMENT_SHADER = "shaders/skybox_fragment.glsl";
    public static final String SKYDOME_VERTEX_SHADER = "shaders/skydome_vertex.glsl";
    public static final String SKYDOME_FRAGMENT_SHADER = "shaders/skydome_fragment.glsl";
    public static final String REFLECTION_MAP_VERTEX_SHADER = "shaders/vs_reflect_map.glsl";
    public static final String REFLECTION_MAP_FRAGMENT_SHADER = "shaders/fs_reflect_map.glsl";
    public static final String REFRACTION_MAP_VERTEX_SHADER = "shaders/vs_refract_map.glsl";
    public static final String REFRACTION_MAP_FRAGMENT_SHADER = "shaders/fs_refract_map.glsl";
    public static final String RAYS_VERTEX_SHADER = "shaders/vs_rays.glsl";
    public static final String RAYS_FRAGMENT_SHADER = "shaders/fs_rays.glsl";

    public static final int FBO_TEXTURE_SLOT = 6;
    public static final int BACKGROUND_TEXTURE_SLOT = 7;
    public static final int ROAD_TILE_TEXTURE_SLOT = 8;

}
