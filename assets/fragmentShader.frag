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


void main() {
	#if defined(normalFlag)
		vec3 normal = v_normal;
	#endif // normalFlag

	#if defined(diffuseTextureFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);//получаем цвет фрагмента из текстуры
	#elif defined(diffuseColorFlag)
		vec4 diffuse = u_diffuseColor;//делаем его как цвет модели если нема текстуры
	#else
		vec4 diffuse = vec4(1.0);//тупа делаем его белым
	#endif

	gl_FragColor.rgb = diffuse.rgb;

	#ifdef blendedFlag
		gl_FragColor.a = diffuse.a * v_opacity;
		#ifdef alphaTestFlag
			if (gl_FragColor.a <= v_alphaTest)
				discard;
		#endif
	#else
		gl_FragColor.a = 1.0;
	#endif

}
 