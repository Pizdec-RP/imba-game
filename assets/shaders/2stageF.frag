#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec2 screensize;

varying vec2 v_texCoord0;

void main() {
    gl_FragColor = texture2D(u_texture, v_texCoord0);
    //--------vignette------
	vec2 position = (gl_FragCoord.xy / screensize) - vec2(0.5);
	float len = length(position);
	float vignette = smoothstep(0.75, 0.75-0.45, len);
	gl_FragColor.rgb = mix(gl_FragColor.rgb, gl_FragColor.rgb * vignette, 0.65);
	//--------vignette------
}
	