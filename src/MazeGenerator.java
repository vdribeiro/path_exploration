
public class MazeGenerator {
	int[][] cell;
  
	int[][] 
	L,   //  connects left  0, 1, or 2
	U,   //  connects up    0, 1, or 2
	R,   //  connects right 0, 1, or 2 
	D;   //  connects down  0, 1, or 2

	int X, Y;
	int W=0, H=0;  
	boolean done=false;

	char  direc[]=new char[4] ;
	int   dist []=new  int[4], options ;
  
	public MazeGenerator(int width, int height) {
		W=width;
		H=height;
	}

	public void printMaze() {
		System.out.println("Cell:");
		for (int i=0; i<H;i++) {
			for (int j=0; j<W;j++) {
				System.out.print(cell[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("L:");
		for (int i=0; i<H;i++) {
			for (int j=0; j<W;j++) {
				System.out.print(L[j][i] + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("U:");
		for (int i=0; i<H;i++) {
			for (int j=0; j<W;j++) {
				System.out.print(U[j][i] + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("R:");
		for (int i=0; i<H;i++) {
			for (int j=0; j<W;j++) {
				System.out.print(R[j][i] + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("D:");
		for (int i=0; i<H;i++) {
			for (int j=0; j<W;j++) {
				System.out.print(D[j][i] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
  
	public void resetMaze() {
		cell=new int[W+1][H+1];
	    L   =new int[W+1][H+1];
	    U   =new int[W+1][H+1];
	    R   =new int[W+1][H+1];
	    D   =new int[W+1][H+1];
	    for (int i=0; i<=W; i++) { 
	    	for (int j=0; j<=H; j++) {
	    		cell[i][j]=0;
	    		L   [i][j]=0;
	    		U   [i][j]=0;
	    		R   [i][j]=0;
	    		D   [i][j]=0; 
	    	}
	    }
	}
  
  public void generate() {
	  
	    //  Initialize
		done=false;
	
	    //  Exit if area is too small.
	    if (W<1 || H<1) return;
	
	    //  Set up the starting grid data.
	    resetMaze();
	
	    //  Initialize the maze-generating conditions.
	    X=W/2; 
	    Y=H/2; 
	    cell[X][Y]=1; 
	
	    // maze-generation loop.
	    while (!done) {	  
	    	//  Make a list of up to four ways that the trail can be extended.
		    options=0;
		    if (X>0   && cell[X-1][Y  ]==0) {
		      direc[options]='L'; dist[options]=1; options++; }
		    if (Y>0   && cell[X  ][Y-1]==0) { 
		      direc[options]='U'; dist[options]=1; options++; }
		    if (X<W-1 && cell[X+1][Y  ]==0) {
		      direc[options]='R'; dist[options]=1; options++; }
		    if (Y<H-1 && cell[X  ][Y+1]==0) { 
		      direc[options]='D'; dist[options]=1; options++; }
		    if (X>1   && cell[X-1][Y  ]!=0 && L[X-1][Y  ]==0 && U[X-1][Y  ]!=0 && R[X-1][Y  ]==0 && D[X-1][Y  ]!=0 && cell[X-2][Y  ]==0) { 
		      direc[options]='L'; dist[options]=2; options++; }
		    if (Y>1   && cell[X  ][Y-1]!=0 && L[X  ][Y-1]!=0 && U[X  ][Y-1]==0 && R[X  ][Y-1]!=0 && D[X  ][Y-1]==0 && cell[X  ][Y-2]==0) { 
		      direc[options]='U'; dist[options]=2; options++; }
		    if (X<W-2 && cell[X+1][Y  ]!=0 && L[X+1][Y  ]==0 && U[X+1][Y  ]!=0 && R[X+1][Y  ]==0 && D[X+1][Y  ]!=0 && cell[X+2][Y  ]==0) { 
		      direc[options]='R'; dist[options]=2; options++; }
		    if (Y<H-2 && cell[X  ][Y+1]!=0 && L[X  ][Y+1]!=0 && U[X  ][Y+1]==0 && R[X  ][Y+1]!=0 && D[X  ][Y+1]==0 && cell[X  ][Y+2]==0) { 
		      direc[options]='D'; dist[options]=2; options++; }
		      
		    if (options>0) {   //   Extend the path.
		        int i=(int) (Math.random()*options);
		        if (direc[i]=='L') {
		          L[X][Y]=dist[i]; 
		          X-=dist[i]; 
		          R[X][Y]=dist[i]; 
		          } 
		        if (direc[i]=='U') {
		          U[X][Y]=dist[i]; 
		          Y-=dist[i]; 
		          D[X][Y]=dist[i]; 
		          }
		        if (direc[i]=='R') {
		          R[X][Y]=dist[i]; 
		          X+=dist[i]; 
		          L[X][Y]=dist[i]; 
		          } 
		        if (direc[i]=='D') {
		          D[X][Y]=dist[i]; 
		          Y+=dist[i]; 
		          U[X][Y]=dist[i]; 
		          }
		        cell[X][Y]=1;
		    }
	
		    else {   //  Retreat the path.
		    	cell[X][Y]=2; 
		        if      (cell[X-L[X][Y]][Y        ]==1) X-=L[X][Y];
		        else if (cell[X        ][Y-U[X][Y]]==1) Y-=U[X][Y];
		        else if (cell[X+R[X][Y]][Y        ]==1) X+=R[X][Y]; 
		        else if (cell[X        ][Y+D[X][Y]]==1) Y+=D[X][Y];
		        else                                    done=true; 
		    }
	    }

	    //  Open up the start and end of the maze.
	    U[0][0]=1; D[W-1][H-1]=1;
  	}
  
  	/*
  	public Node crossroadsTree() {
		Node root = null;
		Node current = null;
		boolean flag = false;
		
		for (int i=0; i<H;i++) {
			for (int j=0; j<W;j++) {
				int count = 0;
				if (U[i][j]!=0) {
					count++;
				}
				if (R[i][j]!=0) {
					count++;
				}
				if (D[i][j]!=0) {
					count++;
				}
				if (L[i][j]!=0) {
					count++;
				}
				
				if (count>2) {
					Node node = new Node(i,j);
					node.parent = current;
					
					if (!flag) {
						flag = true;
						root = current;
					} 
				}
			}
		}
		
		  
		return root;
  	}*/

}

