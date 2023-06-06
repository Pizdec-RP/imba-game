package net.pzdcrp.Hyperborea.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.TextureData;

import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;

import com.badlogic.gdx.graphics.Texture;

public class Mutex {
	public Texture comp;
	Map<String, Texture> ar;
	Map<String, float[]> razmetka;
	
	public Mutex() {
		
	}
	
	public void begin() {
		ar = new HashMap<>();
		razmetka = new HashMap<>();
	}
	
	public void end() {
		int height = 0;
		int width = 0;
		for (Texture tex : ar.values()) {
			if (tex.getHeight() > height) height = tex.getHeight();
			width += tex.getWidth();
		}
		comp = new Texture(width, height, Format.RGBA8888);
		//comp.setFilter(TextureFilter.MipMap,TextureFilter.Nearest);
		int twidth = 0;
		TextureData dt;
		for (Entry<String, Texture> tex : ar.entrySet()) {
			dt = tex.getValue().getTextureData();
			dt.prepare();
			comp.draw(dt.consumePixmap(), twidth, 0);
			float[] razm = new float[4];
			
			razm[1] = 0;
			float hep = (float)tex.getValue().getHeight() / (float)height;
			razm[3] = hep;
			
			razm[0] = (float)twidth / (float)width;
			twidth+=tex.getValue().getWidth();
			razm[2] = (float)twidth / (float)width;
			
			razmetka.put(tex.getKey(), razm);
			//System.out.println(tex.getKey()+" 53f "+razm[0]+" "+razm[1]+" "+razm[2]+" "+razm[3]+" "+" "+tex.getValue().getWidth()+" "+tex.getValue().getHeight());
		}
	}
	
	public void addTexture(Texture t, String name) {
		ar.put(name,t);
	}
	
	public void hookuvr(SexyMeshBuilder mpb, String name, float u1, float v1, float u2, float v2) {
		float[] r = razmetka.get(name);
		float width = r[2] - r[0];
		float height = r[3] - r[1];
		
		float nu1 = r[0]+width*u1, nv1 = r[1]+height*v1, nu2 = r[0]+width*u2, nv2 = r[1]+height*v2;
		
		mpb.setUVRange(nu1, nv1,nu2,nv2);
	}
}
