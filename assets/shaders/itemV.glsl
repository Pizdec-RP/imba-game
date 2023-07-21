attribute vec3 a_position;
uniform mat4 u_projViewTrans;

attribute vec2 a_texCoord0;
uniform vec4 u_diffuseUVTransform;
varying vec2 v_diffuseUV;
uniform mat4 u_worldTrans;
uniform float u_opacity;
varying float v_opacity;

void main() {
	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
	v_opacity = u_opacity;
	vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	gl_Position = u_projViewTrans * pos;
}
