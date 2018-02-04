package Billiard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import CollisionEngine.*;

import javax.swing.*;

import com.sun.org.apache.xml.internal.serialize.OutputFormat.Defaults;

import CollisionEngine.Ball;
import CollisionEngine.CollisionHandler;
import CollisionEngine.Vector2D;

public class Board extends JPanel implements Runnable, ActionListener, MouseListener, MouseMotionListener{
	private JFrame frame;
	private static JPanel lowerPanel;

	private JButton backToLauncher;

	//While this is true the thread is running
	private boolean running = true;
	//If this boolean is true the game is over.
	public static boolean quit =false;
	//Billiard Variables.
	//turn == 0 - RED's turn. 1 is BLUE's.
	public static int turn = 0;
	//Red and blue scores.
	public static int redScore = 0;
	public static int blueScore = 0;
	//Did the player hit the ball? true if yes.
	public static boolean playerHasMadeHisTurn = false;
	//Are balls moving? True if yes.
	public static boolean ballsAreMoving = false;
	//Did the black ball fall into a hole? true if yes.
	public static boolean blackHasFallen = false;
	//Did the white ball fall into a hole? true if yes.
	public static boolean whiteHasFallen = false;
	//Did white touch another ball? true if yes.
	public static boolean whiteTouchedAnotherBall = false;
	//Did ball the same color as the current player fall? true if yes.
	public static boolean currentColorBallHasFallen = false;
	//Did all the red balls fell? true if yes.
	public static boolean allRedBallsFell = false;
	//Did all the blue balls fell? true if yes.
	public static boolean allBlueBallsFell = false;
	private double eX = 0;
	private double eY = 0;
	//Is the player dragging the mouse to shoot the white ball?
	private boolean drag = false;
	//How far and to which direction is the mouse dragged.
	private Vector2D dragForce = new Vector2D();

	private final static int xBounds = 1136;
	private final static int yBounds = 568;
	//A white line that makes the border of where a white ball can be placed.
	private final static int whiteLine = xBounds/5;
	public final static int ballSize = 18;
	//A variable that is the friction teller.
	protected static final float mu = 0.00003f;

	static final Color[] colors = {};

	private static CollisionHandler handleMan;
	//A Ball ArrayList that holds all the balls currently existing.
	private static ArrayList<Ball> balls = null;
	//list of fallen red balls
	public static int fallenRedBalls = 0;
	//list of fallen blue balls
	public static int fallenBlueBalls = 0;
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

		//Random turn- RED or BLUE
		turn = ThreadLocalRandom.current().nextInt(0, 2);
		System.out.println("#####################");
		System.out.println(turn);

		//creates ArrayList of balls and adds balls to it.
		balls= new ArrayList<Ball>();


		balls.add(new Ball(whiteLine,yBounds/2,18,4f,0,0,Color.WHITE));
		//1.444444444
		double blackSpotX = 0.825*xBounds;
		double blackSpotY = yBounds/2;
		balls.add(new Ball(blackSpotX-67, blackSpotY, ballSize,4f,0,0,Color.BLUE));
		balls.add(new Ball(blackSpotX-34, blackSpotY-19, ballSize,4f,0,0,Color.RED));
		balls.add(new Ball(blackSpotX-34, blackSpotY+19, ballSize,4f,0,0,Color.BLUE));

		balls.add(new Ball(blackSpotX, blackSpotY-38,ballSize,4f,0,0,Color.RED));

		balls.add(new Ball(blackSpotX, blackSpotY,ballSize,4f,0,0,Color.BLACK));

		balls.add(new Ball(blackSpotX, blackSpotY+38,ballSize,4f,0,0,Color.BLUE));

		balls.add(new Ball(blackSpotX+34, blackSpotY-57, ballSize,4f,0,0,Color.RED));
		balls.add(new Ball(blackSpotX+34, blackSpotY-19, ballSize,4f,0,0,Color.BLUE));
		balls.add(new Ball(blackSpotX+34, blackSpotY+19, ballSize,4f,0,0,Color.RED));
		balls.add(new Ball(blackSpotX+34, blackSpotY+57, ballSize,4f,0,0,Color.BLUE));

		balls.add(new Ball(blackSpotX+68, blackSpotY-74, ballSize,4f,0,0,Color.RED));
		balls.add(new Ball(blackSpotX+68, blackSpotY-37, ballSize,4f,0,0,Color.BLUE));
		balls.add(new Ball(blackSpotX+68, blackSpotY, ballSize,4f,0,0,Color.RED));
		balls.add(new Ball(blackSpotX+68, blackSpotY+37, ballSize,4f,0,0,Color.BLUE));
		balls.add(new Ball(blackSpotX+68, blackSpotY+74, ballSize,4f,0,0,Color.RED));


		this.setPreferredSize(new Dimension(xBounds, yBounds));
		this.setBackground(new Color(14, 122, 52));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		lowerPanel = new LowerJPanel();
		lowerPanel.setBackground(Color.LIGHT_GRAY);
		//		lowerPanel.setPreferredSize(new Dimension(xBounds, 72));


		backToLauncher = new JButton("Back to Launcher");
		//		backToLauncher.setPreferredSize(new Dimension(xBounds/3, 80));
		backToLauncher.addActionListener(this);
		backToLauncher.setActionCommand("BackToLauncher");





		frame= new JFrame();
		frame.setTitle("2D Collision");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(xBounds, yBounds+130);
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
				if(!quit){
					//If player has made his turn and no balls are moving:
					if(playerHasMadeHisTurn && !ballsAreMoving){
						//If a black ball has fallen:
						if(blackHasFallen){
							//If it's Red's turn not all the red balls fell in the former
							//turns and the black ball fell in this one then change turn to blue and quit.
							//RED WINS
							if(turn ==0 && !allRedBallsFell){
								turn = 1-turn;
								quit = true;
//								return;
							}
							//Same as last if but for blue.
							//BLUE WINS
							else if(turn ==1 && !allBlueBallsFell){
								turn = 1-turn;
								quit = true;
//								return;
							}

							//If this is red's turn and all the red balls already fell in 
							//former turns, and in this turn the black ball fell and 
							//the white didn't then quit.
							//RED WINS
							if(turn == 0 && allRedBallsFell && !whiteHasFallen){
								quit = true;
//								return;
							}
							//Same as last if but for blue.
							//BLUE WINS
							else if(turn == 1 && allBlueBallsFell && !whiteHasFallen){
								quit = true;
//								return;
							}
							//If it's red's turn, and his balls fell in former turns,
							//black ball fell in this turn, and white DID fall in this turn
							//then change turn and quit.
							//BLUE WINS
							if(turn == 0 && allRedBallsFell && whiteHasFallen){
								turn = 1-turn;
								quit = true;
//								return;
							}
							//Same as last if but for blue
							//RED WINS
							else if(turn == 1 && allBlueBallsFell && whiteHasFallen){
								turn = 1-turn;
								quit = true;
//								return;
							}
						}


						//If the white ball has fallen in a hole
						else if(whiteHasFallen){
							//Change turn and reset all the flags exept the whiteHasFallen one.
							//ballsAreMoving is already false;
							//That is because it will be used later when replacing the white ball
							//on Board.
							turn = 1-turn;	
							playerHasMadeHisTurn = false;
							whiteTouchedAnotherBall = false;
							currentColorBallHasFallen = false;
						}

						//If a ball of the same color as the current player falls in a hole:
						else if(currentColorBallHasFallen){
							//Reset all flags (whiteHasFallen and ballsAreMoving are already false). 
							//The turn remains with the same player.
							playerHasMadeHisTurn = false;
							currentColorBallHasFallen = false;
							whiteTouchedAnotherBall = false;
						}

						//If white ball didn't touch any other ball:
						else if(!whiteTouchedAnotherBall){
							//The other player gets the turn.
							turn = 1-turn;
							playerHasMadeHisTurn = false;
						}

						if(Board.fallenRedBalls == 7)
							Board.allRedBallsFell = true;

						if(Board.fallenBlueBalls == 7)
							Board.allBlueBallsFell = true;

						//Reset this for the next turn.
						whiteTouchedAnotherBall = false;


					}
					actualJump=jumpConstant;
					System.out.println("cycle");

					handleMan.handle(balls, holes, actualJump, jumpConstant);

				}
				repaint();
				lowerPanel.repaint();
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
		g.fillOval(whiteLine-3, yBounds/2-3, 6, 6);
		g.drawLine(whiteLine, 0, whiteLine, yBounds);
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

	public JPanel getLowerPanel() {
		return lowerPanel;
	}
	public void setLowerPanel(JPanel lowerPanel) {
		this.lowerPanel = lowerPanel;
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
		//If former player made white fall and current player hadn't played yet:
		if(whiteHasFallen && !playerHasMadeHisTurn){
			//if current Mouse X position is bigger the white line (minus radius):
			if(e.getX() > whiteLine-balls.get(0).getRadius()){
				balls.get(0).setX(whiteLine-balls.get(0).getRadius());
			}
			else
				balls.get(0).setX(e.getX());
			balls.get(0).setY(e.getY());
		}
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

		//If no balls are moving:
		if(!ballsAreMoving ){
			//If a white ball has not fallen:
			if(!whiteHasFallen){
				//start dragging.
				double d = Math.sqrt(Math.pow(e.getX()-balls.get(0).getX(), 2)
						+Math.pow(e.getY()-balls.get(0).getY(), 2));
				if(d<balls.get(0).getRadius()) {
					eX = e.getX();
					eY = e.getY();
					drag = true;
				}
			}
			else {
				//What it practically does here is lay down the white ball when mouse is 
				//pressed and not on another ball.
				//Happens if ball has fallen the last turn.
				for(int i= 1; i< balls.size(); i++) {
					double d = Math.sqrt(Math.pow(e.getX()-balls.get(i).getX(), 2)
							+Math.pow(e.getY()-balls.get(i).getY(), 2));
					if(d<balls.get(i).getRadius()+balls.get(0).getRadius()) {
						return;
					}
				}
				whiteHasFallen = false;
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(drag) {
			//Change relevant flags.
			playerHasMadeHisTurn = true;
			ballsAreMoving = true;
			//Add speed to the white ball.
			balls.get(0).getSpeed().add(dragForce);
			//No more dragging.
			drag = false;
		}
	}





}

//A class for the lower JPanel.
class LowerJPanel extends JPanel{
	public LowerJPanel() {
		// TODO Auto-generated constructor stub	
		setLayout( new BorderLayout() );
		setPreferredSize(new Dimension(Board.getXbounds(),102));
	}

	@Override 
	public void paintComponent(Graphics g) { 
		super.paintComponent(g); 
		g.setColor(Color.DARK_GRAY);
		g.drawLine(180, 0, 180, 200);
		g.drawLine(320, 0, 320, 200);


		g.setColor(Color.BLACK);
		g.drawString("RED Score: "+String.valueOf(Board.redScore), 20, 20);
		g.drawString("BLUE Score: "+String.valueOf(Board.blueScore), 20, 60);

		g.setColor(Color.RED);
		for(int i=0; i<7; i++){
			g.drawOval(20*(i+1), 25, Board.ballSize,Board.ballSize);
		}
		for(int i=0; i<Board.fallenRedBalls; i++){
			g.fillOval(20*(i+1), 25, Board.ballSize,Board.ballSize);
		}
		g.setColor(Color.BLUE);
		for(int i=0; i<7; i++){
			g.drawOval(20*(i+1), 65, Board.ballSize,Board.ballSize);
		}
		for(int i=0; i<Board.fallenBlueBalls; i++){
			g.fillOval(20*(i+1), 65, Board.ballSize,Board.ballSize);
		}

		if(Board.turn == 0){
			g.setColor(Color.RED);
			g.fillRect(200, 30, 100, 60);
			if(!Board.quit){
				g.setColor(Color.BLACK);
				g.drawString("Turn: RED Player", 200, 20);
			}
			else{
				g.setColor(Color.BLACK);
				g.drawString("RED PLAYER WINS!", 200, 20);
				g.setColor(Color.ORANGE);
				Font f = new Font("serif", Font.PLAIN, 100);
				g.setFont(f);
				g.drawString("WIN  RED  WIN", 340, 80);

			}
		}
		if(Board.turn == 1){
			g.setColor(Color.BLUE);
			g.fillRect(200, 30, 100, 60);
			if(!Board.quit){
				g.setColor(Color.BLACK);
				g.drawString("Turn: BLUE Player", 200, 20);
			}
			else{
				g.setColor(Color.BLACK);
				g.drawString("BLUE PLAYER WINS!", 200, 20);
				g.setColor(Color.ORANGE);
				Font f = new Font("serif", Font.PLAIN, 100);
				g.setFont(f);
				g.drawString("WIN  BLUE  WIN", 340, 80);

			}
		}
	} 
}
