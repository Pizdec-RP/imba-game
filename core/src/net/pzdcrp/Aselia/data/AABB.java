package net.pzdcrp.Aselia.data;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.pzdcrp.Aselia.utils.MathU;

public class AABB {
	public float minX,minY,minZ,maxX,maxY,maxZ;

	public AABB(float x0, float y0, float z0, float x1, float y1, float z1) {
	    this.minX = x0;
	    this.minY = y0;
	    this.minZ = z0;
	    this.maxX = x1;
	    this.maxY = y1;
	    this.maxZ = z1;
	}

	public AABB(double x0, double y0, double z0, double x1, double y1, double z1) {
		this.minX = (float)x0;
	    this.minY = (float)y0;
	    this.minZ = (float)z0;
	    this.maxX = (float)x1;
	    this.maxY = (float)y1;
	    this.maxZ = (float)z1;
	}

	@Override
	public AABB clone() {
	    return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public AABB floor() {
	    this.minX = MathU.floor(this.minX);
	    this.minY = MathU.floor(this.minY);
	    this.minZ = MathU.floor(this.minZ);
	    this.maxX = MathU.floor(this.maxX);
	    this.maxY = MathU.floor(this.maxY);
	    this.maxZ = MathU.floor(this.maxZ);
	    return this;
	}

	public AABB grow(float x, float y, float z) {
        return new AABB(this.getMinX() - x, this.getMinY() - y, this.getMinZ() - z, this.getMaxX() + x, this.getMaxY() + y, this.getMaxZ() + z);
    }

	public List<Vector3D> getCorners() {
		List<Vector3D> c = new ArrayList<>();

		c.add(new Vector3D(minX,minY,minZ));
		c.add(new Vector3D(minX,maxY,minZ));
		c.add(new Vector3D(minX,minY,maxZ));
		c.add(new Vector3D(minX,maxY,maxZ));

		c.add(new Vector3D(maxX,minY,minZ));
		c.add(new Vector3D(maxX,maxY,minZ));
		c.add(new Vector3D(maxX,minY,maxZ));
		c.add(new Vector3D(maxX,maxY,maxZ));
		return c;
	}

	public AABB extend(float dx, float dy, float dz) {
	    if (dx < 0) this.minX += dx;
	    else this.maxX += dx;

	    if (dy < 0) this.minY += dy;
	    else this.maxY += dy;

	    if (dz < 0) this.minZ += dz;
	    else this.maxZ += dz;

	    return this;
	}

	public AABB offset(Vector3D a) {
		this.minX += a.x;
	    this.minY += a.y;
	    this.minZ += a.z;
	    this.maxX += a.x;
	    this.maxY += a.y;
	    this.maxZ += a.z;
	    return this;
	}

	public AABB noffset(Vector3D a) {
		return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ).offset(a);
	}

	public AABB contract(float x, float y, float z) {
	    this.minX += x;
	    this.minY += y;
	    this.minZ += z;
	    this.maxX -= x;
	    this.maxY -= y;
	    this.maxZ -= z;
	    return this;
	}

	public AABB expand(float x, float y, float z) {
	    this.minX -= x;
	    this.minY -= y;
	    this.minZ -= z;
	    this.maxX += x;
	    this.maxY += y;
	    this.maxZ += z;
	    return this;
	}

	public AABB offset(float x, float y, float z) {
	    this.minX += x;
	    this.minY += y;
	    this.minZ += z;
	    this.maxX += x;
	    this.maxY += y;
	    this.maxZ += z;
	    return this;
	}

	public float computeOffsetX (AABB other, float offsetX) {
	    if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
	      if (offsetX > 0.0 && other.maxX <= this.minX) {
	        offsetX = Math.min(this.minX - other.maxX, offsetX);
	      } else if (offsetX < 0.0 && other.minX >= this.maxX) {
	        offsetX = Math.max(this.maxX - other.minX, offsetX);
	      }
	    }
	    return offsetX;
	}

	public float computeOffsetY (AABB other, float offsetY) {
	    if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
	      if (offsetY > 0.0 && other.maxX <= this.minX) {
	        offsetY = Math.min(this.minX - other.maxX, offsetY);
	      } else if (offsetY < 0.0 && other.minX >= this.maxX) {
	        offsetY = Math.max(this.maxX - other.minX, offsetY);
	      }
	    }
	    return offsetY;
	}

	public float computeOffsetZ (AABB other, float offsetZ) {
	    if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
	      if (offsetZ > 0.0 && other.maxX <= this.minX) {
	        offsetZ = Math.min(this.minX - other.maxX, offsetZ);
	      } else if (offsetZ < 0.0 && other.minX >= this.maxX) {
	        offsetZ = Math.max(this.maxX - other.minX, offsetZ);
	      }
	    }
	    return offsetZ;
	}

	public boolean collide(AABB other) {
	    return this.minX < other.maxX && this.maxX > other.minX &&
	           this.minY < other.maxY && this.maxY > other.minY &&
	           this.minZ < other.maxZ && this.maxZ > other.minZ;
	}

	public boolean collide(Vector3D other) {
	    return this.minX < other.x && this.maxX > other.x &&
	           this.minY < other.y && this.maxY > other.y &&
	           this.minZ < other.z && this.maxZ > other.z;
	}

	public float getMinX() {
		return minX;
	}

	public void setMinX(float minX) {
		this.minX = minX;
	}

	public float getMinY() {
		return minY;
	}

	public void setMinY(float minY) {
		this.minY = minY;
	}

	public float getMinZ() {
		return minZ;
	}

	public void setMinZ(float minZ) {
		this.minZ = minZ;
	}

	public float getMaxX() {
		return maxX;
	}

	public void setMaxX(float maxX) {
		this.maxX = maxX;
	}

	public float getMaxY() {
		return maxY;
	}

	public void setMaxY(float maxY) {
		this.maxY = maxY;
	}

	public float getMaxZ() {
		return maxZ;
	}

	public void setMaxZ(float maxZ) {
		this.maxZ = maxZ;
	}

	public AABB setBB(AABB bb) {
        this.setMinX(bb.getMinX());
        this.setMinY(bb.getMinY());
        this.setMinZ(bb.getMinZ());
        this.setMaxX(bb.getMaxX());
        this.setMaxY(bb.getMaxY());
        this.setMaxZ(bb.getMaxZ());
        return this;
    }

	public float calculateXOffset(AABB bb, float x) {
        if (bb.getMaxY() <= this.getMinY() || bb.getMinY() >= this.getMaxY() || bb.getMaxZ() <= this.getMinZ() || bb.getMinZ() >= this.getMaxZ()) {
            return x;
        }
        if (x > 0 && bb.getMaxX() <= this.getMinX()) {
            float x1 = this.getMinX() - bb.getMaxX();
            if (x1 < x) {
                x = x1;
            }
        }
        if (x < 0 && bb.getMinX() >= this.getMaxX()) {
            float x2 = this.getMaxX() - bb.getMinX();
            if (x2 > x) {
                x = x2;
            }
        }

        return x;
    }

    public float calculateYOffset(AABB bb, float y) {
        if (bb.getMaxX() <= this.getMinX() || bb.getMinX() >= this.getMaxX() || bb.getMaxZ() <= this.getMinZ() || bb.getMinZ() >= this.getMaxZ()) {
            return y;
        }
        if (y > 0 && bb.getMaxY() <= this.getMinY()) {
            float y1 = this.getMinY() - bb.getMaxY();
            if (y1 < y) {
                y = y1;
            }
        }
        if (y < 0 && bb.getMinY() >= this.getMaxY()) {
            float y2 = this.getMaxY() - bb.getMinY();
            if (y2 > y) {
                y = y2;
            }
        }

        return y;
    }

    public float calculateZOffset(AABB bb, float z) {
        if (bb.getMaxX() <= this.getMinX() || bb.getMinX() >= this.getMaxX() || bb.getMaxY() <= this.getMinY() || bb.getMinY() >= this.getMaxY()) {
            return z;
        }
        if (z > 0 && bb.getMaxZ() <= this.getMinZ()) {
            float z1 = this.getMinZ() - bb.getMaxZ();
            if (z1 < z) {
                z = z1;
            }
        }
        if (z < 0 && bb.getMinZ() >= this.getMaxZ()) {
            float z2 = this.getMaxZ() - bb.getMinZ();
            if (z2 > z) {
                z = z2;
            }
        }

        return z;
    }

	@Override
	public String toString() {
		return "AABB [minX=" + minX + ", minY=" + minY + ", minZ=" + minZ + ", maxX=" + maxX + ", maxY=" + maxY + ", maxZ=" + maxZ + "]";
	}

	public static AABB fromString(String s) {
		if (s.startsWith("AABB [") && s.endsWith("]")) {
			s = s.replace("AABB [", "");//minX=" + minX + ", minY=" + minY + ", minZ=" + minZ + ", maxX=" + maxX + ", maxY=" + maxY + ", maxZ=" + maxZ + "
			s = s.replace("]", "");
			String[] arr = s.split(", ");
			float minx=0,miny=0,minz=0,maxx=0,maxy=0,maxz=0;
			for (String a : arr) {
				a = a.replace(", ", "");
				if (a.contains("minX=")) {
					a = a.replace("minX=", "");
					minx = Float.parseFloat(a);
				} else if (a.contains("minY=")) {
					a = a.replace("minY=", "");
					miny = Float.parseFloat(a);
				} else if (a.contains("minZ=")) {
					a = a.replace("minZ=", "");
					minz = Float.parseFloat(a);
				} else if (a.contains("maxX=")) {
					a = a.replace("maxX=", "");
					maxx = Float.parseFloat(a);
				} else if (a.contains("maxY=")) {
					a = a.replace("maxY=", "");
					maxy = Float.parseFloat(a);
				} else if (a.contains("maxZ=")) {
					a = a.replace("maxZ=", "");
					maxz = Float.parseFloat(a);
				}
			}
			return new AABB(minx,miny,minz,maxx,maxy,maxz);
		} else {
			return null;
		}
	}

	public AABB addCoord(float x, float y, float z) {
        float minX = this.getMinX();
        float minY = this.getMinY();
        float minZ = this.getMinZ();
        float maxX = this.getMaxX();
        float maxY = this.getMaxY();
        float maxZ = this.getMaxZ();

        if (x < 0) minX += x;
        if (x > 0) maxX += x;

        if (y < 0) minY += y;
        if (y > 0) maxY += y;

        if (z < 0) minZ += z;
        if (z > 0) maxZ += z;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

	public AABB setBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.setMinX(minX);
        this.setMinY(minY);
        this.setMinZ(minZ);
        this.setMaxX(maxX);
        this.setMaxY(maxY);
        this.setMaxZ(maxZ);
        return this;
    }

	public BoundingBox translate() {
		return new BoundingBox(new Vector3(minX, minY, minZ),new Vector3(maxX, maxY, maxZ));
	}


}
