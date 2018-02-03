package CollisionEngine;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Ball {

	protected double x,y;
	protected int radius;
	protected float mass;
	protected Vector2D speed;
	protected Color color;

	public Ball(double x, double y, int radius, float mass, double xSpeed, double ySpeed, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.mass = mass;
		speed = new Vector2D(xSpeed, ySpeed);
		this.color = color;
	}
	public Ball() {
		super();
		this.x = 0;
		this.y = 0;
		this.radius = 0;
		this.mass = 0;
		speed = new Vector2D(0, 0);
		this.color = color;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public double getxSpeed() {
		return speed.x;
	}

	public void setxSpeed(double xSpeed) {
		this.speed.x = xSpeed;
	}

	public double getySpeed() {
		return speed.y;
	}

	public void setySpeed(double ySpeed) {
		this.speed.y = ySpeed;
	}
	public Vector2D getSpeed() {
		return speed;
	}
	public void setSpeed(Vector2D speed) {
		this.speed = speed;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	

	
}
