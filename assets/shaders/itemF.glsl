#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

varying float v_opacity;
varying MED vec2 v_diffuseUV;
uniform sampler2D u_diffuseTexture;
uniform float light;

void main() {
	gl_FragColor = texture2D(u_diffuseTexture, v_diffuseUV);
	gl_FragColor.a = gl_FragColor.a * v_opacity;
	//light
	gl_FragColor.rgb = mix(gl_FragColor.rgb, vec3(0), 1.0-light/14.0);//TODO light/14.0 можно делать до передачи в шейдер но в целом похуй
}
