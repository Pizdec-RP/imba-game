package net.pzdcrp.Hyperborea.data;

public class Vector3I {
	public int x,y,z;
	public Vector3I(int x, int y, int z) {
		this.x = x;
		this.y = y; 
		this.z = z;
	}
	
	public Vector3I() {
		this.x = 0;
		this.y = 0; 
		this.z = 0;
	}
	
	public Vector3I(Vector3D pos) {
		this.x = (int)pos.x;
		this.y = (int)pos.y; 
		this.z = (int)pos.z;
	}
	
	@Override
	public String toString() {
		return "Vec3I [x:"+x+" y:"+y+" z:"+z+"]";
	}
	
	public static Vector3D fromString(String s) {
		if (s.startsWith("Vec3I [") && s.endsWith("]")) {
			s = s.replace("Vec3I [", "");
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
}
