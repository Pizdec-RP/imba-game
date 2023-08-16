package net.pzdcrp.Hyperborea.world.elements;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.AABB;
import net.pzdcrp.Hyperborea.data.DM;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block;

public class Particle {
	protected static int id = 0;
    public Vector3 pos;
	private Vector3 before;
    private Vector3 vel;
    private int life;
    private Texture texture;
    private float halfSize = 0.05f;
    private Node node;
    private boolean ready = false;

    public Particle(Texture texture, Vector3 position, Vector3 velocity, int life) {
        this.pos = position;
        before = pos.cpy();
        this.vel = velocity;
        this.life = life;
        this.texture = texture;
    }
    
    public void initNode() {
    	Mesh mesh = new Mesh(
		    true,
		    4, 6,
		    VertexAttribute.Position(),
		    new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0")
		);
    	float x = MathU.rndf(0, 0.9f);
		float[] vertices = new float[] {
		    -halfSize, -halfSize, 0, x, x,
		    -halfSize, halfSize, 0, x, x + 0.1f,
		    halfSize, halfSize, 0, x + 0.1f, x + 0.1f,
		    halfSize, -halfSize, 0, x + 0.1f, x
		};
		short[] indices = new short[] {
		    0, 1, 2,
		    2, 3, 0
		};
		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		MeshPart meshPart = new MeshPart();
		meshPart.mesh = mesh;
		meshPart.offset = 0;
		meshPart.size = mesh.getNumIndices();
		meshPart.primitiveType = GL20.GL_TRIANGLES;

		Node node = new Node();
		node.id = "ptcl" + id();
		node.parts.add(new NodePart(meshPart, new Material(TextureAttribute.createDiffuse(texture), IntAttribute.createCullFace(GL20.GL_NONE), new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))));
		
		Hpb.world.particlesModel.nodes.add(node);
		
		this.node = node;
		
        ready=true;
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
    	if (!ready) return;
    	before.set(pos);
    	life -= 1;
        if (isDead()) {
        	Hpb.world.particles.remove(this);
        	Hpb.world.particlesModel.nodes.removeValue(node, false);
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
	    if (node == null) initNode();
	    
	    //if (!Hpb.world.player.cam.cam.frustum.pointInFrustum(pos)) return;
	    
	    temp.set(Hpb.lerp(before.x, pos.x), Hpb.lerp(before.y, pos.y), Hpb.lerp(before.z, pos.z));
	    node.translation.set(temp);
	    
	    Vector3 direction = Hpb.world.player.getEyeLocation().translate().sub(temp).nor();
	    float yaw = MathUtils.atan2(direction.x, direction.z) * MathUtils.radiansToDegrees;
	    float pitch = -MathUtils.atan2(direction.y, (float)Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * MathUtils.radiansToDegrees; 

	    node.rotation.setEulerAngles(yaw, pitch, 0f);
	    node.calculateTransforms(true);
	    //node.localTransform.rotate(Vector3.Y, yAngle).rotate(Vector3.X, xAngle);
	}
	
	public static int id() {
		return id++;
	}
}
