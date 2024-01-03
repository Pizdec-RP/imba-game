package net.pzdcrp.Aselia.data;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;

import io.netty.buffer.ByteBuf;
import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.world.World;
import net.pzdcrp.Aselia.world.elements.Chunk;
import net.pzdcrp.Aselia.world.elements.Column;

public class Vector3D {
	public static final Vector3D ZERO = new Vector3D();
	public float x, y, z;
	public static int test = 0;

	public Vector3D() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		test++;
	}

	public Vector3D(ByteBuf byteBuf) {
		this.x = byteBuf.readFloat();
		this.y = byteBuf.readFloat();
		this.z = byteBuf.readFloat();
		test++;
	}

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		test++;
	}

	public Vector3D(Vector3 p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
		test++;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (int)x;
		hash = 31 * hash + (int)y;
		hash = 31 * hash + (int)z;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector3D)) {
			return false;
		}
		Vector3D co = (Vector3D) obj;
		return co.x == x && co.y == y && co.z == z;
	}

	public Vector3D down() {
		return new Vector3D(x,y-1,z);
	}

	public Vector3D up() {
		return new Vector3D(x,y+1,z);
	}

	public Vector3D up(float d) {
		return new Vector3D(x,y+d,z);
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public void setZero() {
		this.x = 0f;
		this.y = 0f;
		this.z = 0f;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public float getPosX() {
		return this.x;
	}

	public float getPosY() {
		return this.y;
	}

	public float getPosZ() {
		return this.z;
	}

	public float getBlockX() {
		return this.x;
	}

	public float getBlockY() {
		return this.y;
	}

	public float getBlockZ() {
		return this.z;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void addX(float i) {
		this.x +=i;
	}

	public void addY(float i) {
		this.y +=i;
	}

	public void addZ(float i) {
		this.z +=i;
	}

	public Vector3D floorXZ() {
		this.x = MathU.floor(x);
		this.z = MathU.floor(z);
		return this;
	}

	public void origin() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vector3D func_vf() {
		this.x = MathU.floor(x);
		this.y = MathU.floor(y);
		this.z = MathU.floor(z);
		return this;
	}

	public Vector3D getDirection(Vector3D to) {
		return to.subtract(this).normalize();
	}

	public Vector3D floor() {
		return this.clone().func_vf();
	}

	public Vector3D VecToInt() {
		return new Vector3D((int)Math.floor(x),(int)Math.floor(y),(int)Math.floor(z));
	}


	public Vector3D add(Vector3D other) {
		if (other == null) throw new IllegalArgumentException("other cannot be NULL");
		return new Vector3D(x + other.x, y + other.y, z + other.z);
	}

	public Vector3D add(float x, float y, float z) {
		return new Vector3D(this.x + x, this.y + y, this.z + z);
	}

	public Vector3D add(float s) {
		return new Vector3D(this.x + s, this.y + s, this.z + s);
	}

	public Vector3D subtract(Vector3D other) {
		if (other == null) throw new IllegalArgumentException("other cannot be NULL");
		return new Vector3D(x - other.x, y - other.y, z - other.z);
	}

	public Vector3D subtract(float x, float y, float z) {
		return new Vector3D(this.x - x, this.y - y, this.z - z);
	}

	public Vector3D multiply(int factor) {
		return new Vector3D(x * factor, y * factor, z * factor);
	}

	public Vector3D multiply(float factor) {
		return new Vector3D(x * factor, y * factor, z * factor);
	}

	public Vector3D divide(int divisor) {
		if (divisor == 0) throw new IllegalArgumentException("Cannot divide by null.");
		return new Vector3D(x / divisor, y / divisor, z / divisor);
	}

	public Vector3D divide(float divisor) {
		if (divisor == 0) throw new IllegalArgumentException("Cannot divide by null.");
		return new Vector3D(x / divisor, y / divisor, z / divisor);
	}

	public Vector3D abs() {
		return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
	}

	@Override
	public String toString() {
		return "Vec3D [x:"+x+" y:"+y+" z:"+z+"]";
	}

	public Vector3D[] bsides() {
		return new Vector3D[] {add(1, 0, 0),add(0, 0, 1),add(-1, 0, 0),
				add(0, 0, -1),add(0, 1, 0),add(0, -1, 0)};
	}

	public List<Vector3D> sides() {
		Vector3D target = this.floor().add(0.5f, 0.5f, 0.5f);
		List<Vector3D> sides = new ArrayList<>() {{
			add(target.add(1, 0, 0));
			add(target.add(0, 0, 1));
			add(target.add(-1, 0, 0));
			add(target.add(0, 0, -1));
			add(target.add(0, 1, 0));
			add(target.add(0, -1, 0));
		}};
		//System.out.println("-------");
		//for (Vector3D side : sides) {
		//	System.out.println(side.toString());
		//}
		return sides;
	}

	public List<Chunk> getSidesChunks() {
		List<Chunk> chunks = new ArrayList<>();
		Chunk thiss = Hpb.world.getColumn(x, z).chunks[(int)y/16];
		for (Vector3D side : sides()) {
			Column col = Hpb.world.loadedColumns.get(new Vector2I((int)x >> 4, (int)z >> 4));
			if (col == null) continue;
			Chunk c = Hpb.world.getColumn(side.x, side.z).chunks[(int)side.y/16];
			if (c != thiss && !chunks.contains(c)) chunks.add(c);
		}
		return chunks;
	}

	public static Vector3D fromString(String s) {
		if (s.startsWith("Vec3D [") && s.endsWith("]")) {
			s = s.replace("Vec3D [", "");
			s = s.replace("]", "");
			float X=0,Y=0,Z=0;
			for (String a : s.split(" ")) {
				if (a.contains("x:")) {
					a = a.replace("x:", "");
					X = Float.parseFloat(a);
				} else if (a.contains("y:")) {
					a = a.replace("y:", "");
					Y = Float.parseFloat(a);
				} else if (a.contains("z:")) {
					a = a.replace("z:", "");
					Z = Float.parseFloat(a);
				}
			}
			return new Vector3D(X,Y,Z);
		} else {
			return null;
		}
	}

	public String toStringInt() {
		return "x:"+(int)Math.floor(x)+" y:"+(int)Math.floor(y)+" z:"+(int)Math.floor(z);
	}

	public String forCommand() {
		return (int)Math.floor(x)+" "+(int)Math.floor(y)+" "+(int)Math.floor(z);
	}

	public String forCommandD() {
		return x+" "+y+" "+z;
	}

	public float distanceSq(float toX, float toY, float toZ) {
        float var7 = this.getX() - toX;
        float var9 = this.getY() - toY;
        float var11 = this.getZ() - toZ;
        return MathU.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
    }

	public float distanceSq(Vector3D to) {
        return this.distanceSq(to.getX(), to.getY(), to.getZ());
    }

	public Vector3D normalize() {
        float length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

	public float length() {
        return MathU.sqrt(this.lengthSquared());
    }

	@Override
	public Vector3D clone() {
		return new Vector3D(x,y,z);
	}

	/*public float heuristic(int x, int y, int z) {
        int xDiff = (int) (x - this.x);
        int yDiff = (int) (y - this.y);
        int zDiff = (int) (z - this.z);
        return calculate(xDiff, yDiff, zDiff);
    }

	public static float calculate(float xDiff, int yDiff, float zDiff) {
        float heuristic = 0;
        heuristic += gylcalculate(yDiff, 0);
        heuristic += xzcalculate(xDiff, zDiff);
        return heuristic;
    }*/

	public static float xzcalculate(float xDiff, float zDiff) {
        float x = Math.abs(xDiff);
        float z = Math.abs(zDiff);
        float straight;
        float diagonal;
        if (x < z) {
            straight = z - x;
            diagonal = x;
        } else {
            straight = x - z;
            diagonal = z;
        }
        diagonal *= Math.sqrt(2);
        return (diagonal + straight) * 3.5f;
    }

	public static Vector3D translate(Vector3 p) {
		return new Vector3D(p.x,p.y,p.z);
	}

	public Vector3 translate() {
		return new Vector3(x, y, z);
	}

	public void setComponents(float x, float y, float z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}

	public void set(Vector3D a) {
		this.x=a.x;
		this.y=a.y;
		this.z=a.z;
	}

	public void addComponents(float x, float y, float z) {
		this.x+=x;
		this.y+=y;
		this.z+=z;
	}

	public void addComponents(Vector3D l) {
		this.x+=l.x;
		this.y+=l.y;
		this.z+=l.z;
	}

	public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

	public Vector3D round() {
        return new Vector3D(Math.round(this.x), Math.round(this.y), Math.round(this.z));
    }

	public Vector3D[] faces() {
		return new Vector3D[] {
			this.add(1, 0, 0), this.add(0, 1, 0), this.add(0, 0, 1),
			this.add(-1, 0, 0), this.add(0, -1, 0), this.add(0, 0, -1)
		};
	}

	public void writeBuffer(ByteBuf byteBuf) {
		byteBuf.writeFloat(x);
		byteBuf.writeFloat(y);
		byteBuf.writeFloat(z);
	}

	public void callChunkUpdate(World world) {
		world.getColumn(x, z).chunks[(int) (Math.floor(y)/16)].updateModel();
	}
}
