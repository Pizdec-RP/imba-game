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

#if defined(specularTextureFlag) || defined(specularColorFlag)
#define specularFlag
#endif

#ifdef normalFlag
varying vec3 v_normal;//хз чо это
#endif //normalFlag

#if defined(colorFlag)
varying vec4 v_color;//цвет вершины если у нее нет текстуры
#endif

#ifdef blendedFlag
varying float v_opacity;
#ifdef alphaTestFlag
varying float v_alphaTest;
#endif //alphaTestFlag
#endif //blendedFlag

#if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(emissiveTextureFlag)
#define textureFlag
#endif

#ifdef diffuseTextureFlag
varying MED vec2 v_diffuseUV;//корды текстуры относительно вершины
#endif

#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;//цвет модели без текстуры
#endif

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;//текстура модели
#endif

uniform vec3 test;
varying float vertexlight;
uniform int haslight;
varying float makefragred;

//gl_FragCoord

void main() {
	#if defined(normalFlag)
		vec3 normal = v_normal;
	#endif // normalFlag
	
	vec4 diffuse = vec4(1.0);
	#if defined(diffuseTextureFlag)
		diffuse = texture2D(u_diffuseTexture, v_diffuseUV);//получаем цвет фрагмента из текстуры
	#elif defined(diffuseColorFlag)
		diffuse = u_diffuseColor;//делаем его как цвет модели если нема текстуры
	#else
		diffuse = vec4(1.0);//тупа делаем его белым
	#endif
	
	
	gl_FragColor = diffuse;


	#ifdef blendedFlag
		gl_FragColor.a = diffuse.a * v_opacity;
		#ifdef alphaTestFlag
			if (gl_FragColor.a <= v_alphaTest)
				discard;
		#endif
	#else
		gl_FragColor.a = 1.0;
	#endif
	if (gl_FragCoord.x > 0 && gl_FragCoord.x < 10 && gl_FragCoord.y > 710 && gl_FragCoord.y < 720) {
		gl_FragColor.rgb = test.rgb;
	}
	if (gl_FragCoord.x > 639 && gl_FragCoord.x < 641 && gl_FragCoord.y > 350 && gl_FragCoord.y < 370) {
		gl_FragColor.rgb = mix(vec3(1,1,1),gl_FragColor.rgb, 0.3);
	} else if (gl_FragCoord.x > 630 && gl_FragCoord.x < 650 && gl_FragCoord.y > 359 && gl_FragCoord.y < 361) {
		gl_FragColor.rgb = mix(vec3(1,1,1),gl_FragColor.rgb, 0.3);
	}
	
	vec2 position = (gl_FragCoord.xy / vec2(1280,720)) - vec2(0.5);
	
	//determine the vector length of the center position
	float len = length(position);
	
	//use smoothstep to create a smooth vignette
	float vignette = smoothstep(0.75, 0.75-0.45, len);
	
	//apply the vignette with 50% opacity
	gl_FragColor.rgb = mix(gl_FragColor.rgb, gl_FragColor.rgb * vignette, 0.5);
	
	gl_FragColor.rgb = mix(gl_FragColor.rgb, vec3(0), 1.0-vertexlight);
	
	if (makefragred > 0 && makefragred <= 1) {
		gl_FragColor.rgb = vec3(1,0,0);
	} else if (makefragred > 1.0 && makefragred <= 2) {
		gl_FragColor.rgb = vec3(0,0,1);
	} else if (makefragred > 2.0 && makefragred <= 3) {
		gl_FragColor.rgb = vec3(1,1,1);
	}
}