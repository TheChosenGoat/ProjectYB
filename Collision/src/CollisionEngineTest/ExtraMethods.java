package CollisionEngineTest;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

//This class holds some extra methods, about adding balls, pausing, reseting and more.
public class ExtraMethods {

	/*
	 * This class should not be constructed.
	 */
	private ExtraMethods() {
	}

	/*
	 *A func that adds a ball to the balls ArrayList in Board.
	 *A ball is added only if it's coordinates are inside the play area, and also
	 *not inside another ball.
	 *
	 *@param balls- the ArrayList of current balls.
	 *@param xBounds- X limit of the board.
	 *@param yBounds- Y limit of the board.
	 *@param colors- array of current colors.
	 *@return the balls ArrayList, weather it was changed or not.
	 */
	public static ArrayList<Ball> addBall(ArrayList<Ball> balls, 
			int xBounds,
			int yBounds,
			float mu,
			Color[] colors) {
		
		//Creating a random ball.
		Ball ball = new Ball(ThreadLocalRandom.current().nextInt(0,xBounds),
				ThreadLocalRandom.current().nextInt(0,yBounds),
				ThreadLocalRandom.current().nextInt(9,15),
				ThreadLocalRandom.current().nextInt(6,13),
				ThreadLocalRandom.current().nextDouble(0.0,0.4),
				ThreadLocalRandom.current().nextDouble(0.0,0.4),
				colors[ThreadLocalRandom.current().nextInt(0, colors.length  )]); 
		ball.acceleration = new Vector2D((mu*10),(float)(ball.speed.getAngle()+Math.PI));
		System.out.println(ball.acceleration);
		//Checking if the balls's coordinates are inside the play area.
		//If not, quitting the func and the ball isn't spawned.
		if(xBounds-ball.x-ball.radius<0 || yBounds-ball.y-ball.radius<0)
			return balls;
		if(ball.x-ball.radius<0 || ball.y-ball.radius<0)
			return balls;

		//Checking if the ball's coordinates aren't inside another ball.
		//If the ball's coordinates are inside another ball quitting the func
		//and the ball isn't spawned.
		for(int i = 0; i<balls.size(); i++) {
			double d = Math.sqrt(Math.pow(balls.get(i).x-ball.x, 2)+Math.pow(balls.get(i).y-ball.y, 2));
			if(d<balls.get(i).radius+ball.radius) {
				return balls;
			}
		}


		balls.add(ball);
		return balls;
	}

	/*
	 *A func that adds a ball to the balls ArrayList in Board. Used in DodgeTheBall.
	 *A ball is added only if it's coordinates are inside the play area, not
	 *inside another ball, and also not near the mouse pointer.
	 *
	 *@param balls- the ArrayList of current balls.
	 *@param eX -the last known X coordinates of the mouse pointer.
	 *@param eY -the last known Y coordinates of the mouse pointer.
	 *@param xBounds- X limit of the board.
	 *@param yBounds- Y limit of the board.
	 *@param colors- array of current colors.
	 */
	public static void addBallDodge(ArrayList<Ball> balls, 
			double eX, double eY,int xBounds, int yBounds,
			Color[] colors) {
		
		//Creating a random ball.
		Ball ball = new Ball(ThreadLocalRandom.current().nextInt(0,xBounds),
				ThreadLocalRandom.current().nextInt(0,yBounds),
				ThreadLocalRandom.current().nextInt(12,15),
				ThreadLocalRandom.current().nextInt(8,9),
				ThreadLocalRandom.current().nextDouble(0.0,0.2),
				ThreadLocalRandom.current().nextDouble(0.0,0.2),
				colors[ThreadLocalRandom.current().nextInt(0, colors.length  )]); 
		//If the ball is inside a radius of 70 from the mouse quitting the func
		//and the ball isn't spawned.
		if(Math.sqrt(Math.pow(ball.x- eX, 2)+Math.pow(ball.y- eY, 2))<70)
			return;

		//Checking if the balls's coordinates are inside the play area.
		//If not, quitting the func and the ball isn't spawned.
		if(xBounds-ball.x-ball.radius<0 || yBounds-ball.y-ball.radius<0)
			return;
		if(ball.x-ball.radius<0 || ball.y-ball.radius<0)
			return;

		//Checking if the ball's coordinates aren't inside another ball.
		//If the ball's coordinates are inside another ball quitting the func
		//and the ball isn't spawned.
		for(int i = 0; i<balls.size(); i++) {
			double d = Math.sqrt(Math.pow(balls.get(i).x-ball.x, 2)+Math.pow(balls.get(i).y-ball.y, 2));
			if(d<balls.get(i).radius+ball.radius) {
				return;
			}
		}


		balls.add(ball);

	}
	/*
	 * A func that checks if the mouse pointer is touching a ball. Used in DodgeTheBall.
	 * 
	 * @param x- x coordinate of ball.
	 * @param y- y coordinate of ball.
	 * @param radius- radius of ball.
	 * @param eX -the last known X coordinates of the mouse pointer.
	 * @param eY -the last known Y coordinates of the mouse pointer.
	 * @return - true if touch false if not.
	 */
	public static boolean touchesAnotherBall(double x, double y, double radius, double eX, double eY) {
		double d = Math.sqrt(Math.pow(x-eX, 2)+Math.pow(y-eY, 2));
		if(d<radius) {
			return true;
		}

		//No ball was touched.
		return false;
	}


}
