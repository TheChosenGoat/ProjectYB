package Collision2D;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import CollisionEngine.*;

import javax.swing.*;



public class Board extends JPanel implements Runnable, ActionListener{
	private JFrame frame;
	private JPanel lowerPanel;
	private JButton addBallBtn;
	private JButton restartBtn;

	private JPanel midLowerPanel;
	private JButton backToLauncher;
	private JButton magicBtn;
	private boolean magicFlag = false;

	//While this is true the thread is running
	private boolean running = true;

	private final static int xBounds = 400;
	private final static int yBounds = 400;
	//A variable that is the friction teller.
	protected static final float mu = 0.0000f;


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
	//A Ball arrayList for all the holes- USED ONLY IN BILLIARD
	private static ArrayList<Ball> holes = new ArrayList<Ball>();


	//jumpConstant is the default jump value.
	private final static long jumpConstant=1;
	//actualJump is the actual jump value.
	private static long actualJump=100;


	public void bootUp() {
		System.out.println("bootup");
		//creates ArrayList of balls and adds balls to it.
		balls= new ArrayList<Ball>();

		this.setBounds(0, 0, xBounds+1, yBounds);
		this.setBackground(Color.DARK_GRAY);


		addBallBtn = new JButton("Add Ball");
		addBallBtn.setPreferredSize(new Dimension(xBounds/4, 80));
		addBallBtn.addActionListener(this);
		addBallBtn.setActionCommand("AddBall");

		magicBtn = new JButton("Magic");
		//		magicBtn.setPreferredSize(new Dimension(xBounds/3, 80));
		magicBtn.addActionListener(this);
		magicBtn.setActionCommand("Magic");

		backToLauncher = new JButton("Back to Launcher");
		//		backToLauncher.setPreferredSize(new Dimension(xBounds/3, 80));
		backToLauncher.addActionListener(this);
		backToLauncher.setActionCommand("BackToLauncher");

		restartBtn = new JButton("Restart");
		restartBtn.setPreferredSize(new Dimension(xBounds/4, 80));
		restartBtn.addActionListener(this);
		restartBtn.setActionCommand("Restart");


		midLowerPanel = new JPanel();
		midLowerPanel.setSize(xBounds/4, 80);
		midLowerPanel.setLayout(new BorderLayout());
		midLowerPanel.add(magicBtn,BorderLayout.NORTH);
		midLowerPanel.add(backToLauncher,BorderLayout.CENTER);


		lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		lowerPanel.setSize(xBounds, 80);
		lowerPanel.add(addBallBtn,BorderLayout.WEST);
		lowerPanel.add(midLowerPanel,BorderLayout.CENTER);
		lowerPanel.add(restartBtn,BorderLayout.EAST);


		frame= new JFrame();
		frame.setTitle("2D Collision");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(xBounds, yBounds+100);
		frame.setResizable(false);
		frame.setVisible(true);
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
		if(!magicFlag)
			super.paintComponent(g);
		g.setColor(Color.ORANGE);
		g.drawRect(0, 0, xBounds-1, yBounds-1);

		for(int i=0; i<balls.size(); i++) {
			g.setColor(balls.get(i).getColor());	
			g.fillOval((int) (balls.get(i).getX()-balls.get(i).getRadius()),
					(int) (balls.get(i).getY()-balls.get(i).getRadius()),
					(int) balls.get(i).getRadius()*2,
					(int) balls.get(i).getRadius()*2);

			g.setColor(Color.RED);

			//this should be another button
			//			g.drawLine((int)balls.get(i).getX(),
			//					(int)balls.get(i).getY(), 
			//					(int)((balls.get(i).getX()+ balls.get(i).getxSpeed()*200)),
			//					(int)((balls.get(i).getY()+ balls.get(i).getySpeed()*200)));
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
		if ("AddBall".equals(e.getActionCommand())) {
			ExtraMethods.addBall(balls, xBounds, yBounds, mu, colors);
		}
		else if ("Magic".equals(e.getActionCommand())) {
			magicFlag= !magicFlag;
		}
		else if ("Restart".equals(e.getActionCommand())) {
			balls.clear();
			magicFlag= false;
		}
		else if ("BackToLauncher".equals(e.getActionCommand())) {
			a_GeneralGamePackage.GameLauncher.main(null);
			terminate();
			this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
		}
	}





}
