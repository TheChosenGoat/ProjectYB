package CollisionEngine;

import java.awt.Color;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.Scanner;


import javax.swing.*;

import Billiard.Board;
import CollisionEngine.Ball;
import CollisionEngine.Formulas;
import CollisionEngine.Vector2D;
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
	private float mu;

	public static boolean xOrYWall; //X walls or Y walls, where will the ball hit.
	// REMOVE THIS WHEN YOU CAN!!!!!!!!
	public static boolean xOrYWallActual;
	boolean holeFall = false;
	private double t; // next time of crash of balls
	public static final double NO_COLLISION = 100000;
	public static final double EPSILON = -0.0000000001;
	Scanner reader = new Scanner(System.in);
	//A String for using the Scanner while debugging.
	private String read = "";

	//constructon for the CollisionHandler.
	public CollisionHandler(int xBounds, int yBounds, float mu) {
		this.xBounds = xBounds;
		this.yBounds = yBounds;
		this.mu = mu;
	}



	/*
	 * A func that checks when the next collision will happen
	 * 
	 * @param balls- the ArrayList of current balls.
	 */
	public double checkClosestTime(ArrayList<Ball> balls, ArrayList<Ball> holes){
		double ti = 0;
		double th = NO_COLLISION;
		double tw = 0;
		double LowestT = NO_COLLISION;
		Billiard.Board.ballsAreMoving = false;
		for(int i= 0; i< balls.size()-1; i++) {
			if(!balls.get(i).speed.equals(new Vector2D(0,0)))
				Billiard.Board.ballsAreMoving = true;
			for(int j= i+1; j< balls.size(); j++) {
				ti= Formulas.formula5(balls.get(i), balls.get(j));
				if (ti <= LowestT && ti>=EPSILON) {
					LowestT = ti;
					b1Pointer = balls.get(i);
					b2Pointer = balls.get(j);
				}

			}

		}
		if(!holes.isEmpty()){
			for(int i= 0; i< balls.size(); i++) {

				for(int j= 0; j< holes.size(); j++) {
					th= Formulas.formula5(balls.get(i), holes.get(j));
					if (th <= LowestT && th>=EPSILON) {
						LowestT = th;
						b1Pointer = balls.get(i);
						b2Pointer = null;
						holeFall = true;
					}

				}
			}
		}
		for(int i= 0; i< balls.size(); i++) {
			tw = Formulas.formula4WithAcceleration(balls.get(i), xBounds, yBounds, mu);
			if(tw<LowestT && tw>=EPSILON) {
				LowestT = tw;
				b1Pointer = balls.get(i);
				b2Pointer = null;
				holeFall = false;
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
	public long handle(ArrayList<Ball> balls, ArrayList<Ball> holes, long actualJump, long jumpConstant) {
		//For every ball in balls:
		for(int i= 0; i< balls.size(); i++) {

			//Stops a ball that has stopped moving
			if((balls.get(i).speed.x>=0 && balls.get(i).acceleration.x>balls.get(i).speed.x) ||
					(balls.get(i).speed.x<=0 && balls.get(i).acceleration.x<balls.get(i).speed.x) ||
					(balls.get(i).speed.y>=0 && balls.get(i).acceleration.y>balls.get(i).speed.y) ||
					(balls.get(i).speed.y<=0 && balls.get(i).acceleration.y<balls.get(i).speed.y)
					) {
				balls.get(i).setSpeed(new Vector2D(0, 0));
				balls.get(i).setAcceleration(new Vector2D(0, 0));

			}
			//If ball is still moving update it's acceleration.
			else if (balls.get(i).speed.x != 0 && balls.get(i).speed.y != 0)
				balls.get(i).acceleration = new Vector2D((mu*10),(float)(balls.get(i).speed.getAngle()+Math.PI));

		}
		//Check when the closest crash will happen.
		t = checkClosestTime(balls, holes);
		//If the closest crash will happen in less than the normal jump time:
		if(t<= jumpConstant) {
			actualJump = (long)t;
			//For every ball in balls:
			for(int i= 0; i< balls.size(); i++) {
				//Add acceleration to balls.
				balls.get(i).speed.add(balls.get(i).acceleration);
				//Move balls 
				balls.get(i).x +=balls.get(i).speed.x*t;
				balls.get(i).y +=balls.get(i).speed.y*t;

			}
			//If the closest collision will happen between two balls:
			if(b2Pointer!=null) {
				collide();
			}
			//else (Meaning the crash in either between a ball and a wall or a ball and a hole):
			else {
				//If a ball fell in a hole:
				if(holeFall) {
					//If the falling ball is a white ball:
					if(b1Pointer.color == Color.WHITE){
						b1Pointer.x = (7000);
						b1Pointer.y = (7000);
						b1Pointer.speed = new Vector2D(0, 0);
						Billiard.Board.whiteHasFallen = true;
					}
					//If the falling ball is a red ball add redScore and remove ball
					else if(b1Pointer.color == (Color.RED)){
						Billiard.Board.redScore++;
						balls.remove(b1Pointer);
					}
					//If the falling ball is a blue ball add blueScore and remove ball
					else if(b1Pointer.color == (Color.BLUE)){
						Billiard.Board.blueScore++;
						balls.remove(b1Pointer);
					}
					//reset holeFall after fall.
					holeFall = false;

				}
				//Else if the next crash will happen with the left or white walls:
				else if(xOrYWallActual == true) 
					b1Pointer.speed.x = b1Pointer.speed.x*-1;
				//Else if the next crash will happen with the up or down walls:
				else 
					b1Pointer.speed.y = b1Pointer.speed.y*-1;

			}


		}
		else if(t>jumpConstant) {
			actualJump = jumpConstant;
			for(int i= 0; i< balls.size(); i++) {
				balls.get(i).speed.add(balls.get(i).acceleration);

				balls.get(i).x +=balls.get(i).speed.x*jumpConstant;
				balls.get(i).y +=balls.get(i).speed.y*jumpConstant;
			}
		}
		return actualJump;
	}






	//END OF MOVING AND CRASHING THE balls.




}
