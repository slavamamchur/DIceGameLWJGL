#extension GL_EXT_clip_cull_distance : enable

layout(location = 1) in vec3 a_Position;
layout(location = 2) in vec2 a_Texture;

varying vec2 v_Texture;

const vec4 uClipPlane = vec4(1.0, 1.0, 0.0, 100000); //todo: change plane orientation ???

void main()
{
	v_Texture = a_Texture; /*a_Position.xy * 0.5 + 0.5 */
	gl_ClipDistance[0] = dot(vec4(a_Position, 1.0), uClipPlane);
	gl_Position = vec4(a_Position, 1.0);

}
