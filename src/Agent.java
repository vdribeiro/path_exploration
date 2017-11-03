
import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DTorus;

public class Agent implements Drawable {
	int id, x, y;
	Color color;
	Object2DTorus space;

	public Agent(int id, int x, int y, Color color, Object2DTorus space){
		this.id=id;
		this.x = x;
		this.y = y;
		this.color = color;
		this.space = space;
	}
	
	public boolean equals(Object o) {
		Agent a = (Agent) o;
		if ( this.id==a.id ) {
			return true;
		}
		return false;
	}
	
	public void draw(SimGraphics g) {
		g.drawFastCircle(color);
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

}
