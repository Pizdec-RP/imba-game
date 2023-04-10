attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans; //Light Camera

varying vec4 v_positionLightFrame;
varying vec2 texCoords;

void main() {
	v_positionLightFrame = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
	gl_Position = v_positionLightFrame;
	texCoords = a_texCoord0;
}

