precision mediump float;

#ifdef GLES330
    layout (location = 0) out vec4 colorBuffer;
    layout (location = 1) out vec4 lightBuffer;
#endif

uniform sampler2D u_TextureUnit;
uniform sampler2D uShadowTexture;

uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform vec3 u_lightPositionF;
uniform vec3 u_lightColour;
uniform int u_isCubeMapF;
uniform float uxPixelOffset;
uniform float uyPixelOffset;

//varying vec3 v_Normal;
varying highp vec4 vShadowCoord;
varying float vdiffuse;
varying float vspecular;
varying vec4 wPos;

const float nmapTiling = 6.0;
const float PI  = 3.141592653589793;

vec2 RadialCoords(vec3 a_coords_n)
{
    float lon = atan(a_coords_n.z, a_coords_n.x);
    float lat = acos(a_coords_n.y);
    vec2 sphereCoords = vec2(lon, lat) * (1.0 / PI);

    return vec2(1.0 - sphereCoords.x * 0.5 + 0.5, sphereCoords.y);
}

highp float calcDynamicBias(highp float bias, vec3 normal) {
    highp float result;
    highp vec3 nLightPos = normalize(u_lightPositionF);
    highp float cosTheta = clamp(dot(normal, nLightPos), 0.0, 1.0);
    result = bias * tan(acos(cosTheta));

    return clamp(result, 0.0, 0.3);
}

highp float unpack (highp vec4 packedZValue) {
    return packedZValue.r;
}

float calcShadowRate(vec2 offSet) {
        highp float bias = 0.00005; //calcDynamicBias(0.001, nNormal); // (0.00005)
        highp vec4 shadowMapPosition = vShadowCoord/* / vShadowCoord.w - > for spot lights only (low priority) */;

        #ifdef GLES330
            highp vec4 packedZValue = texture2DProj(uShadowTexture, (shadowMapPosition + vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.05, 0.0)));
        #else
            highp vec4 packedZValue = texture2D(uShadowTexture, (shadowMapPosition + vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.05, 0.0)).st);
        #endif

        highp float distanceFromLight = unpack(packedZValue);

        return float(distanceFromLight > (shadowMapPosition.z /** 255.0*/ - bias));
}

float shadowPCF(float n) { //pcf nxn
	float shadow = 1.0;

	float cnt = (n - 1.0) / 2.0;
	for (float y = -cnt; y <= cnt; y = y + 1.0) {
		for (float x = -cnt; x <= cnt; x = x + 1.0) {
			shadow += calcShadowRate(vec2(x,y));
		}
	}

	shadow /= (n * n);
	shadow += 0.2;

	return shadow;
}

vec4 calcLightColor(float shadowRate) {
      float lightFactor = u_DiffuseRate * vdiffuse;
      vec3 lightColour = u_lightColour * (u_AmbientRate + lightFactor);

      return vec4(lightColour * shadowRate, 1.0);
}

vec4 calcSpecularColor(float shadowRate) {
      float specular = u_SpecularRate * vspecular;

      if (shadowRate < 1.0) {
            specular = 0.0;
      }
      else /*if (u_isCubeMapF == 1)*/ {
            specular *= 0.25; //todo: use specular map to separate ground and water regions
      }

      return vec4(u_lightColour * specular, 1.0);
}

vec4 calcPhongLightingMolel(vec4 diffuseColor, float shadowRate, float specularRate) {
      vec4 lightColor = calcLightColor(shadowRate);
      vec4 specularColor = calcSpecularColor(shadowRate);

      return lightColor * diffuseColor + specularColor * specularRate;
}

vec4 textureFromAtlas(sampler2D atlas, vec2 uv, float page) {
    return texture2D(atlas, vec2(clamp(uv.x + 0.25 * page, 0.25 * page, 0.25 * (page + 1.0)), uv.y));
}

float getColorBrightness(vec4 color) {
    return color.r * 0.2126 + color.g * 0.7152 + color.b * 0.0722;
}

void main()
{
      highp float shadowRate = 1.0;
      if (vShadowCoord.w > 0.0 /*&& wPosition.y >= 0.0*/) {
        shadowRate = shadowPCF(/*n_normal,*/ 4.0); //todo: use param for pcf quality level
        shadowRate = (shadowRate * (1.0 - u_AmbientRate)) + u_AmbientRate;
      }

    vec2 v_Texture = RadialCoords(normalize(wPos.xyz));
    vec4 fragColor = calcPhongLightingMolel(texture2D(u_TextureUnit, v_Texture), shadowRate, 1.0);

      #ifdef GLES330
        colorBuffer = fragColor;

        if (getColorBrightness(fragColor) > 0.5) {
            lightBuffer = fragColor;
        }
        else {
            lightBuffer = vec4(0.0);
        }
      #else
        gl_FragColor = fragColor;
      #endif
}
