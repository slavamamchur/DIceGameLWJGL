#ifdef CLIP_PLANE
#extension GL_EXT_clip_cull_distance : enable
#endif

#ifdef GLES330
layout(location = 1) in vec3 a_Position;
#else
attribute vec3 a_Position;
#endif

uniform mat4 u_MVP_Matrix;

varying vec3 v_Normal;

const vec4 uClipPlane = vec4(0.0, 1.0, 0.0, -0.0);

void main()
{
	#ifdef CLIP_PLANE
		gl_ClipDistance[0] = dot(vec4(a_Position, 1.0), uClipPlane);
	#endif

	v_Normal = a_Position * -1.0;

	gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
}