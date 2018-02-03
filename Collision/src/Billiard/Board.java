package Billiard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import CollisionEngineTest.*;

import javax.swing.*;

public class Board extends JPanel implements Runnable, ActionListener, MouseListener, MouseMotionListener{
	private JFrame frame;
	private JPanel lowerPanel;

	private JButton backToLauncher;

	//While this is true the thread is running
	private boolean running = true;
	
	private double eX = 0;
	private double eY = 0;
	private boolean drag = false;
	private Vector2D dragForce = new Vector2D();
	
	private final static int xBounds = 800;
	private final static int yBounds = 400;
	
	//A variable that is the friction teller.
	protected static final float mu = 0.000012f;

	static final Color[] colors = {Color.GREEN,
			Color.BLUE,
			Color.BLACK,
			Color.CYAN,
			Color.PINK,
			Color.GRAY,
			Color.MAGENTA,
			Color.LIGHT_GRAY,
			Color.RED,
			Color.ORANGE,
			Color.YELLOW,
			Color.WHITE,};

	private static CollisionHandler handleMan;
	//A Ball ArrayList that holds all the balls currently existing.
	private static ArrayList<Ball> balls = null;
	//A Ball arrayList for all the holes
	private static ArrayList<Ball> holes = new ArrayList<Ball>();
	static {
		holes.add(new Ball(0, 0, 30, 0, 0, 0, Color.DARK_GRAY));
		holes.add(new Ball(xBounds/2, 0, 30, 0, 0, 0, Color.DARK_GRAY));
		holes.add(new Ball(xBounds, 0, 30, 0, 0, 0, Color.DARK_GRAY));
		holes.add(new Ball(xBounds, yBounds, 30, 0, 0, 0, Color.DARK_GRAY));
		holes.add(new Ball(xBounds/2, yBounds, 30, 0, 0, 0, Color.DARK_GRAY));		
		holes.add(new Ball(0, yBounds, 30, 0, 0, 0, Color.DARK_GRAY));
		
		
	}

	//jumpConstant is the default jump value.
	private final static long jumpConstant=1;
	//actualJump is the actual jump value.
	private static long actualJump=100;


	public void bootUp() {
		System.out.println("bootup");
		//creates ArrayList of balls and adds balls to it.
		balls= new ArrayList<Ball>();

		

		balls.add(new Ball(0.216*xBounds,yBounds/2,18,4f,0,0,Color.WHITE));
		//1.444444444
		double blackSpotX = 0.825*xBounds;
		double blackSpotY = yBounds/2;
		balls.add(new Ball(blackSpotX-67, blackSpotY, 18,4f,0,0,Color.BLUE));
		balls.add(new Ball(blackSpotX-34, blackSpotY-19, 18,4f,0,0,Color.GREEN));
		balls.add(new Ball(blackSpotX-34, blackSpotY+19, 18,4f,0,0,Color.YELLOW));
		
		balls.add(new Ball(blackSpotX, blackSpotY-38,18,4f,0,0,Color.MAGENTA));
		
		balls.add(new Ball(blackSpotX, blackSpotY,19,4f,0,0,Color.BLACK));

		balls.add(new Ball(blackSpotX, blackSpotY+38,18,4f,0,0,Color.MAGENTA));
		
		balls.add(new Ball(blackSpotX+34, blackSpotY-57, 18,4f,0,0,Color.YELLOW));
		balls.add(new Ball(blackSpotX+34, blackSpotY-19, 18,4f,0,0,Color.CYAN));
		balls.add(new Ball(blackSpotX+34, blackSpotY+19, 18,4f,0,0,Color.YELLOW));
		balls.add(new Ball(blackSpotX+34, blackSpotY+57, 18,4f,0,0,Color.YELLOW));
		
		balls.add(new Ball(blackSpotX+68, blackSpotY-74, 18,4f,0,0,Color.RED));
		balls.add(new Ball(blackSpotX+68, blackSpotY-37, 18,4f,0,0,Color.ORANGE));
		balls.add(new Ball(blackSpotX+68, blackSpotY, 18,4f,0,0,Color.BLUE));
		balls.add(new Ball(blackSpotX+68, blackSpotY+37, 18,4f,0,0,Color.ORANGE));
		balls.add(new Ball(blackSpotX+68, blackSpotY+74, 18,4f,0,0,Color.RED));

		
		this.setPreferredSize(new Dimension(xBounds, yBounds));
		this.setBackground(new Color(14, 122, 52));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		lowerPanel = new JPanel();
		lowerPanel.setPreferredSize(new Dimension(xBounds, 72));


		backToLauncher = new JButton("Back to Launcher");
		//		backToLauncher.setPreferredSize(new Dimension(xBounds/3, 80));
		backToLauncher.addActionListener(this);
		backToLauncher.setActionCommand("BackToLauncher");

		

		

		frame= new JFrame();
		frame.setTitle("2D Collision");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(xBounds, yBounds+100);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		frame.add(this);
		frame.add(lowerPanel, BorderLayout.SOUTH);
		handleMan = new CollisionHandler(xBounds, yBounds, mu);






	}
	//Terminates the thread by chaging thread boolean to false;
	public void terminate() {
		running = false;
	}

	public static void main(String[] args) {
		System.out.println("main");
		Board board = new Board();
		board.bootUp();
		Thread t1= new Thread(board);
		t1.start();
	}

	@Override
	public void run() {
		while(running) {
			try{
				//If the frame is closing, terminating the thread.
				if(!this.frame.isVisible())
					terminate();
				actualJump=jumpConstant;
				System.out.println("cycle");
//				System.out.println(balls.get(0).getSpeed());

				handleMan.handle(balls, holes, actualJump, jumpConstant);
				repaint();
				Thread.sleep(actualJump);
			}
			catch (InterruptedException e) {
				running = false;
			}
			catch(Exception e) {}

		}

	}


	public void paintComponent(Graphics g) {
		System.out.println("repainting");
		super.paintComponent(g);
		
		g.setColor(Color.WHITE);
		g.fillOval((int)(0.216*xBounds), yBounds/2, 5, 5);
		g.setColor(Color.BLACK);
		g.fillOval((int)(0.825*xBounds), yBounds/2, 5, 5);
		
		
		for(int i=0; i<balls.size(); i++) {
			g.setColor(balls.get(i).getColor());	
			g.fillOval((int) (balls.get(i).getX()-balls.get(i).getRadius()),
					(int) (balls.get(i).getY()-balls.get(i).getRadius()),
					(int) balls.get(i).getRadius()*2,
					(int) balls.get(i).getRadius()*2);
			
//			g.drawLine((int)balls.get(i).getX(),
//					(int)balls.get(i).getY(), 
//					(int)((balls.get(i).getX()+ 1000000*balls.get(i).getxAcceleration())),
//					(int)((balls.get(i).getY()+ 1000000*balls.get(i).getyAcceleration())));
			
		}
		for(int i=0; i<holes.size(); i++) {
			g.setColor(Color.DARK_GRAY);	
			g.fillOval((int) (holes.get(i).getX()-holes.get(i).getRadius()),
					(int) (holes.get(i).getY()-holes.get(i).getRadius()),
					(int) holes.get(i).getRadius()*2,
					(int) holes.get(i).getRadius()*2);
			
		}
		
		g.setColor(Color.ORANGE);
		g.drawRect(0, 0, xBounds-1, yBounds-1);
		if(drag) {
			g.setColor(Color.WHITE);
			g.drawLine((int)balls.get(0).getX(), (int)balls.get(0).getY(), (int)eX, (int)eY);
		}
	}

	//Getters and setters.
	public static ArrayList<Ball> getBalls() {
		return balls;
	}
	public static void setBalls(ArrayList<Ball> balls) {
		Board.balls = balls;
	}
	public static void addBall(Ball ball) {
		balls.add(ball);
	}
	public static long getActualJump() {
		return actualJump;
	}
	public static void setActualJump(long actualJump) {
		Board.actualJump = actualJump;
	}
	public static int getXbounds() {
		return xBounds;
	}
	public static int getYbounds() {
		return yBounds;
	}
	public static long getJumpconstant() {
		return jumpConstant;
	}

	public static CollisionHandler getHandleMan() {
		return handleMan;
	}

	public static void setHandleMan(CollisionHandler handleMan) {
		Board.handleMan = handleMan;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		eX = e.getX();
		eY = e.getY();
		dragForce = new Vector2D((balls.get(0).getX()-eX)*0.01, (balls.get(0).getY()-eY)*0.01);
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		double d = Math.sqrt(Math.pow(e.getX()-balls.get(0).getX(), 2)
				+Math.pow(e.getY()-balls.get(0).getY(), 2));
		if(d<balls.get(0).getRadius() && balls.get(0).getSpeed().x == 0 && balls.get(0).getSpeed().y == 0) {
			eX = e.getX();
			eY = e.getY();
			drag = true;
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(drag) {
//		balls.get(0).addxSpeed((balls.get(0).getX()-eX)*0.01);
//		balls.get(0).addySpeed((balls.get(0).getY()-eY)*0.01);
		balls.get(0).getSpeed().add(dragForce);
		}
		drag = false;
	}





}
