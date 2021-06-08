precision mediump float;

uniform sampler2D u_TextureUnit;
uniform int u_is2DModeF;
uniform int u_isLightSource;
uniform vec3 u_lightColour;

varying vec2 v_Texture;
varying vec3 v_wPosition;

void main()
{
      bool need2Clip;
      #ifdef CLIP_PLANE
        need2Clip = false;
      #else
        need2Clip = (v_wPosition.y <= 0.0) && (u_isLightSource == 0);
      #endif

      if (need2Clip || u_is2DModeF == 1) {
          discard;
      }

    vec4 color = texture2D(u_TextureUnit, v_Texture);
    gl_FragColor = vec4(u_lightColour * u_isLightSource, color.a) * color;
}
