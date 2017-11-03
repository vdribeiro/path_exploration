import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.Random;

public class WalkingAgent extends Agent {
	static final byte NONE = 0x00;
	static final byte UP = 0x01;
	static final byte RIGHT = 0x02;
	static final byte DOWN = 0x04;
	static final byte LEFT = 0x08;
    
	int U, R, D, L, dir;
	byte last;
	
	boolean update;
	
	ArrayList<Node> cross;
	ArrayList<WalkingAgent> reachable;
	
	public WalkingAgent(int id, int x, int y, Color color, Object2DTorus space) {
		super(id, x, y, color, space);
		
		this.update=true;
		
		this.cross = new ArrayList<Node>();
		this.reachable = new ArrayList<WalkingAgent>();
		
		this.U=0;
		this.R=0;
		this.D=0;
		this.L=0;
		this.dir=0;
		
		this.last=DOWN;
	}
	
	public void draw(SimGraphics g) {
		g.drawFastRect(color);
	}
	
	public void printStatus() {
		System.out.println("id: " + this.id);
		
		System.out.println("x: " + this.x/2);
		System.out.println("y: " + this.y/2);
		
		System.out.println("U: " + this.U);
		System.out.println("R: " + this.R);
		System.out.println("D: " + this.D);
		System.out.println("L: " + this.L);
		
		System.out.println("Last: " + this.last);
		
		System.out.println("Crosslist");
		for (int i = 0; i < cross.size(); i++) {
			Node node = cross.get(i);
			System.out.println(node.x);
			System.out.println(node.y);
			System.out.println(node.parentdir);
			System.out.println(node.current);
			System.out.println(node.U);
			System.out.println(node.R);
			System.out.println(node.D);
			System.out.println(node.L);
		}
	}
	
	// reset agent
	public void reset() {
		this.x=1;
		this.y=1;
		
		this.cross.clear();
		this.reachable.clear();
		
		this.U=0;
		this.R=0;
		this.D=0;
		this.L=0;
		this.dir=0;
		
		this.last=DOWN;
		
		this.update=true;
	}
	
	//update local information
	public void updateVision(int U, int R, int D, int L) {
		this.U=U;
		this.R=R;
		this.D=D;
		this.L=L;
		this.dir=getNumOfDir();
	}
	
	//update crossroads info from reachables
	public void updateCross() {
		if (reachable.isEmpty()) return;
		
		for (int i=0;i<reachable.size();i++) {
			//System.out.println("Reachable: " + reachable.get(i).id);
			ArrayList<Node> nodes = reachable.get(i).cross;
			for (int j=0;j<nodes.size();j++) {
				Node node = nodes.get(j);
				int index=cross.indexOf(node);
				if (index==-1) {
					cross.add(new Node(node,false));
				} else {
					Node crossnode = cross.get(index);
					if (node.U) crossnode.U=true;
					if (node.R) crossnode.R=true;
					if (node.D) crossnode.D=true;
					if (node.L) crossnode.L=true;
				}
			}
		}
		reachable.clear();
	}
	
	public void merge(ArrayList<Node> merge) {
		for(int i = 0; i < merge.size(); i++) {
			Node node = merge.get(i);
			int index = cross.indexOf(node);
			if (index<0) {
				cross.add(new Node(node,false));
			} else {
				Node unode = cross.get(index);
				if (node.U) unode.U = true;
				if (node.R) unode.R = true;
				if (node.D) unode.D = true;
				if (node.L) unode.L = true;
			}
		}
	}
	
	//get number of directions
	private int getNumOfDir() {
		int dir=0;
		if (U>0) dir++;
		if (D>0) dir++;
		if (R>0) dir++;
		if (L>0) dir++;
		return dir;
	}
	
	public boolean communicate(ArrayList<WalkingAgent> agentList) {return false;}
	public boolean updateComRadio() {return false;}
	public boolean updateComTel() {return false;}
	
	public void walk() {
		
		/*if ((x<1) || (y<1) ) {
			reset();
		}*/
		
		if (dir==1) {
			//dead end
			rotateDir(2);
		} else if (dir==2) {
			//continue
		} else if (dir>2) {
			//crossroads
			Node node = cruNode();
			ArrayList<Byte> bdir = new ArrayList<Byte>();
			byte start = WalkingAgent.NONE;
			if (!node.U) {
				bdir.add(WalkingAgent.UP); 
			}
			if (!node.R) {
				bdir.add(WalkingAgent.RIGHT);
			}
			if (!node.D) {
				bdir.add(WalkingAgent.DOWN);
			}
			if (!node.L) {
				bdir.add(WalkingAgent.LEFT);
			}
			
			// if there are no possible directions
			// backtracks node
			// else choose random direction
			int size = bdir.size();
			if (size==0) {
				node.current=start;
				last=node.parentdir;
			} else {
				int rnd = Random.uniform.nextIntFromTo(0, size-1);
				node.current=bdir.get(rnd);
				last=node.current;
			}
			
		} else {
			// nowhere to go
			return;
		}
		
		go();
		
		//printStatus();
		space.putObjectAt(this.x, this.y, this);
	}
	
	// walk one time
	public void go() {
		switch(last) {
			case UP:
				if (U>0) {
					this.y=this.y-(2*U);
				} else {
					rotateDir();
					go();
				}
				break;
			case RIGHT:
				if (R>0) {
					this.x=this.x+(2*R);
				} else {
					rotateDir();
					go();
				}
				break;
			case DOWN:
				if (D>0) {
					this.y=this.y+(2*D);
				} else {
					rotateDir();
					go();
				}
				break;
			case LEFT:
				if (L>0) {
					this.x=this.x-(2*L);
				} else {
					rotateDir();
					go();
				}
				break;
			default:
				break;
		}
	}
	
	//rotate to next possible direction
	private void rotateDir() {
		byte bd = last;
		for (int i=0; i<2; i++) {
			bd = (byte) (bd << 1);
			if (bd>LEFT) bd = UP;
		}
		
		boolean flag=false;
		final byte b = bd;
		do {
			last = (byte) (last << 1);
			if (last>LEFT) last = UP;
			switch(last) {
				case UP:
					if (U==0) continue;
					else flag = true;
					break;
				case RIGHT:
					if (R==0) continue;
					else flag = true;
					break;
				case DOWN:
					if (D==0) continue;
					else flag = true;
					break;
				case LEFT:
					if (L==0) continue;
					else flag = true;
					break;
				default:
					flag=false;
					break;
			}
			if (b==last) flag = false;
		} while (!flag);
	}
	
	//rotate direction n times
	private void rotateDir(int n) {		
		for (int i=0; i<n; i++) {
			last = (byte) (last << 1);
			if (last>LEFT) last = UP;
		}
	}
	
	public Node cruNode() {
		Node node = new Node(x/2,y/2);
		int index = cross.indexOf(node);
		
		if (index<0) {
			// new crossroad - create node
			byte bd = last;
			for (int i=0; i<2; i++) {
				bd = (byte) (bd << 1);
				if (bd>LEFT) bd = UP;
			}
			final byte b = bd;
			node.parentdir=b;
			node.current=NONE;
			switch(b) {
				case UP:
					node.U=true;
					break;
				case RIGHT:
					node.R=true;
					break;
				case DOWN:
					node.D=true;
					break;
				case LEFT:
					node.L=true;
					break;
				default:
					break;
			}
			if (U==0) node.U = true;
			if (R==0) node.R = true;
			if (D==0) node.D = true;
			if (L==0) node.L = true;
			cross.add(node);
		} else {
			// already knows this crossroad
			node = cross.get(index);
			if (!update) {
				return node;
			}
			switch(node.current) {
				case UP:
					node.U=true;
					break;
				case RIGHT:
					node.R=true;
					break;
				case DOWN:
					node.D=true;
					break;
				case LEFT:
					node.L=true;
					break;
				default:
					break;
			}
			node.current=NONE;
		}
		return node;
	}

}
