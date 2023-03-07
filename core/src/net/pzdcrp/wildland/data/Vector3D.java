package net.pzdcrp.wildland.data;

import com.badlogic.gdx.math.Vector3;

public class Vector3D {
	public double x;
	public double y;
	public double z;
	public int hasheddata = 0;

	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		//Main.allVec++;
		//BotU.log(Main.allVec+" vectors created lol");
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
	
	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}
	
	public double getPosX() {
		return this.x;
	}

	public double getPosY() {
		return this.y;
	}

	public double getPosZ() {
		return this.z;
	}
	
	public double getBlockX() {
		return this.x;
	}
	
	public double getBlockY() {
		return this.y;
	}
	
	public double getBlockZ() {
		return this.z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public void addX(double i) {
		this.x +=i;
	}
	
	public void addY(double i) {
		this.y +=i;
	}
	
	public void addZ(double i) {
		this.z +=i;
	}
	
	public Vector3D floorXZ() {
		this.x = Math.floor(x);
		this.z = Math.floor(z);
		return this;
	}
	
	public void origin() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector3D func_vf() {
		this.x = Math.floor(x);
		this.y = Math.floor(y);
		this.z = Math.floor(z);
		return this;
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

	public Vector3D add(double x, double y, double z) {
		return new Vector3D(this.x + x, this.y + y, this.z + z);
	}
	
	public Vector3D add(double s) {
		return new Vector3D(this.x + s, this.y + s, this.z + s);
	}

	public Vector3D subtract(Vector3D other) {
		if (other == null) throw new IllegalArgumentException("other cannot be NULL");
		return new Vector3D(x - other.x, y - other.y, z - other.z);
	}

	public Vector3D subtract(double x, double y, double z) {
		return new Vector3D(this.x - x, this.y - y, this.z - z);
	}

	public Vector3D multiply(int factor) {
		return new Vector3D(x * factor, y * factor, z * factor);
	}

	public Vector3D multiply(double factor) {
		return new Vector3D(x * factor, y * factor, z * factor);
	}

	public Vector3D divide(int divisor) {
		if (divisor == 0) throw new IllegalArgumentException("Cannot divide by null.");
		return new Vector3D(x / divisor, y / divisor, z / divisor);
	}

	public Vector3D divide(double divisor) {
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
	
	public static Vector3D fromString(String s) {
		if (s.startsWith("Vec3D [") && s.endsWith("]")) {
			s = s.replace("Vec3D [", "");
			s = s.replace("]", "");
			double X=0,Y=0,Z=0;
			for (String a : s.split(" ")) {
				if (a.contains("x:")) {
					a = a.replace("x:", "");
					X = Double.parseDouble(a);
				} else if (a.contains("y:")) {
					a = a.replace("y:", "");
					Y = Double.parseDouble(a);
				} else if (a.contains("z:")) {
					a = a.replace("z:", "");
					Z = Double.parseDouble(a);
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
	
	public double distanceSq(double toX, double toY, double toZ) {
        double var7 = (double)this.getX() - toX;
        double var9 = (double)this.getY() - toY;
        double var11 = (double)this.getZ() - toZ;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }
	
	public double distanceSq(Vector3D to) {
        return this.distanceSq((double)to.getX(), (double)to.getY(), (double)to.getZ());
    }
	
	public Vector3D normalize() {
        double length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }
	
	public double length() {
        return Math.sqrt(this.lengthSquared());
    }
	
	public Vector3D clone() {
		return new Vector3D(x,y,z);
	}
	
	/*public double heuristic(int x, int y, int z) {
        int xDiff = (int) (x - this.x);
        int yDiff = (int) (y - this.y);
        int zDiff = (int) (z - this.z);
        return calculate(xDiff, yDiff, zDiff);
    }
	
	public static double calculate(double xDiff, int yDiff, double zDiff) {
        double heuristic = 0;
        heuristic += gylcalculate(yDiff, 0);
        heuristic += xzcalculate(xDiff, zDiff);
        return heuristic;
    }*/
	
	public static double xzcalculate(double xDiff, double zDiff) {
        double x = Math.abs(xDiff);
        double z = Math.abs(zDiff);
        double straight;
        double diagonal;
        if (x < z) {
            straight = z - x;
            diagonal = x;
        } else {
            straight = x - z;
            diagonal = z;
        }
        diagonal *= Math.sqrt(2);
        return (diagonal + straight) * 3.5D;
    }
	
	public static Vector3D translate(Vector3 p) {
		return new Vector3D(p.x,p.y,p.z);
	}
	
	public Vector3 translate() {
		return new Vector3((float)x, (float)y, (float)z);
	}

	public void setComponents(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public void addComponents(double x, double y, double z) {
		this.x+=x;
		this.y+=y;
		this.z+=z;
	}

	public void addComponents(Vector3D l) {
		this.x+=l.x;
		this.y+=l.y;
		this.z+=l.z;
	}

	public double lengthSquared() {
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
}
