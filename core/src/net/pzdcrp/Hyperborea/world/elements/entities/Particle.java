package net.pzdcrp.Hyperborea.world.elements.entities;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;

public class Particle {
    public Vector3 pos;
	private Vector3 before;
    private Vector3 vel;
    private int life;
    public ModelInstance modelInstance;
    private Texture texture;
    private float halfSize = 0.05f;

    public Particle(Texture texture, Vector3 position, Vector3 velocity, int life) {
        this.pos = position;
        before = pos.cpy();
        this.vel = velocity;
        this.life = life;
        this.texture = texture;
    }
    
    public void initModel() {
    	ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder = modelBuilder.part("particle", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(texture), IntAttribute.createCullFace(GL20.GL_NONE)));
        float x = MathU.rnd(0, 0.9);
        meshBuilder.setUVRange(x, x, x+0.1f, x+0.1f);
        meshBuilder.rect(
        		-halfSize, -halfSize, 0,
        		-halfSize, halfSize, 0,
        		+halfSize, +halfSize, 0,
        		halfSize, -halfSize, 0,
        		1, 1, 1);
        modelInstance = new ModelInstance(modelBuilder.end());
        modelInstance.userData = new Object[] {"c"};
    }
    
    public boolean grAAvity() {
    	return true;
    }
    
    public boolean collide() {
    	return true;
    }
    
    public List<AABB> getNearBlocks() {
		List<AABB> b = new ArrayList<>();
		
		for (int tx = (int) (pos.x-1); tx <= pos.x+1; tx++) {
			for (int tz = (int) (pos.z-1); tz <= pos.z+1; tz++) {
				for (int ty = (int) (pos.y-1); ty <= pos.y+1; ty++) {
					Block bl = Hpb.world.getBlock(Math.floor(tx), Math.floor(ty), Math.floor(tz));
					if (bl.isCollide()) {
						b.add(bl.getHitbox());
					}
				}
			}
		}
		return b;
	}

    public void update() {
    	if (modelInstance == null) return;
    	before.set(pos);
    	life -= 1;
        if (isDead()) {
        	Hpb.world.particles.remove(this);
        	return;
        }
        if (collide()) {
	    	if (vel.x != 0 || vel.y != 0 || vel.z != 0) {
				List<AABB> nb = getNearBlocks();
				for (AABB collidedBB : nb) {
					vel.y = (float) collidedBB.calculateYOffset(this.getHitbox(), vel.y);
				}
				this.pos.y += vel.y;
				
				for (AABB collidedBB : nb) {
					vel.x = (float) collidedBB.calculateXOffset(this.getHitbox(), vel.x);
				}
				this.pos.x += vel.x;
				
				for (AABB collidedBB : nb) {
					vel.z = (float) collidedBB.calculateZOffset(this.getHitbox(), vel.z);
				}
				this.pos.z += vel.z;
			}
        }
    	if (vel.y == 0 && collide()) {
			vel.x *= 0.6;
			vel.z *= 0.6;
		} else {
			vel.x *= 0.98;
			vel.z *= 0.98;
		}
    	if (grAAvity()) {
	    	vel.y -= DM.gravity;
	    	vel.y *= DM.airdrag;
    	}
    }

    private AABB getHitbox() {
		return new AABB(pos.x-halfSize,pos.y-halfSize,pos.z-halfSize,pos.x+halfSize,pos.y+halfSize,pos.z+halfSize);
	}

	public boolean isDead() {
        return life <= 0;
    }
	Vector3 temp = new Vector3();
	public void render() {
		if (modelInstance == null) initModel();
		if (!Hpb.world.player.cam.cam.frustum.pointInFrustum(pos)) return;
		temp.set(Hpb.lerp(before.x, pos.x),Hpb.lerp(before.y, pos.y),Hpb.lerp(before.z, pos.z));
		modelInstance.transform.setToTranslation(temp);
		Vector3 direction = Hpb.world.player.getEyeLocation().translate().sub(temp).nor();
		float yAngle = MathUtils.atan2(direction.x, direction.z) * MathUtils.radiansToDegrees;
		float xAngle = -MathUtils.atan2(direction.y, (float)Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * MathUtils.radiansToDegrees;
		modelInstance.transform.rotate(Vector3.Y, yAngle).rotate(Vector3.X, xAngle);

		Hpb.render(modelInstance);
		//System.out.println("render");
	}
}
