precision mediump float;

#ifdef GLES330
    layout (location = 0) out vec4 colorBuffer;
    layout (location = 1) out vec4 lightBuffer;
#endif

uniform sampler2D u_TextureUnit;
//uniform vec3 u_lightColour;
uniform float uAlphaScale;

varying vec2 v_Texture;

void main()
{
    vec4 fragColor = texture2D(u_TextureUnit, v_Texture); //vec4(u_lightColour, texture2D(u_TextureUnit, v_Texture).a);
    fragColor.a *= uAlphaScale;

    #ifdef GLES330
        colorBuffer = fragColor;
        lightBuffer = vec4(0.0);
    #else
        gl_FragColor = fragColor;
    #endif

}
