import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;

public class Model extends SimModelImpl {
	//private ArrayList<Captain> captainList;
	//private ArrayList<Soldier> soldierList;
	private ArrayList<Robot> robotList;
	private ArrayList<WalkingAgent> agentList;
	private ArrayList<Agent> objectList;
	
	private MazeGenerator maze;
	private Schedule schedule;
	private DisplaySurface dsurf;
	private Object2DTorus space;

	private static enum AIMode { Single, Battalion };
	private static enum RobotMode { Start, Random };
	private static enum Priority { Soldier, Captain };
	private int width, height;
	private AIMode mode;
	private RobotMode Rmode;
	private Priority pry;
	
	private int id, captains, soldiers, robots;
	private Radio radio, cell;
	private int velocity;
	private int robotbat;
	
	private int battery;
	private double ticks;

	public Model() {
		this.id=-1;
		this.mode = AIMode.Battalion;
		this.Rmode = RobotMode.Start;
		this.pry = Priority.Captain;
		this.width = 10;
		this.height = 10;
		
		this.captains = 5;
		this.soldiers = 15;
		this.robots = 5;
		
		this.radio = new Radio(5,50);
		this.cell = new Radio(0,50);
		
		this.velocity=50;
		this.robotbat=50;
		
	}

	public String getName() {
		return "Explore";
	}
	
	public Schedule getSchedule() {
		return schedule;
	}
	
	public String[] getInitParam() {
		return new String[] 
				{ "AgentCaptain", "AgentSoldier", "AgentRobot", 
				"MazeWidth", "MazeHeight", "AIMode", 
				"RadioRadius", "RadioBattery", "TelRadius", "TelBattery", 
				"Velocity", "RobotBattery", "RobotMode", "Priority" };
	}
	
	public int getVelocity() {
		return this.velocity;
	}

	public void setVelocity(int v) {
		if (v<0) v=0;
		this.velocity = v;
	}
	
	public int getRobotBattery() {
		return this.robotbat;
	}

	public void setRobotBattery(int N) {
		if (N<0) N=0;
		this.robotbat = N;
	}
	
	public int getRadioRadius() {
		return this.radio.radius;
	}

	public void setRadioRadius(int N) {
		if (N<0) N=0;
		this.radio.radius = N;
	}
	
	public int getRadioBattery() {
		return this.radio.battery;
	}

	public void setRadioBattery(int N) {
		if (N<0) N=0;
		this.radio.battery = N;
	}
	
	public int getTelRadius() {
		return this.cell.radius;
	}

	public void setTelRadius(int N) {
		if (N<0) N=0;
		this.cell.radius = N;
	}
	
	public int getTelBattery() {
		return this.cell.battery;
	}

	public void setTelBattery(int N) {
		if (N<0) N=0;
		this.cell.battery = N;
	}
	
	public int getAgentCaptain() {
		return this.captains;
	}

	public void setAgentCaptain(int NA) {
		if (NA<0) NA=0;
		this.captains = NA;
	}
	
	public int getAgentSoldier() {
		return this.soldiers;
	}

	public void setAgentSoldier(int NA) {
		if (NA<0) NA=0;
		this.soldiers = NA;
	}
	
	public int getAgentRobot() {
		return this.robots;
	}

	public void setAgentRobot(int NA) {
		if (NA<0) NA=0;
		this.robots = NA;
	}

	public int getMazeWidth() {
		return this.width;
	}

	public void setMazeWidth(int W) {
		if (W<2) W=2;
		this.width = W;
	}
	
	public int getMazeHeight() {
		return this.height;
	}

	public void setMazeHeight(int H) {
		if (H<2) H=2;
		this.height = H;
	}
	
	public void setAIMode(AIMode mode) {
		this.mode = mode;
	}

	public AIMode getAIMode() {
		return this.mode;
	}
	
	public void setRobotMode(RobotMode rmode) {
		this.Rmode = rmode;
	}

	public RobotMode getRobotMode() {
		return this.Rmode;
	}
	
	public void setPriority(Priority pry) {
		this.pry = pry;
	}

	public Priority getPriority() {
		return this.pry;
	}
	
	public int getMazeU(int x, int y) {
		return maze.U[x][y];
	}
	
	public int getMazeR(int x, int y) {
		return maze.R[x][y];
	}
	
	public int getMazeD(int x, int y) {
		return maze.D[x][y];
	}
	
	public int getMazeL(int x, int y) {
		return maze.L[x][y];
	}
	
	@SuppressWarnings("unchecked")
	public void setup() {
		schedule = new Schedule();
		if (dsurf != null) dsurf.dispose();
		dsurf = new DisplaySurface(this, "Explore Display");
		dsurf.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {
				System.out.println("Key pressed: " + evt.getKeyCode());
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					//ticks = getTickCount() - ticks;
					System.out.println("Reset key typed at " + getTickCount());
					System.out.println("Reseting all agents");
					pause();
					battery=robotbat;
					for(int i = 0; i < robotList.size(); i++) {
						Robot agent = robotList.get(i);
						agent.reset();
						agent.battery=robotbat;
						space.putObjectAt(1, 1, agent);
					}
					for(int i = 0; i < agentList.size(); i++) {
						WalkingAgent agent = agentList.get(i);
						agent.reset();
						space.putObjectAt(1, 1, agent);
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent evt) {
			}

			@Override
			public void keyTyped(KeyEvent evt) {
			}

			
		});
		registerDisplaySurface("Explore Display", dsurf);	
		
		// property descriptors
		Vector<AIMode> vMM = new Vector<AIMode>();
		for(int i=0; i<AIMode.values().length; i++) {
			vMM.add(AIMode.values()[i]);
		}
		descriptors.put("AIMode", new ListPropertyDescriptor("AIMode", vMM));
		
		Vector<RobotMode> RvMM = new Vector<RobotMode>();
		for(int i=0; i<RobotMode.values().length; i++) {
			RvMM.add(RobotMode.values()[i]);
		}
		descriptors.put("RobotMode", new ListPropertyDescriptor("RobotMode", RvMM));
		
		Vector<Priority> PvMM = new Vector<Priority>();
		for(int i=0; i<Priority.values().length; i++) {
			PvMM.add(Priority.values()[i]);
		}
		descriptors.put("Priority", new ListPropertyDescriptor("Priority", PvMM));
	}

	public void begin() {
		buildModel();
		buildDisplay();
		buildSchedule();
	}

	public void buildModel() {
		id=-1;
		battery=robotbat;
		ticks=0;
		//captainList = new ArrayList<Captain>();
		//soldierList = new ArrayList<Soldier>();
		robotList  = new ArrayList<Robot>();
		agentList = new ArrayList<WalkingAgent>();
		objectList = new ArrayList<Agent>();
		
		space = new Object2DTorus((width*2) + 1, (height*2) + 1);
		maze = new MazeGenerator(this.width, this.height);
		
		maze.generate();
		//maze.printMaze();
		
		Color walcolor = new Color(0,128,0);
		Color bricolor = new Color(64,128,64);
		
		Color capcolor = new Color(128,0,0);
		Color solcolor = new Color(255,0,255);
		Color robcolor = new Color(0,0,128);
		
		// draw maze mold
		for (int i=0; i<height*2 + 1;i=i+2) {
			for (int j=0; j<width*2 + 1;j=j+2) {
				if (space.getObjectAt(i, j)==null) {
					//id++;
					WallAgent agent = new WallAgent(id, i, j, walcolor, space);
					objectList.add(agent);
					space.putObjectAt(i, j, agent);
				}
			}
		}
		
		// draw walls
		for (int i=0; i<height;i++) {
			for (int j=0; j<width;j++) {
				int x=(i*2)+1;
				int y=(j*2)+1;
				WallAgent agent = null;
				
				if (maze.U[i][j]==0) {
					//id++;
					agent = new WallAgent(id, x, y-1, walcolor, space);
					space.putObjectAt(x, y-1, agent);
					objectList.add(agent);
				}
				
				if (maze.D[i][j]==0) {
					//id++;
					agent = new WallAgent(id, x, y+1, walcolor, space);
					space.putObjectAt(x, y+1, agent);
					objectList.add(agent);
				} 
				
				if (maze.R[i][j]==0) {
					//id++;
					agent = new WallAgent(id, x+1, y, walcolor, space);
					space.putObjectAt(x+1, y, agent);
					objectList.add(agent);
				}

				if (maze.L[i][j]==0) {
					//id++;
					agent = new WallAgent(id, x-1, y, walcolor, space);
					space.putObjectAt(x-1, y, agent);
					objectList.add(agent);
				}
			}
		}
		
		// draw bridges
		for (int i=0; i<height;i++) {
			for (int j=0; j<width;j++) {
				int x=(i*2)+1;
				int y=(j*2)+1;
				
				if (maze.U[i][j]==2) {
					for (int c=1; c<4;c++) {
						boolean flag = false;
						for (int li = 0; li<objectList.size();li++) {
							if ((objectList.get(li).x==x) && (objectList.get(li).y==y-c)) {
									objectList.get(li).color = bricolor;
									flag = true;
							}
						}
						
						if (!flag) {
							//id++;
							WallAgent agent = new WallAgent(id, x, y-c, bricolor, space);
							space.putObjectAt(x, y-c, agent);
							objectList.add(agent);
						}
					}
					
				}
				
				if (maze.R[i][j]==2) {
					
					for (int c=1; c<4;c++) {
						boolean flag = false;
						for (int li = 0; li<objectList.size();li++) {
							if ((objectList.get(li).x==x+c) && (objectList.get(li).y==y)) {
									objectList.get(li).color = bricolor;
									flag = true;
							}
						}
						
						if (!flag) {
							//id++;
							WallAgent agent = new WallAgent(id, x+c, y, bricolor, space);
							space.putObjectAt(x+c, y, agent);
							objectList.add(agent);
						}
					}
					
				}
			}
		}
		
		for (int i = 0; i<captains; i++) {
			id++;
			WalkingAgent agent = new Captain(id,1, 1, capcolor, space, radio, cell);
			//captainList.add(agent);
			agentList.add(agent);
			
			objectList.add(agent);
			space.putObjectAt(1, 1, agent);
		}
		
		for (int i = 0; i<soldiers; i++) {
			id++;
			WalkingAgent agent = new Soldier(id, 1, 1, solcolor, space, radio);
			//soldierList.add(agent);
			agentList.add(agent);
			
			objectList.add(agent);
			space.putObjectAt(1, 1, agent);
		}
		
		for (int i = 0; i<robots; i++) {
			id++;
			Robot agent = new Robot(id, 1, 1, robcolor, space, robotbat);
			
			int rx=1;
			int ry=1;
			if (Rmode==RobotMode.Random) {
				// random position
				rx = Random.uniform.nextIntFromTo(0, maze.W-1);
				ry = Random.uniform.nextIntFromTo(0, maze.H-1);
				rx=(rx*2)+1;
				ry=(ry*2)+1;
				agent.x=rx;
				agent.y=ry;
				// random direction
				int rndir = Random.uniform.nextIntFromTo(0, 4);
				for (int j=0; j<rndir; j++) {
					agent.last = (byte) (agent.last << 1);
					if (agent.last>0x08) agent.last = 0x01;
				}
			}
			
			robotList.add(agent);
			//agentList.add(agent);
			
			objectList.add(agent);
			space.putObjectAt(rx, ry, agent);
		}
		
		//close entrance
		//maze.U[0][0]=0;
	}
	
	private void buildDisplay() {
		// space and display surface
		Object2DDisplay display = new Object2DDisplay(space);
		
		display.setObjectList(objectList);
		dsurf.addDisplayableProbeable(display, "Agent Space");
		dsurf.setBackground(new Color(128,64,0));
		dsurf.display();
	}

	private void buildSchedule() {
		//schedule.scheduleActionBeginning(0, new MainAction());
		//first schedule robots then humans
		//schedule.scheduleActionAtInterval(velocity, new RobotAction());
		schedule.scheduleActionAtInterval(velocity, new MainAction());
		schedule.scheduleActionAtInterval(10, dsurf, "updateDisplay", Schedule.LAST);
	}
	
	class MainAction extends BasicAction {

		// Main Action Summary:
		// 1-shuffle agents and clear crossroads info and walk list
		// 2-For each human agent:
		// 	2.1-update local maze information
		// 	2.2-check for agents in current position
		//	2.3-if in a crossroad add to crossroads list
		//	2.4-else add to walk list
		//	2.5-execute walk list
		// 3-For each agent in a crossroad:
		//	3.1-communicate with other agents
		// 	3.2-divide & conquer
		
		public void execute() {
			try {
				if (battery>0) {
					// shuffle robots
					SimUtilities.shuffle(robotList);

					// iterate through all robots
					for(int i = 0; i < robotList.size(); i++) {
						Robot agent = robotList.get(i);
						
						if (agent.battery<=0) return;
						
						//for each robot create a list of reachable robots
						//ArrayList<Robot> reachable = reachableRobots(agent);
						
						//gives only local information of maze
						int agentx = agent.x/2;
						int agenty = agent.y/2;
						
						if ((agentx >= maze.W) || (agenty >= maze.H)) {
							for (int j=0; j<2; j++) {
								agent.last = (byte) (agent.last << 1);
								if (agent.last>0x08) agent.last = 0x01;
							}
							agent.y=agent.y-2;
							agenty =agent.y/2;
							agent.update = false;
						}
						
						agent.updateVision(
							getMazeU(agentx,agenty), getMazeR(agentx,agenty), 
							getMazeD(agentx,agenty), getMazeL(agentx,agenty)
						);
						
						agent.walk();
						agent.battery--;
						
					}
					battery--;
				} else {
					// 1-shuffle agents
					SimUtilities.shuffle(agentList);
					// 1-clear crossroads
					HashMap<Point,ArrayList<WalkingAgent>> mapinfo = 
							new HashMap<Point,ArrayList<WalkingAgent>>();
					// 1-clear walk list
					ArrayList<WalkingAgent> listtowalk = new ArrayList<WalkingAgent>();
					
					int out=0;
					// 2-iterate through all human agents
					for(int i = 0; i < agentList.size(); i++) {
						boolean outofbounds=false;
						
						WalkingAgent agent = agentList.get(i);
						//System.out.println("\nAgent: " + agent.id);
						
						// 2.1-gives only local information of maze
						int agentx = (int) Math.floor(agent.x/2);
						int agenty = (int) Math.floor(agent.y/2);
						//System.out.println("x,y: " + agentx + "-" + agenty);
						
						// if agent is out of the maze pop and do nothing
						if ((agentx >= maze.W) || (agenty >= maze.H)) {
							//System.out.println("Agent " + agent.id + " out Ok");
							out++;
							outofbounds=true;
						}
						if ((agentx < 0) || (agenty < 0)){
							//System.out.println("Agent out not Ok");
							out++;
							outofbounds=true;
						}
						if (out>=agentList.size()) {
							System.out.println("All Agents Are Out");
							ticks = getTickCount() - ticks;
							System.out.println("Simulation paused after : " + ticks + " ticks");
							pause();
							return;
						}
						if (outofbounds) continue;
						
						// 2.1-update agent's local info 
						agent.updateVision(
								getMazeU(agentx,agenty), getMazeR(agentx,agenty), 
								getMazeD(agentx,agenty), getMazeL(agentx,agenty));
						/*
						System.out.println("Vision: ");
						System.out.println(agent.U);
						System.out.println(agent.R);
						System.out.println(agent.D);
						System.out.println(agent.L);
						System.out.println(agent.dir);
						System.out.println("Last: " + agent.last);
						*/
						
						/*
						System.out.println("Crosslist before");
						for (int j = 0; j < agent.cross.size(); j++) {
							Node node = agent.cross.get(j);
							System.out.println("parentdir: " + node.parentdir);
							System.out.println("currentdir: " + node.current);
							System.out.println("x: "+ node.x);
							System.out.println("y: "+ node.y);
							System.out.println(node.U);
							System.out.println(node.R);
							System.out.println(node.D);
							System.out.println(node.L);
						}
						*/
						
						// 2.2-check for robots in current position
						for(int j = 0; j < robotList.size(); j++) {
							Robot robo = robotList.get(j);
							if ( (agent.x==robo.x) && (agent.y==robo.y) ) {
								agent.reachable.add(robo);
							}
						}
						
						// 2.2-check for other agents in current position
						for(int j = 0; j < agentList.size(); j++) {
							WalkingAgent wa = agentList.get(j);
							if ( (agent.x==wa.x) && (agent.y==wa.y) && (agent.id!=wa.id) ) {
								agent.reachable.add(wa);
							}
						}
						
						// 2.2-update agent info from reachable agents
						agent.updateCross();
						
						/*
						System.out.println("Crosslist after");
						for (int j = 0; j < agent.cross.size(); j++) {
							Node node = agent.cross.get(j);
							System.out.println("parentdir: " + node.parentdir);
							System.out.println("currentdir: " + node.current);
							System.out.println("x: "+ node.x);
							System.out.println("y: "+ node.y);
							System.out.println(node.U);
							System.out.println(node.R);
							System.out.println(node.D);
							System.out.println(node.L);
						}
						*/
						
						// 2.3-if in a crossroad, update cross info
						if (agent.dir>2) {
							// add or update current node to agent
							Node node=agent.cruNode();
							
							/*
							System.out.println("CrossNode:");
							System.out.println("parentdir: " + node.parentdir);
							System.out.println("currentdir: " + node.current);
							System.out.println("x: "+ node.x);
							System.out.println("y: "+ node.y);
							System.out.println(node.U);
							System.out.println(node.R);
							System.out.println(node.D);
							System.out.println(node.L);
							*/
							
							//System.out.println("Status:");
							//agent.printStatus();
							
							// 2.3-for each agent create a list of agents in the same position
							Point p = new Point(agentx,agenty);
							if (mapinfo.containsKey(p)) {
								// node is on the list
								//System.out.println("existing map node");
								mapinfo.get(p).add(agent);
							} else {
								// node is not on the list
								//System.out.println("new map node");
								ArrayList<WalkingAgent> list = new ArrayList<WalkingAgent>();
								list.add(agent);
								mapinfo.put(p, list);
							}
						} else {
							// 2.4-add to walk list
							// not on a crossroad
							//agent.walk();
							listtowalk.add(agent);
						}
					}
					
					// 2.5-execute walk list
					for (int i = 0; i < listtowalk.size(); i++) {
						listtowalk.get(i).walk();
						//System.out.println(listtowalk.get(i).id + " walked");
					}
					
					// 3-iterate through all agents in a crossroad 
					for (Entry<Point,ArrayList<WalkingAgent>> entry : mapinfo.entrySet()) {
						// get node
					    Node key = new Node (entry.getKey().x,entry.getKey().y);
					    // get list
					    ArrayList<WalkingAgent> value = entry.getValue();
					    
					    /*System.out.println("Key:");
						System.out.println(key.x);
						System.out.println(key.y);
						System.out.println("Num of agents: " + value.size());
						*/
						
					    // create a list of crossroads with the joined values
					    // of each agent's cross list
					    ArrayList<Node> crosslist = new ArrayList<Node>(value.get(0).cross);
					    /*
						for(int i = 0; i < value.size(); i++) {
							System.out.println("agent id on cross: "+value.get(i).id);
							ArrayList<Node> agentcross = value.get(i).cross;
							for(int j = 0; j < agentcross.size(); j++) {
								Node node = agentcross.get(j);
								int index = crosslist.indexOf(node);
								if (index<0) {
									crosslist.add(node);
								} else {
									Node unode = crosslist.get(index);
									if (node.U) unode.U = true;
									if (node.R) unode.R = true;
									if (node.D) unode.D = true;
									if (node.L) unode.L = true;
								}
							}
					    }
					    */

						if (mode == AIMode.Battalion) {
							// check information of the current node
							int index = crosslist.indexOf(key);
							Node sn = crosslist.get(index);
							boolean request=false;
							if (!sn.U) request=true;
							if (!sn.R) request=true;
							if (!sn.D) request=true;
							if (!sn.L) request=true;
							
							// if the info is insufficient we radio our mates!
							// (unless the whole battalion is on the same position)
							if (agentList.size()==value.size()) {
								request=false;
							}
									
							if (request) {
								// choose best suited agent
								// first best captain and best soldier
								int sbat=0;
								int cbat=0;
								Captain cap=null;
								Soldier sol=null;
								for(int i = 0 ; i < value.size();i++){
									WalkingAgent agn = value.get(i);
									if(agn instanceof Soldier) {
										Soldier s = (Soldier) agn;
										int bat=s.radio.battery;
										if (bat>sbat) {
											sbat=bat;
											sol=(Soldier) agn;
										}
									} else if(agn instanceof Captain) {
										Captain c = (Captain) agn;
										int bat=c.cell.battery+c.radio.battery;
										
										if (bat>cbat){
											cbat=bat;
											cap = (Captain) agn;
										}
									}
								}
								
								// best between
								WalkingAgent best=null;
								if (pry==Priority.Captain) {
									if (cbat>0) {
										best=cap;
									} else if (sbat>0) {
										best=sol;
									}
								} else if (pry==Priority.Soldier) {
									if (sbat>0) {
										best=sol;
									} else if (cbat>0) {
										best=cap;
									}
								}
								
								// reach agents
								if (best!=null) {
									best.communicate(agentList);
									crosslist.clear();
									for(int i = 0; i < best.cross.size(); i++) {
										Node nnode = new Node(best.cross.get(i),true);
										crosslist.add(nnode);
									}
								}
							}
						}
						
						/*
						System.out.println("Crosslist");
						for (int i = 0; i < crosslist.size(); i++) {
							Node node = crosslist.get(i);
							System.out.println("parentdir: " + node.parentdir);
							System.out.println("currentdir: " + node.current);
							System.out.println("x: "+ node.x);
							System.out.println("y: "+ node.y);
							System.out.println(node.U);
							System.out.println(node.R);
							System.out.println(node.D);
							System.out.println(node.L);
						}
						*/
						
						// for the current crossroad decide
						// distribuiton of agents
						int index = crosslist.indexOf(key);
						Node sn = crosslist.get(index);
						int posdir=0;
						byte start = WalkingAgent.NONE;
						if (!sn.L) {
							posdir++;
							start=WalkingAgent.LEFT;
						}
						if (!sn.D) {
							posdir++;
							start=WalkingAgent.DOWN;
						}
						if (!sn.R) {
							posdir++;
							start=WalkingAgent.RIGHT;
						}
						if (!sn.U) {
							posdir++;
							start=WalkingAgent.UP;
						}
						
						// res is the number of agents 
						// to disperse in each direction 
						int res=0;
						if (posdir!=0) {
							res=value.size()/posdir;
						}
						
						System.out.println("node start: " + start);
						System.out.println("node possible directions: " + posdir);
						System.out.println("disperse agents: " + res);

						// copy crossroads list with the joined values to all agents
						for(int i = 0; i < value.size(); i++) {
							WalkingAgent agn = value.get(i);
							agn.cross.clear();
							for(int j = 0; j < crosslist.size(); j++) {
								Node nnode = new Node(crosslist.get(j),true);
								agn.cross.add(nnode);
							}
							//agn.cross = new ArrayList<Node>(crosslist);
						}
						
						int count=0;
						for(int i = 0; i < value.size(); i++) {
							WalkingAgent agn = value.get(i);
							ArrayList<Node> agentcross = agn.cross;
							int ix=agentcross.indexOf(key);
							Node no = agentcross.get(ix);
							// set direction
							no.current=start;
							//System.out.println("start: " + no.current);
							if (posdir==0) {
								// go back
								agn.last=no.parentdir;
							} else {
								// go to chosen direction
								agn.last=no.current;
								count++;
								if (count>=res) {
									count=0;
									boolean flag=true;
									do {
										start = (byte) (start << 1);
										if (start>WalkingAgent.LEFT) start = WalkingAgent.UP;
										switch(start) {
											case WalkingAgent.UP:
												if (!no.U) flag=false;
												break;
											case WalkingAgent.RIGHT:
												if (!no.R) flag=false;
												break;
											case WalkingAgent.DOWN:
												if (!no.D) flag=false;
												break;
											case WalkingAgent.LEFT:
												if (!no.L) flag=false;
												break;
											default:
												break;
										}
									} while (flag);
								}
							}
							agn.go();
						}
					}
					mapinfo.clear();
					listtowalk.clear();
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}

	}
	
	public static void main(String[] args) {
		SimInit init = new SimInit();
		init.loadModel(new Model(), null, false);
	}

}
