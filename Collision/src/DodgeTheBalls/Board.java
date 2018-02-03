package DodgeTheBalls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import CollisionEngine.*;

import javax.swing.*;



public class Board extends JPanel implements Runnable, ActionListener, MouseMotionListener{
	private int score =0;
	private int highScore;
	//Counts the frames passed from the start of a game.
	private int framesPassed = 0;
	//If this boolean is true the game is over.
	private boolean quit = false;
	//Holds the last known coordinates of the mouse.
	private double eX,eY;

	//These are used when eating a green ball
	//The boolean is used for a flag in repaint. The ball for drawing and calculating.
	//NEEDS WORKING.
	/*
	 * Works like this:
	 * If a green ball is touched it is detected in the MouseMoved func.
	 * There greenFramesPassedStamp becomes equal to framesPassed, and
	 * greenBallPointer becomes equal to the touched green ball.
	 * In the paintComponent, if the difference between framesPassed and
	 * greenFramesPassedStamp is lower than 100, and greenBallPointer exists:
	 * it draws a circle around the greenBallPointer Coordinates. the circle 
	 * is exactly as large as the hit-zone of the touched green ball.
	 */
	private int greenFramesPassedStamp;
	private Ball greenBallPointer;

	//lowerPanel is a panel in frame, restartBtn is a button in lowerPanel.
	private JFrame frame;
	private JFrame tutorialFrame;
	private JPanel tutorialPanel;
	private JLabel tutorialLabel;
	private JButton startButton;
	private JPanel lowerPanel;
	private JButton restartBtn;
	private JButton backToLauncher;

	//While this is true the thread is running
	private boolean running = true;


	//Those mark the dimensions of the play area. 
	private final static int xBounds = 400;
	private final static int yBounds = 400;
	
	//A variable that is the friction teller.
	protected static final float mu = 0.0000f;
	//A Color array. used when spawning balls with random colors.
	//Colors are:
	/*
	 * BLACK -- 12
	 * WHITE -- 1
	 * GREEN -- 3
	 * BLUE -- 1
	 * PINK -- 3
	 * GRAY -- 4
	 * MAGENTA -- 4			
	 * RED -- 4
	 * ORANGE -- 4
	 * YELLOW --4
	 */
	//Total -- 40
	static final Color[] colors = {				Color.BLACK,
			Color.BLACK,			
			Color.BLACK,
			Color.BLACK,
			Color.BLACK,
			Color.BLACK,	
			Color.BLACK,	
			Color.BLACK,	
			Color.BLACK,	
			Color.BLACK,
			Color.BLACK,
			Color.BLACK,
			Color.WHITE,
			Color.GREEN,
			Color.GREEN,
			Color.GREEN,
			Color.BLUE,
			Color.PINK,
			Color.PINK,
			Color.PINK,
			Color.GRAY,
			Color.GRAY,
			Color.GRAY,
			Color.GRAY,
			Color.MAGENTA,
			Color.MAGENTA,
			Color.MAGENTA,
			Color.MAGENTA,			
			Color.RED,
			Color.RED,
			Color.RED,
			Color.RED,
			Color.ORANGE,
			Color.ORANGE,
			Color.ORANGE,
			Color.ORANGE,
			Color.YELLOW,
			Color.YELLOW,
			Color.YELLOW,
			Color.YELLOW,
	};
	//A collision handler instance. used for handling collisions (checking when they 
	//will happen and calculating the collision itself).
	private static CollisionHandler handleMan;
	//A Ball ArrayList that holds all the balls currently existing.
	private static ArrayList<Ball> balls = null;
	//A Ball arrayList for all the holes- USED ONLY IN BILLIARD
	private static ArrayList<Ball> holes = new ArrayList<Ball>();

	//jumpConstant is the default jump value.
	private final static long jumpConstant=1;
	//actualJump is the actual jump value.
	private static long actualJump=100;

	//This func is the boot-up func, it initializes all of the variables
	//of a board instance.
	public void bootUp() {
		System.out.println("bootup");

		highScore = ExtraMethodsDatabase.readHighScoreFromFile();

		//creates ArrayList of balls and adds balls to it.
		balls= new ArrayList<Ball>();

		addMouseMotionListener(this);
		this.setBounds(0, 0, xBounds+1, yBounds+1);
		this.setBackground(Color.DARK_GRAY);


		restartBtn = new JButton("Restart Game");
		restartBtn.setPreferredSize(new Dimension(xBounds, 80));
		restartBtn.addActionListener(this);
		restartBtn.setActionCommand("Restart");

		backToLauncher = new JButton("Back to Launcher");
		//		backToLauncher.setPreferredSize(new Dimension(xBounds/3, 80));
		backToLauncher.addActionListener(this);
		backToLauncher.setActionCommand("BackToLauncher");

		lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		lowerPanel.setPreferredSize(new Dimension(xBounds, 80));
		lowerPanel.add(restartBtn, BorderLayout.CENTER);
		lowerPanel.add(backToLauncher, BorderLayout.WEST);

		frame = new JFrame();
		frame.setTitle("Dodge the Balls");
		frame.add(this);
		frame.add(lowerPanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(xBounds, yBounds+130);
		frame.setResizable(false);
		frame.setVisible(true);
		handleMan = new CollisionHandler(xBounds, yBounds, mu);
	}

	public void preBootUp() {
		startButton = new JButton("Start Game");
		//		startButton.setPreferredSize(new Dimension(100, 80));
		startButton.addActionListener(this);
		startButton.setActionCommand("StartGame");


		tutorialLabel = new JLabel();
		tutorialLabel.setPreferredSize(new Dimension(xBounds,yBounds));
		tutorialLabel.setText("dab");

		tutorialPanel = new JPanel();
		tutorialPanel.setBackground(Color.WHITE);
		tutorialPanel.setLayout(new BorderLayout());
		tutorialPanel.setPreferredSize(new Dimension(xBounds+1,yBounds+1));
		tutorialPanel.add(tutorialLabel, BorderLayout.NORTH);
		tutorialPanel.add(startButton, BorderLayout.SOUTH);

		tutorialFrame= new JFrame();
		tutorialFrame.setTitle("Dodge the Balls- Menu");
		//		frame.setLayout(new BorderLayout());
		tutorialFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tutorialFrame.add(tutorialPanel);
		tutorialFrame.setSize(xBounds, yBounds+130);
		tutorialFrame.setResizable(false);
		tutorialFrame.setVisible(true);
		handleMan = new CollisionHandler(xBounds, yBounds, mu);
	}

	//Main method
	public static void main(String[] args) {
		System.out.println("main");
		//creating and booting up a Board.
		Board board = new Board();
		board.preBootUp();
		//		board.bootUp();
		//Creating and Starting a new Thread.
		Thread t1= new Thread(board);
		t1.start();
	}

	//Terminates the thread by chaging thread boolean to false;
	public void terminate() {
		running = false;
	}

	//The thread that is actually the game.
	@Override
	public void run() {
		while(running) {
			try{
				//If the frame is closing, terminating the thread.
				if(!this.frame.isVisible() && !this.tutorialFrame.isVisible())
					terminate();
				actualJump=jumpConstant;
				System.out.println("cycle");
				//If game is not over up framesPassed by one and call the 
				//handle function.
				if(!quit) {
					checkTouch();
					framesPassed++;
					actualJump = handleMan.handle(balls, holes, actualJump, jumpConstant);
					repaint();
				}
				//At certain points in the game add a ball.
				if((framesPassed%300==0 || framesPassed%500==0 || framesPassed%700==0) && !quit)
					ExtraMethods.addBallDodge(balls, eX, eY, xBounds, yBounds, colors); 



				Thread.sleep(actualJump);
			}
			catch(Exception e) {}
		}

	}


	public void paintComponent(Graphics g) {
		System.out.println("repainting");
		super.paintComponent(g);
		g.setColor(Color.ORANGE);
		g.drawRect(0, 0, xBounds-1, yBounds-1);

		//If green was touched drawing a green circle around where it has been.
		if(greenBallPointer != null && framesPassed-greenFramesPassedStamp < 100) {
			g.setColor(Color.GREEN);
			g.drawOval((int)(greenBallPointer.getX()-110), (int)(greenBallPointer.getY()-110), 220, 220);
		}
		else{
			greenFramesPassedStamp = 0;
		}

		//Drawing the balls.
		for(int i=0; i<balls.size(); i++) {
			g.setColor(balls.get(i).getColor());	
			g.fillOval((int) (balls.get(i).getX()-balls.get(i).getRadius()),
					(int) (balls.get(i).getY()-balls.get(i).getRadius()),
					(int)balls.get(i).getRadius()*2,
					(int)balls.get(i).getRadius()*2);
		}

		//If game is over printing game over all over the place.
		if(quit == true) {
			g.setColor(Color.RED);
			for(int i = 0; i<400; i= i+30) {
				for(int j = 0; j<400; j= j+80) {
					g.drawString("GAME OVER", j, i);
				}
			}
		}

		g.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
		FontMetrics fontMetrics = g.getFontMetrics();

		g.setColor(Color.CYAN);
		g.drawString("Score: "+String.valueOf(score), 5, yBounds+20);
		g.setColor(Color.GREEN);
		g.drawString("HighScore: "+String.valueOf(highScore),xBounds - fontMetrics.stringWidth("HighScore: "+String.valueOf(highScore)), yBounds+20);

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
		// If button pressed is restart button restarting the game.
		if ("Restart".equals(e.getActionCommand())) {
			System.out.println("Restart");
			balls.clear();
			score = 0;
			highScore = ExtraMethodsDatabase.readHighScoreFromFile();
			framesPassed = 0;
			greenBallPointer = null;
			quit = false;
		}
		else if ("BackToLauncher".equals(e.getActionCommand())) {
			a_GeneralGamePackage.GameLauncher.main(null);
			terminate();
			this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
		}

		else if ("StartGame".equals(e.getActionCommand())) {
			//			balls.clear();
			//			score = 0;
			//			highScore = ExtraMethodsDatabase.readHighScoreFromFile();
			//			framesPassed = 0;
			//			greenBallPointer = null;
			//			quit = false;
			bootUp();
			this.tutorialFrame.dispatchEvent(new WindowEvent(this.tutorialFrame, WindowEvent.WINDOW_CLOSING));
			repaint();
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		eX = e.getX();
		eY = e.getY();

	}

	public void checkTouch() {
		//-999 means didn't touch another ball
		int touchIndex = -999;

		for(int i = 0; i<balls.size(); i++) {
			//If there is a touch touchIndex = index of the touched ball
			//and quitting the for loop.
			if(ExtraMethods.touchesAnotherBall(balls.get(i).getX(), balls.get(i).getY(),
					balls.get(i).getRadius(), eX, eY)) {
				touchIndex = i;
				break;
			}
		}	

		//If touched another ball and game is on.
		if(touchIndex != -999 && quit == false) {
			//If black was touched stopping game and updating high score if necessary.
			if(balls.get(touchIndex).getColor().equals(Color.BLACK)) {
				if (score>highScore) { 
					ExtraMethodsDatabase.writeHighScoreToFile(score);
					highScore = ExtraMethodsDatabase.readHighScoreFromFile();
				}
				quit = true;
			}

			//If white was touched adding a point, removing all Black balls
			//and removing the touched white ball.
			else if(balls.get(touchIndex).getColor().equals(Color.WHITE)) {
				score++;
				balls.remove(touchIndex);
				for(int i = 0; i<balls.size(); i++) {
					if(balls.get(i).getColor().equals(Color.BLACK)) {
						balls.remove(i);
						i--;
					}
				}
			}
			//If green was touched removing ALL balls in Radius of 110 from touched green.
			//Player gets a point for each ball removed including blacks.
			//If removing a bonus ball the bonus isn't executed.
			else if(balls.get(touchIndex).getColor().equals(Color.GREEN)) {
				score++;
				greenFramesPassedStamp = framesPassed;
				greenBallPointer = balls.get(touchIndex);
				balls.remove(touchIndex);
				for(int i = 0; i<balls.size(); i++) {
					double d = Math.sqrt(Math.pow(balls.get(i).getX()-greenBallPointer.getX(), 2)
							+Math.pow(balls.get(i).getY()-greenBallPointer.getY(), 2));
					if(d-balls.get(i).getRadius()<110) {
						balls.remove(i);
						score++;
						i--;
					}
				}
			}
			//Red was touched 
			//if red was touched player gets +5 more points.
			else if(balls.get(touchIndex).getColor().equals(Color.RED)) {
				balls.remove(touchIndex);
				score+=5;
			}
			else {
				balls.remove(touchIndex);
				score++;
			}
			if (score>highScore) { 
				ExtraMethodsDatabase.writeHighScoreToFile(score);
				highScore = ExtraMethodsDatabase.readHighScoreFromFile();
			}
		}
	}



}
