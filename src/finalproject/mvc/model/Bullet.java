package finalproject.mvc.model;

import finalproject.mvc.controller.Game;

import java.awt.*;
import java.util.ArrayList;


public class Bullet extends Sprite {

	  private double FIRE_POWER = 30.0;

	 
	
public Bullet(Falcon fal){
		
		super();
	    setTeam(fal.getTeam());
		
		//defined the points on a cartesean grid
		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		pntCs.add(new Point(0,3)); //top point
		
		pntCs.add(new Point(1,-1));
		pntCs.add(new Point(0,-2));
		pntCs.add(new Point(-1,-1));

		assignPolarPoints(pntCs);

		//a bullet expires after 20 frames
	    setExpire(100);
	    setRadius(4);
	    

	    //everything is relative to the falcon ship that fired the bullet
	    setDeltaX( fal.getDeltaX() +
	               Math.cos( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
	    setDeltaY( fal.getDeltaY() +
	               Math.sin( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
	    setCenter( fal.getCenter() );

	    //set the bullet orientation to the falcon (ship) orientation
	    setOrientation(fal.getOrientation());


	}

    public Bullet(Falcon fal, double x_speed, double y_speed) {
        this(fal);
        setDeltaY(y_speed);
        setDeltaX(x_speed);
    }

    @Override
    public void draw(Graphics g) {
        if (getTeam() == Team.FOE) {
            setColor(Color.GRAY);
        }

        super.draw(g);
    }

    //implementing the expire functionality in the move method - added by Dmitriy
	public void move(){

		super.move();

		if (getExpire() == 0)
			Cc.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
		else
			setExpire(getExpire() - 1);

		if (getCenter().x < 0 || getCenter().x > Game.DIM.width || getCenter().y < 0 || getCenter().y > Game.DIM.height) {
            Cc.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
        }
	}

}
