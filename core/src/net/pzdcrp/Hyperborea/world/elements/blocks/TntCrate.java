package net.pzdcrp.Hyperborea.world.elements.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;

import net.pzdcrp.Hyperborea.Hpb;
import net.pzdcrp.Hyperborea.data.BlockFace;
import net.pzdcrp.Hyperborea.data.MBIM;
import net.pzdcrp.Hyperborea.data.Vector3D;
import net.pzdcrp.Hyperborea.data.MBIM.offset;
import net.pzdcrp.Hyperborea.extended.SexyMeshBuilder;
import net.pzdcrp.Hyperborea.player.Player;
import net.pzdcrp.Hyperborea.utils.MathU;
import net.pzdcrp.Hyperborea.utils.ModelUtils;
import net.pzdcrp.Hyperborea.utils.VectorU;
import net.pzdcrp.Hyperborea.world.elements.blocks.Block.BlockType;
import net.pzdcrp.Hyperborea.world.elements.entities.Entity;
import net.pzdcrp.Hyperborea.world.elements.entities.Particle;

public class TntCrate extends Block {
	public static String tname = "tntcrate";
	static final int rays = 16;
	static final double stepLen = 0.3d;
	static final int size = 4;
	public TntCrate(Vector3D pos) {
		super(pos,tname);
	}
	
	@Override
	public void addModel(boolean py, boolean ny, boolean nx, boolean px, boolean nz, boolean pz, MBIM mbim) {
		if (!py || !ny || !nx || !px || !nz || !pz) {
			SexyMeshBuilder a = mbim.obtain(pos);
			ModelUtils.setTransform(pos);
			Hpb.mutex.hookuvr(a, tname, 0, 0, 0.5f, 0.5f);
			mbim.curoffset = offset.py;
	    	if (!py) ModelUtils.buildTopX(a);//PY
	    	//bottom texture
	    	Hpb.mutex.hookuvr(a, tname, 0, 0.5f, 0.5f, 1);
	    	mbim.curoffset = offset.nx;
		    if (!nx) ModelUtils.buildLeftPY(a);//NX
		    mbim.curoffset = offset.px;
		    if (!px) ModelUtils.buildRightPY(a);//PX
		    mbim.curoffset = offset.nz;
		    if (!nz) ModelUtils.buildFrontY(a);//NZ
		    mbim.curoffset = offset.pz;
		    if (!pz) ModelUtils.buildBackY(a);//PZ
		    //down texture
		    Hpb.mutex.hookuvr(a, tname, 0.5f, 0.5f, 1, 1);
		    mbim.curoffset = offset.ny;
		    if (!ny) ModelUtils.buildBottomX(a);//NY
		}
	}
	
	@Override
	public BlockType getType() {
		return BlockType.solid;
	}
	
	@Override
	public Block clone(Vector3D poss) {
		return new TntCrate(poss);
	}
	
	private boolean fired = false;
	private int timer = 90;//ticks
	@Override
	public void tick() {
		if (!fired) return;
		if (timer>0) {
			System.out.println(timer);
			timer--;
			world.particles.add(new Particle(Hpb.mutex.getBlockTexture("firebase"), pos.translate().add(0.5f, 1f, 0.5f), new Vector3(MathU.rndf(0.2f, -0.2f),MathU.rndf(0.6f, 0.1f),MathU.rndf(0.2f, -0.2f)), 60));
			return;
		}
		world.setBlock(new Air(this.pos));
		explode();
	}
	
	@Override
	public boolean onClick(Entity actor) {
		if (actor instanceof Player) {
			Player p = (Player)actor;
			if (p.down) {
				return false;
			} else {
				this.callChunkUpdate();
				fired = true;
				return true;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean tickable() {
		return fired;
	}
	
	public void explode() {
		List<Vector3D> affectedBlocks = new ArrayList<>();
		Vector3D vector = new Vector3D(0, 0, 0);
        Vector3D vBlock = new Vector3D(0, 0, 0);
        
		int mRays = rays - 1;
        for (int i = 0; i < rays; ++i) {
            for (int j = 0; j < rays; ++j) {
                for (int k = 0; k < rays; ++k) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents((double) i / (double) mRays * 2d - 1, (double) j / (double) mRays * 2d - 1, (double) k / (double) mRays * 2d - 1);
                        double len = vector.length();
                        vector.setComponents((vector.x / len) * stepLen, (vector.y / len) * stepLen, (vector.z / len) * stepLen);
                        double pointerX = this.pos.x;
                        double pointerY = this.pos.y;
                        double pointerZ = this.pos.z;

                        for (double blastForce = size * (ThreadLocalRandom.current().nextInt(700, 1301)) / 1000d; blastForce > 0; blastForce -= stepLen * 0.75d) {
                            int x = (int) pointerX;
                            int y = (int) pointerY;
                            int z = (int) pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;
                            if (vBlock.y < 0 || vBlock.y > 255) {
                                break;
                            }
                            Block block = world.getBlock(vBlock);

                            if (block.isCollide()) {
                                blastForce -= (block.getResistance() / 5 + 0.3d) * stepLen;
                                if (blastForce > 0) {
                                    if (!affectedBlocks.contains(block.pos)) {
                                        affectedBlocks.add(block.pos);
                                    }
                                }
                            }
                            pointerX += vector.x;
                            pointerY += vector.y;
                            pointerZ += vector.z;
                        }
                    }
                }
            }
        }
        
        for (Vector3D block : affectedBlocks) {
        	Block b = world.getBlock(block);
        	if (b instanceof TntCrate) {
        		TntCrate bb = (TntCrate)b;
        		bb.timer = 1;
        		bb.fired = true;
        	} else {
        		world.breakBlock(block);
        	}
        }
        /*double dr = size*2.5;
        for (Entity e : world.getEntities(pos, dr)) {
        	System.out.println("en");
        	Vector3D dir = pos.subtract(e.pos).normalize();
        	double amp = dr - VectorU.sqrt(pos, e.pos);
        	Vector3D t = dir.multiply(amp);
        	System.out.println(t.toString());
        	e.vel.add(t);
        }*/
	}
}
