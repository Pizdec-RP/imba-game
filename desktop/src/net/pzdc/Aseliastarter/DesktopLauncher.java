package net.pzdc.Aseliastarter;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.pzdcrp.Aselia.Hpb;
import net.pzdcrp.Aselia.data.Vector3D;
import net.pzdcrp.Aselia.utils.GameU;
import net.pzdcrp.Aselia.utils.MathU;
import net.pzdcrp.Aselia.utils.VectorU;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static final String ver = "0.1.30";
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(120);
		config.setWindowedMode(1280, 720);//1280, 720
		config.setTitle("Aselia "+ver);
		new Lwjgl3Application(new Hpb(), config);
		//GameU.log(VectorU.sqrt(new Vector3D(-5, -6, -7), new Vector3D(-10, -12, -5)));
		/*for (float i = 0; i < 10; i+=0.5f) {
			GameU.log(MathU.sin(i));
		}*/
	}
}
