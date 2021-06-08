#ifndef GLES330
    precision mediump float;
#else
    layout (location = 0) out vec4 refractBuffer;
    layout (location = 1) out vec4 raysBuffer;
#endif

uniform sampler2D u_TextureUnit;
uniform int u_is2DModeF;
uniform int u_isLightSource;
uniform vec3 u_lightColour;
uniform int  u_isObjectGroupF;

varying vec3 v_wPosition;
varying vec2 v_Texture;
varying float vdiffuse;

const float nmapTiling = 4.0;

vec4 calcLightColor() {
      vec3 lightColour = vec3(1.0) * (0.2 + 0.8 * vdiffuse);

      return vec4(lightColour, 1.0);
}

vec4 calcPhongLightingMolel(vec4 diffuseColor) {
      return calcLightColor() * diffuseColor;
}

void main()
{
      vec4 diffuseColor;

      bool need2Clip = v_wPosition.y > 0.01;
      #ifdef CLIP_PLANE
           need2Clip = false;
      #endif

      #ifndef GLES330
        if (need2Clip || u_is2DModeF == 1) {
           discard;
        }
        else {
           diffuseColor = texture2D(u_TextureUnit, v_Texture * nmapTiling);
        }

        gl_FragColor = calcPhongLightingMolel(diffuseColor);
      #else
        if (u_is2DModeF == 1) {
            discard;
        }
        else {
            diffuseColor = texture2D(u_TextureUnit, v_Texture * (1.0 + (nmapTiling - 1.0) * (1 - u_isLightSource)));
            refractBuffer = vec4(0.0);
            raysBuffer = vec4(0.0);

            if (need2Clip) {
                raysBuffer = vec4(u_lightColour * u_isLightSource, diffuseColor.a) * diffuseColor;
            }
            else if (u_isObjectGroupF != 1) {
                refractBuffer = calcPhongLightingMolel(diffuseColor);
            }
        }
      #endif
}
