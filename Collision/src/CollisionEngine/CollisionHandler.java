package CollisionEngine;

import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.Scanner;
/*
 * A class that handles, calculates and executes the Collisions of the balls.
 *  
 * This class holds all the info variables of the lowest time, lowest time 
 * index, available crashes,
 */
public class CollisionHandler {
	private Ball b1Pointer = null;
	private Ball b2Pointer = new Ball();
	
	private int xBounds;
	private int yBounds;
	
	public static boolean xOrYWall; //X walls or Y walls, where will the ball hit.
	// REMOVE THIS WHEN YOU CAN!!!!!!!!
	public static boolean xOrYWallActual;
	private double t; // next time of crash of balls
	public static final double NO_COLLISION = 100000;
	public static final double EPSILON = -0.0000000001;
	Scanner reader = new Scanner(System.in);
	//A String for using the Scanner while debugging.
	private String read = "";

	//constructon for the CollisionHandler.
	public CollisionHandler(int xBounds, int yBounds) {
		this.xBounds = xBounds;
		this.yBounds = yBounds;
	}



	/*
	 * A func that checks when the next collision will happen
	 * 
	 * @param balls- the ArrayList of current balls.
	 */
	public double checkClosestTime(ArrayList<Ball> balls){
		double ti = 0;
		double tw = 0;
		double LowestT = NO_COLLISION;
		for(int i= 0; i< balls.size()-1; i++) {

			for(int j= i+1; j< balls.size(); j++) {
				ti= Formulas.formula5(balls.get(i), balls.get(j));
				if (ti <= LowestT && ti>=EPSILON) {
					LowestT = ti;
					b1Pointer = balls.get(i);
					b2Pointer = balls.get(j);
				}

			}

		}
		for(int i= 0; i< balls.size(); i++) {
			tw = Formulas.formula4(balls.get(i), xBounds, yBounds);
			if(tw<LowestT && tw>=EPSILON) {
				LowestT = tw;
				b1Pointer = balls.get(i);
				b2Pointer = null;
				xOrYWallActual = xOrYWall;
			}
		}
		return LowestT;
	}
	/*
	 * A func that calculates the collision of two balls.
	 */
	public void collide() {
		Vector2D normalVector = new Vector2D(b2Pointer.x - b1Pointer.x , b2Pointer.y - b1Pointer.y);
		normalVector.normalize();
		double speed1NLength = b1Pointer.speed.dot(normalVector);
		double speed2NLength = b2Pointer.speed.dot(normalVector);

		Vector2D speed1NComponent = normalVector.getMultiplied(speed1NLength);
		Vector2D speed2NComponent = normalVector.getMultiplied(speed2NLength);

		Vector2D speed1TComponent = b1Pointer.speed.getSubtracted(speed1NComponent);
		Vector2D speed2TComponent = b2Pointer.speed.getSubtracted(speed2NComponent);

		double temp = Formulas.formula1(b1Pointer.mass, b2Pointer.mass, speed1NLength, speed2NLength);
		speed1NLength = Formulas.formula2(speed1NLength, speed2NLength, temp);
		speed2NLength = temp;

		speed1NComponent = normalVector.getMultiplied(speed1NLength);
		speed2NComponent = normalVector.getMultiplied(speed2NLength);


		b1Pointer.speed = speed1TComponent.getAdded(speed1NComponent);
		b2Pointer.speed = speed2TComponent.getAdded(speed2NComponent);

		//		double u2 =  Formulas.formula1(b1Pointer.mass, b2Pointer.mass, b1Pointer.speed.x, b2Pointer.speed.x);
		//		b1Pointer.speed.x = Formulas.formula2(b1Pointer.speed.x, b2Pointer.speed.x, u2);
		//		b2Pointer.speed.x = u2;
	}

	/*
	 * A func that handles the whole collision
	 * 
	 * @param balls- the ArrayList of current balls.
	 * @param actualJump- the Actual jump that will happen
	 * @param jumpConstant- the normal jump time
	 */
	public long handle(ArrayList<Ball> balls, long actualJump, long jumpConstant) {
		t = checkClosestTime(balls);
		if(t<= jumpConstant) {
			actualJump = (long)t;
			for(int i= 0; i< balls.size(); i++) {

				balls.get(i).x +=balls.get(i).speed.x*t;
				balls.get(i).y +=balls.get(i).speed.y*t;

			}
			if(b2Pointer!=null) {
				collide();
			}
			else {
				if(xOrYWallActual == true) 
					b1Pointer.speed.x = b1Pointer.speed.x*-1;

				else 
					b1Pointer.speed.y = b1Pointer.speed.y*-1;

			}


		}
		else if(t>jumpConstant) {
			actualJump = jumpConstant;
			for(int i= 0; i< balls.size(); i++) {

				balls.get(i).x +=balls.get(i).speed.x*jumpConstant;
				balls.get(i).y +=balls.get(i).speed.y*jumpConstant;
			}
		}
		return actualJump;
	}






	//END OF MOVING AND CRASHING THE balls.




}
