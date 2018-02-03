package CollisionEngineTest;

public class Formulas {


	/*
	 * This class should not be constructed.
	 */
	private Formulas() {
	}


	//A class for all the formulas.


	/*
	 * A formula for calculating the speed of the second object in an elastic collision in 
	 * one dimension.
	 * 
	 * @param m1- mass of first object.
	 * @param m2- mass of second object.
	 * @param v1- speed of first object.
	 * @param v2- speed of second object.
	 * @return speed of second object after collision.
	 */
	public static double formula1 (float m1, float m2, double v1, double v2) {
		double u2 = (2*m1*v1-m1*v2+m2*v2)/(m1+m2);
		return u2;
	}

	/*
	 * A formula for calculating the speed of the first object in an elastic collision in
	 * one dimension after we found the speed of the second after the collision object.
	 * 
	 * @param v1- speed of first object.
	 * @param v2- speed of second object.
	 * @param u2- speed of second object after collision.
	 * @return speed of first object after collision.
	 */
	public static double formula2(double v1, double v2, double u2) {
		double u1 = -v1+v2+u2;
		return u1;
	}

	/*
	 * A formula for calculating when the crash of two given objects will happen in one 
	 * dimension.
	 *
	 * @param x01- current X coordinate of first object.
	 * @param x02- current X coordinate of second object.
	 * @param v01- current X speed of first object.
	 * @param v02- current X speed of second object.
	 * @return time when objects will collide.
	 */
	public static double formula3(double x01, double x02, double v01, double v02) {
		return (x02-x01)/(v01-v02);
	}

	/*
	 * A formula for calculating when the crash of an object with the wall will happen 
	 * in two dimensions.
	 * 
	 * @param ball1- a ball we want to know when will hit the wall.
	 * @return time when ball1 will hit wall. or CollisionHandler.NO_COLLISION if not.
	 */
	public static double formula4(Ball ball1, int xBounds, int yBounds) {
		//returning no collision if the ball has no speed at all.
		if(ball1.speed.x == 0 && ball1.speed.y == 0)
			return CollisionHandler.NO_COLLISION;
		//tX is time of crash with left and right walls.
		//tY is time of crash with up and down walls.
		//they are both reset to no collision.
		double tX = CollisionHandler.NO_COLLISION;
		double tY = CollisionHandler.NO_COLLISION;

		//calculating X crash time.
		if(ball1.speed.x >= CollisionHandler.EPSILON) 
			tX = (xBounds-ball1.radius-ball1.x)/ball1.speed.x;
		else if(ball1.speed.x <= -CollisionHandler.EPSILON)
			tX = (ball1.radius-ball1.x)/ball1.speed.x ;


		//Calculating Y crash time.
		if(ball1.speed.y >= CollisionHandler.EPSILON) 
			tY = (yBounds-ball1.radius-ball1.y)/ball1.speed.y;
		else if(ball1.speed.y <= -CollisionHandler.EPSILON)  
			tY = (ball1.radius-ball1.y)/ball1.speed.y;

		//Returning the closest crash. If closest crash is X then xOrYWall 
		//boolean = true. If closest crash is Y then XOrYWall boolean = false.
		if(CollisionHandler.EPSILON<=tX  && tX<tY) {
			CollisionHandler.xOrYWall = true;
			return tX;
		}
		if(CollisionHandler.EPSILON<=tY && tY<tX) {
			CollisionHandler.xOrYWall = false;
			return tY;
		}

		return CollisionHandler.NO_COLLISION;
	}

	/*
	 * A formula for calculating when the crash of an object with another object will happen 
	 * in two dimensions.
	 * 
	 * @param ball1- a ball we want to know when will hit the other ball.
	 * @param ball1- a ball we want to know when will hit the other ball.
	 * @return time when ball1 will the other ball. or CollisionHandler.NO_COLLISION if not.
	 */
	public static double formula5(Ball ball1, Ball ball2){

		Vector2D speed1_2 = ball1.speed.getSubtracted(ball2.speed);
		Vector2D n = new Vector2D(ball1.x-ball2.x, ball1.y-ball2.y);
		double r = ball1.radius + ball2.radius;

		double a = speed1_2.dot(speed1_2);
		double b = 2*n.dot(speed1_2);
		double c = n.dot(n) - (r*r);
		double dis = (b*b) -4*a*c;

		if(dis<=0) 
			return CollisionHandler.NO_COLLISION;


		double t1 = (-b-Math.sqrt(dis))/(2*a);
		double t2 = (-b+Math.sqrt(dis))/(2*a);
		//אין התנגשות - הכדורים מתרחקים זה מזה
		if(t1*t2 < 0) 
			return CollisionHandler.NO_COLLISION;
		//ההתנגשות בין בכדורים כבר בעצמה
		if(t1*t2 > 0 && t1+t2<0)
			return CollisionHandler.NO_COLLISION;
		//אין התנגשות - הכדורים עלולים להתנגש רק לא כרגע
		if(t1*t2 > 0 && t1+t2 > 0) {
			if(CollisionHandler.EPSILON<=t1  && t1<t2) {
				return t1;
			}
			if(CollisionHandler.EPSILON<=t2 && t2<t1) {
				return t2;
			}
		}
		return CollisionHandler.NO_COLLISION;

	}

	/*
	 * A formula for calculating when the crash of an object with the wall will happen 
	 * in two dimensions.
	 * 
	 * @param ball1- a ball we want to know when will hit the wall.
	 * @return time when ball1 will hit wall. or CollisionHandler.NO_COLLISION if not.
	 */
	public static double formula4WithAcceleration(Ball ball1, int xBounds, int yBounds, float mu) {
		//returning no collision if the ball has no speed at all.
		if(ball1.speed.x == 0 && ball1.speed.y == 0)
			return CollisionHandler.NO_COLLISION;
		//tX is time of crash with left and right walls.
		//tY is time of crash with up and down walls.
		//they are both reset to no collision.
		double tX = CollisionHandler.NO_COLLISION;
		double tY = CollisionHandler.NO_COLLISION;

		if(ball1.speed.x!=0) {
			double a = ball1.acceleration.x/2;
			double b = ball1.speed.x;
			double c = 0;
			if(ball1.speed.x>0) {
				c = ball1.x+ball1.radius-xBounds;
			}
			else if(ball1.speed.x-ball1.radius<0) {
				c = ball1.x;
			}
			double dis = (b*b) -4*a*c;
			
			double t1 = (-b-Math.sqrt(dis))/(2*a);
			double t2 = (-b+Math.sqrt(dis))/(2*a);
			
			if(dis<=0) 
				tX = CollisionHandler.NO_COLLISION;
			else if(t1*t2 < 0) 
				tX =  CollisionHandler.NO_COLLISION;
			//ההתנגשות בין בכדורים כבר בעצמה
			else if(t1*t2 > 0 && t1+t2<0)
				tX = CollisionHandler.NO_COLLISION;
			//אין התנגשות - הכדורים עלולים להתנגש רק לא כרגע
			else if(t1*t2 > 0 && t1+t2 > 0) {
				if(CollisionHandler.EPSILON<=t1  && t1<t2) {
					tX = t1;
				}
				else if(CollisionHandler.EPSILON<=t2 && t2<t1) {
					tX = t2;
				}
			}
			else
			tX = CollisionHandler.NO_COLLISION;

		}
		
		if(ball1.speed.y!=0) {
			double a = ball1.acceleration.y/2;
			double b = ball1.speed.y;
			double c = 0;
			if(ball1.speed.y>0) {
				c = ball1.y+ball1.radius-yBounds;
			}
			else if(ball1.speed.y<0) {
				c = ball1.y-ball1.radius;
			}
			double dis = (b*b) -4*a*c;
			
			double t1 = (-b-Math.sqrt(dis))/(2*a);
			double t2 = (-b+Math.sqrt(dis))/(2*a);
			
			if(dis<=0) 
				tY = CollisionHandler.NO_COLLISION;
			else if(t1*t2 < 0) 
				tY =  CollisionHandler.NO_COLLISION;
			//ההתנגשות בין בכדורים כבר בעצמה
			else if(t1*t2 > 0 && t1+t2<0)
				tY = CollisionHandler.NO_COLLISION;
			//אין התנגשות - הכדורים עלולים להתנגש רק לא כרגע
			else if(t1*t2 > 0 && t1+t2 > 0) {
				if(CollisionHandler.EPSILON<=t1  && t1<t2) {
					tY = t1;
				}
				else if(CollisionHandler.EPSILON<=t2 && t2<t1) {
					tY = t2;
				}
			}
			else
			tY = CollisionHandler.NO_COLLISION;

		}
		
		//Returning the closest crash. If closest crash is X then xOrYWall 
				//boolean = true. If closest crash is Y then XOrYWall boolean = false.
				if(CollisionHandler.EPSILON<=tX  && tX<tY) {
					CollisionHandler.xOrYWall = true;
					return tX;
				}
				if(CollisionHandler.EPSILON<=tY && tY<tX) {
					CollisionHandler.xOrYWall = false;
					return tY;
				}

				return CollisionHandler.NO_COLLISION;
			}

		

	







}

