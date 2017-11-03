
public class Node {
	
	public byte parentdir, current;
	public boolean U, R, D, L;
	public int x, y;
	
	//public Node parent;
	//public ArrayList<Node> child;
	
	Node(int px, int py) {
		x=px;
		y=py;
		
		parentdir = 0x00;
		current = 0x00;
		
		U = false;
		R = false; 
		D = false;
		L = false;
	}
	
	Node(Node node, boolean flag) {
		x=node.x;
		y=node.y;
		
		parentdir = node.parentdir;
		
		if (flag) {
			current = node.current;
		} else {
			current = WalkingAgent.NONE;
		}
		
		U = node.U;
		R = node.R; 
		D = node.D;
		L = node.L;
	}
	
	public boolean equals(Object o) {
		Node node = (Node) o;
		
		if (this.x==node.x) 
			if (this.y==node.y)
				return true;
		
		return false;
	}
	
}
