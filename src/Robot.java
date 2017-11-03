
import java.awt.Color;
import uchicago.src.sim.space.Object2DTorus;

public class Robot extends WalkingAgent {

	int battery;
	
	public Robot(int id, int x, int y, Color color, Object2DTorus space, int battery) {
		super(id, x, y, color, space);
		this.battery=battery;
	}

}
