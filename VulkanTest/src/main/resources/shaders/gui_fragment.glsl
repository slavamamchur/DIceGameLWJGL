#ifndef GLES330
      precision mediump float;
#else
    layout (location = 0) out vec4 colorBuffer;
    layout (location = 1) out vec4 lightBuffer;
    layout (location = 2) out vec4 raysBuffer;
#endif

uniform sampler2D u_TextureUnit;
uniform sampler2D u_BlendingMapUnit;
uniform sampler2D depthMap;
uniform int  uEffects;
uniform vec3 u_lightPosition;
uniform float uContrastLevel;

varying vec2 v_Texture;

const int GOD_RAYS_POST_EFFECT   = 1;
const int CONTRAST_CHARGE_EFFECT = 2;
const int BLUR_EFFECT = 4;
const int BLOOM_EFFECT           = 16;
const int DOF_EFFECT             = 32;
const float blurWidth = -0.85;

#define NUM_SAMPLES 100

void main()
{
      vec4 fcolor = texture2D(u_TextureUnit, v_Texture);

      if ((uEffects & GOD_RAYS_POST_EFFECT) != 0) {
          vec2 tc = v_Texture - u_lightPosition.xy;
          vec3 color = vec3(0.0);

          for(int i = 0; i < NUM_SAMPLES; i++) {
              float scale = 1.0 + blurWidth * (float(i) / float(NUM_SAMPLES - 1));
              color += (texture2D(u_TextureUnit, (tc * scale) + u_lightPosition.xy).xyz) / float(NUM_SAMPLES);
          }

          fcolor = vec4(color, 1.0);
      }

      if ((uEffects & BLUR_EFFECT) != 0) {
          const vec2  d1 = vec2 ( 1.0/512.0, 1.0/512.0 );
          const vec2  d2 = vec2 ( 1.0/512.0, -1.0/512.0 );
          const vec2  d3 = vec2 ( -1.0/512.0, 1.0/512.0 );
          const vec2  d4 = vec2 ( -1.0/512.0, -1.0/512.0 );

          fcolor = (texture2D ( u_TextureUnit, vec2 ( v_Texture + d1 ) ) +
                    texture2D ( u_TextureUnit, vec2 ( v_Texture + d2 ) ) +
                    texture2D ( u_TextureUnit, vec2 ( v_Texture + d3 ) ) +
                    texture2D ( u_TextureUnit, vec2 ( v_Texture + d4 ) ) ) * 0.25;
      }

      if ((uEffects & DOF_EFFECT) != 0) {
          float sampleOffset = texture2D(depthMap, v_Texture).r / 100000.0;
          vec4 fcolor = vec4(0.0);

          fcolor += texture2D(u_TextureUnit, v_Texture + vec2(-sampleOffset,-sampleOffset)) * 1.0;
          fcolor += texture2D(u_TextureUnit, v_Texture + vec2( 0.0         ,-sampleOffset)) * 2.0;
          fcolor += texture2D(u_TextureUnit, v_Texture + vec2( sampleOffset,-sampleOffset)) * 1.0;

          fcolor += texture2D(u_TextureUnit, v_Texture + vec2(-sampleOffset, 0.0     )) * 2.0;
          fcolor += texture2D(u_TextureUnit, v_Texture + vec2( 0.0         , 0.0     )) * 4.0;
          fcolor += texture2D(u_TextureUnit, v_Texture + vec2( sampleOffset, 0.0     )) * 2.0;

          fcolor += texture2D(u_TextureUnit, v_Texture + vec2( sampleOffset, sampleOffset)) * 1.0;
          fcolor += texture2D(u_TextureUnit, v_Texture + vec2( 0.0         , sampleOffset)) * 2.0;
          fcolor += texture2D(u_TextureUnit, v_Texture + vec2(-sampleOffset, sampleOffset)) * 1.0;

          fcolor *= 0.0625;
      }

      if ((uEffects & BLOOM_EFFECT) != 0) {
          fcolor += texture2D(u_BlendingMapUnit, v_Texture) * 0.9;
      }

      if ((uEffects & CONTRAST_CHARGE_EFFECT) != 0) { //always last
          fcolor.rgb = (fcolor.rgb - 0.5) * uContrastLevel + 0.5;
      }

      #ifdef GLES330
        colorBuffer = fcolor;
        lightBuffer = vec4(0.0, 0.0, 0.0, 1.0);
        raysBuffer = vec4(0.0);
    #else
        gl_FragColor = fcolor;
      #endif
}
