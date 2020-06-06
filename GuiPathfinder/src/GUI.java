import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GUI extends JFrame{
	
	private Board boardCanvas;
	
	int spacing = 2;
	int windowXSpace = 8;
	int windowYSpace = 67;
	
	public int mouseX = -100;
	public int mouseY = -100;
	public int mouseClick;
	public int startingX;
	public int startingY;
	public int endingX;
	public int endingY;
	public boolean mouseClicked = false;
	public boolean selectStartPoint = false;
	public boolean startConfirmed = false;
	public boolean selectEndPoint = false;
	public boolean endConfirmed = false;
	public boolean selectWalls = true;
	public boolean calculatingPath = false;
	public boolean drawnPathAvailable = false;
	public boolean noPathAvailable = true;
	int[][] walls = new int[50][50];
	int[][] gridStartEnd = new int[50][50];
	Pathfinder pathfinder = new Pathfinder();
	public int[][] pathDrawn;
	
	public GUI() {
		
		for(int i = 0; i < 50; i++) {
			if(i == 0 || i == 49) {
				for(int j = 0; j < 50; j++) {
					walls[i][j] = 1;
				}
			}else {
				walls[i][0] = 1;
				walls[i][49] = 1;
			}
		}
		
		JPanel btnPanel = new JPanel(new FlowLayout());
		JButton randomiseWalls = new JButton("Randomise Walls");
		JButton selectStart = new JButton("Select Start");
		JButton selectEnd = new JButton("Select End");
		JButton goBtn = new JButton("Calculate Path");
		JButton refresh = new JButton("Refresh Grid");
		btnPanel.add(randomiseWalls);
		randomiseWalls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectWalls = true;
				selectStartPoint = false;
				selectEndPoint = false;
				calculatingPath = false;
				Random rand = new Random();
				for(int i = 1; i < 49; i++) {
					for(int j = 1; j < 49; j++) {
						int randomNum = rand.nextInt(100);
						if(randomNum <= 50) {
							walls[i][j] = 1;
						}else {
							walls[i][j] = 0;
						}
					}
				}
			}
		});
		btnPanel.add(selectStart);
		selectStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectWalls = false;
				selectStartPoint = true;
				selectEndPoint = false;
				calculatingPath = false;
			}
		});
		btnPanel.add(selectEnd);
		selectEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(startConfirmed == true) {
					selectWalls = false;
					selectStartPoint = false;
					selectEndPoint = true;
					calculatingPath = false;
				}
			}
		});
		btnPanel.add(goBtn);
		goBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(endConfirmed == true) {
					selectWalls = false;
					selectStartPoint = false;
					selectEndPoint = false;
					calculatingPath = true;
					for(int i = 0; i < 50; i++) {
						for(int j = 0; j < 50; j++) {
							if(gridStartEnd[i][j] == 1) {
								startingX = i;
								startingY = j;
							}else if(gridStartEnd[i][j] == 2) {
								endingX = i;
								endingY = j;
							}
						}
					}
					pathDrawn = pathfinder.calculatePath(startingX, startingY, endingX, endingY, walls);
					
					for(int[] innerArray : pathDrawn) {
						for(int val : innerArray) {
							if(val == 1) {
								noPathAvailable = false;
							}
						}
					}
					
					if(noPathAvailable == true) {
						JOptionPane.showMessageDialog(btnPanel, "No Path Available");
					}else {
						drawnPathAvailable = true;
					}
				}
			}
		});
		
		btnPanel.add(refresh);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectWalls = true;
				selectStartPoint = false;
				selectEndPoint = false;
				calculatingPath = false;
				startConfirmed = false;
				endConfirmed = false;
				noPathAvailable = true;
				for(int i = 0; i < 50; i++) {
					for(int j = 0; j < 50; j++) {
						walls[i][j] = 0;
						gridStartEnd[i][j] = 0;
						pathDrawn[i][j] = 0;
						if(i == 0 || i == 49) {
							walls[i][j] = 1;
						}else {
							walls[i][0] = 1;
							walls[i][49] = 1;
						}
					}
				}
				
				
			}
		});
		
		Board board = new Board();
		this.setContentPane(board);
		
		Move move = new Move();
		this.addMouseMotionListener(move);
		
		Click click = new Click();
		this.addMouseListener(click);
		
		boardCanvas = new Board();
		boardCanvas.setPreferredSize(new Dimension(600, 600));
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(boardCanvas, BorderLayout.SOUTH);
		cp.add(btnPanel, BorderLayout.NORTH);
		
		this.setTitle("A* Pathfinder");
		this.setSize(616, 675);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
		
	}
	
	public class Board extends JPanel{
		
		public void paintComponent(Graphics g) {
			//super.paintComponent(g);
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, 600, 650);
			for(int i = 0; i < 50; i++) {
				for(int j = 0; j < 50; j++) {
					g.setColor(Color.gray);
					
					if(walls[i][j] == 1) {
						g.setColor(Color.white);
					}
					
					if(gridStartEnd[i][j] == 1) {
						g.setColor(Color.orange);
					}
					
					if(gridStartEnd[i][j] == 2) {
						g.setColor(Color.blue);
					}
					
					if(drawnPathAvailable == true) {
						if(pathDrawn[i][j] == 1) {
							g.setColor(Color.green);
						}
					}
					
					if(mouseX >= windowXSpace+spacing+(i*12)
							&& mouseX < windowXSpace+(i*12)+12
							&& mouseY >= windowYSpace+spacing+(j*12)
							&& mouseY < windowYSpace+(j*12)+12) {
						g.setColor(Color.LIGHT_GRAY);
					}
					
					g.fillRect(spacing + (i * 12), spacing + (j * 12), 12 - (2*spacing), 12 - (2*spacing));
				}
			}
			
		}
	}
	
	public class Move implements MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent e) {
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}
		
	}
	
	public class Click implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(selectWalls == true) {
				if(inBoxX() != -1 && inBoxY() != -1) {
					if(inBoxX() == 0 || inBoxX() == 49 || inBoxY() == 0 || inBoxY() == 49) {
						walls[inBoxX()][inBoxY()] = 1;
					}else {
						if(walls[inBoxX()][inBoxY()] == 0) {
							walls[inBoxX()][inBoxY()] = 1;
						}else if(walls[inBoxX()][inBoxY()] == 1) {
							walls[inBoxX()][inBoxY()] = 0;
						}
					}
				}
			}else if(selectStartPoint == true) {
				if(startConfirmed == false) {
					if(inBoxX() != -1 && inBoxY() != -1) {
						if(inBoxX() == 0 || inBoxX() == 49 || inBoxY() == 0 || inBoxY() == 49) {
							gridStartEnd[inBoxX()][inBoxY()] = 0;
							startConfirmed = false;
						}else {
							if(gridStartEnd[inBoxX()][inBoxY()] == 0 && walls[inBoxX()][inBoxY()] != 1) {
								gridStartEnd[inBoxX()][inBoxY()] = 1;
								startConfirmed = true;
							}else if(gridStartEnd[inBoxX()][inBoxY()] == 1) {
								gridStartEnd[inBoxX()][inBoxY()] = 0;
								startConfirmed = false;
							}
						}
					}
				}else if(startConfirmed == true) {
					if(gridStartEnd[inBoxX()][inBoxY()] == 1) {
						gridStartEnd[inBoxX()][inBoxY()] = 0;
						startConfirmed = false;
					}
				}
			}else if(selectEndPoint == true) {
				if(endConfirmed == false) {
					if(inBoxX() != -1 && inBoxY() != -1) {
						if(inBoxX() == 0 || inBoxX() == 49 || inBoxY() == 0 || inBoxY() == 49) {
							gridStartEnd[inBoxX()][inBoxY()] = 0;
							endConfirmed = false;
						}else {
							if(gridStartEnd[inBoxX()][inBoxY()] == 0 && walls[inBoxX()][inBoxY()] != 1) {
								gridStartEnd[inBoxX()][inBoxY()] = 2;
								endConfirmed = true;
							}else if(gridStartEnd[inBoxX()][inBoxY()] == 1) {
								gridStartEnd[inBoxX()][inBoxY()] = 1;
								endConfirmed = false;
							}else if(gridStartEnd[inBoxX()][inBoxY()] == 2) {
								gridStartEnd[inBoxX()][inBoxY()] = 0;
								endConfirmed = false;
							}
						}
					}
				}else if(endConfirmed == true) {
					if(gridStartEnd[inBoxX()][inBoxY()] == 2) {
						gridStartEnd[inBoxX()][inBoxY()] = 0;
						endConfirmed = false;
					}
				}
			}
			//System.out.println("["+inBoxX()+","+inBoxY()+"]");
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}
		
	}
	
	public int inBoxX() {
		for(int i = 0; i < 50; i++) {
			for(int j = 0; j < 50; j++) {
				if(mouseX >= windowXSpace+spacing+(i*12)
						&& mouseX < windowXSpace+(i*12)+12
						&& mouseY >= windowYSpace+spacing+(j*12)
						&& mouseY < windowYSpace+(j*12)+12) {
					return(i);
				}
			}
		}
		return(-1);
	}
	
	public int inBoxY() {
		for(int i = 0; i < 50; i++) {
			for(int j = 0; j < 50; j++) {
				if(mouseX >= windowXSpace+spacing+(i*12)
						&& mouseX < windowXSpace+(i*12)+12 
						&& mouseY >= windowYSpace+spacing+(j*12)
						&& mouseY < windowYSpace+(j*12)+12) {
					return(j);
				}
			}
		}
		return(-1);
	}

}
