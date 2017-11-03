
import java.awt.Color;

import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DTorus;

public class WallAgent extends Agent{

	public WallAgent(int id, int x, int y, Color color, Object2DTorus space) {
		super(id, x, y, color, space);
	}
	
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
	}

}
