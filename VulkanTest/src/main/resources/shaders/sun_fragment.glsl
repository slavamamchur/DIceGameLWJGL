precision mediump float;

layout (location = 0) out vec4 colorBuffer;
layout (location = 1) out vec4 lightBuffer;
layout (location = 2) out vec4 raysBuffer;

uniform sampler2D u_TextureUnit;
uniform vec3 u_lightColour;
uniform float uAlphaScale;
uniform int u_isLightSource;

varying vec2 v_Texture;

void main()
{
    vec4 fragColor = texture2D(u_TextureUnit, v_Texture); //vec4(u_lightColour, texture2D(u_TextureUnit, v_Texture).a);
    fragColor.a *= uAlphaScale;

    colorBuffer = fragColor;
    lightBuffer = vec4(0.0);
    raysBuffer = vec4(u_lightColour * u_isLightSource, fragColor.a);
}
