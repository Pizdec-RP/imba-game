#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec2 screensize;
uniform float hurtlevel;
uniform float isdead;
uniform float random;
uniform float deadshades;
uniform sampler2D beforeframe;

varying vec2 v_texCoord0;

float randomFloat() {
    return fract(sin(random*gl_FragCoord.z+random*gl_FragCoord.w-gl_FragCoord.x));
}

void main() {
    gl_FragColor = texture2D(u_texture, v_texCoord0);
    vec4 beforef = texture2D(beforeframe, v_texCoord0);
    //gl_FragColor = mix(gl_FragColor, beforef, 0.5);
    //--------vignette------
	vec2 position = (gl_FragCoord.xy / screensize) - vec2(0.5);
	float len = length(position);
	float vignette = smoothstep(0.75, 0.75-0.45, len);
	gl_FragColor.rgb = mix(gl_FragColor.rgb, gl_FragColor.rgb * vignette, 0.65);
	//--------vignette------
	
	if (isdead != 0) {
		gl_FragColor.a = deadshades;
	}
	
	if (hurtlevel > 0) {
		float f = (hurtlevel - 1) / 99;
		gl_FragColor = mix(gl_FragColor, vec4(1,0,0,1), f);
	}
}