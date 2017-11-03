
import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.space.Object2DTorus;

public class Soldier extends WalkingAgent {
	
	Radio radio;
	
	public Soldier(int id, int x, int y, Color color, Object2DTorus space, Radio radio) {
		super(id, x, y, color, space);
		this.radio = radio;
	}
	
	public boolean communicate(ArrayList<WalkingAgent> agentList) {
		System.out.println("Sending Transmission");
		int rad = radio.radius;
		if (rad==0) rad=5000;
		for (int i=0; i<agentList.size(); i++) {
			if (radio.battery<=0) break;
			WalkingAgent agn = agentList.get(i);
			if ( (agn.x>x-rad) && (agn.x<x+rad) ) {
				if ( (agn.y>y-rad) && (agn.y<y+rad) ) {
					if (agn.updateComRadio()) {
						radio.battery--;
						merge(agn.cross);
						agn.merge(cross);
					}
				}
			}
		}
		return true;
	}
	
	public boolean updateComRadio() {
		if (radio.battery<=0) {
			return false;
		}
		radio.battery--;
		return true;
	}
	
	public void printStatus() {
		System.out.println("Soldier");
		super.printStatus();
	}

}
