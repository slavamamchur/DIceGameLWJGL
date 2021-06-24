precision mediump float;

#ifdef GLES330
	layout (location = 0) out vec4 colorBuffer;
	layout (location = 1) out vec4 lightBuffer;
	layout (location = 2) out vec4 raysBuffer;
#endif

uniform sampler2D u_TextureUnit;
uniform vec3 u_lightColour;

varying vec3 v_Normal;

const vec4 skyColour = vec4(0.0, 0.64, 0.88, 1.0);
const float lowerLimit = 0.0;
const float upperLimit = 1.0;
const float PI  = 3.141592653589793;

vec2 RadialCoords(vec3 a_coords)
{
	vec3 a_coords_n = normalize(a_coords);
	float lon = atan(a_coords_n.z, a_coords_n.x);
	float lat = acos(a_coords_n.y);
	vec2 sphereCoords = vec2(lon, lat) * (1.0 / PI);

	return vec2(sphereCoords.x * 0.5 + 0.5, 1.0 - sphereCoords.y);
}

void main()
{
      //vec2 v_Texture = RadialCoords(v_Normal);
      //vec4 textureColour = texture2D(u_TextureUnit, v_Texture);

      vec3 vPos = v_Normal * -1.0;
      float blendFactor = 1.0 - clamp((vPos.y - lowerLimit) / (upperLimit - lowerLimit), 0.0, 1.0);

      vec4 fragColor = mix(skyColour, vec4(u_lightColour, 1.0), blendFactor);

	  #ifdef GLES330
		colorBuffer = fragColor;
		lightBuffer = vec4(0.0);
		raysBuffer = vec4(0.0);
	  #else
		gl_FragColor = fragColor;
	  #endif
}