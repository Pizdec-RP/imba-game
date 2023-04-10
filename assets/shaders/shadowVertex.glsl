attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans; //Of the Perspective Camera
uniform mat4 dLightCamProjView;

varying vec4 v_positionLS;
varying vec2 v_texCoords;
varying vec4 v_positionCS;
varying vec3 v_normal;

void main() {
	v_texCoords = a_texCoord0;
	v_positionLS =  dLightCamProjView*u_worldTrans * vec4(a_position, 1.0);
	gl_Position = u_projViewTrans* u_worldTrans * vec4(a_position, 1.0);
	v_positionCS = u_projViewTrans* u_worldTrans * vec4(a_position, 1.0);
	v_normal = mat3(transpose(inverse(u_worldTrans))) * a_normal;
}
