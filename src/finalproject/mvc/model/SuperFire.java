package finalproject.mvc.model;

import finalproject.mvc.controller.Game;

import java.awt.*;
import java.util.ArrayList;


public class SuperFire extends Sprite {

	//public static final int EXPIRE = 15;
	private final double FIRE_POWER = 35.0;
	private int original_x;
    private int original_y;

    public void setOriginalOpsition(int x, int y) {
        this.original_x = x;
        this.original_y = y;
    }



public SuperFire(int x, int y, int r){
		
		super();
	    setTeam(Team.FRIEND);
		
		//defined the points on a cartesean grid
//		ArrayList<Point> pntCs = new ArrayList<Point>();
//
//
//		pntCs.add(new Point(0,3)); //top point
//		pntCs.add(new Point(1,-1));
//		pntCs.add(new Point(0,-2));
//		pntCs.add(new Point(-1,-1));
//
//		assignPolarPoints(pntCs);

		//a bullet expires after 20 frames
	    //setExpire(EXPIRE);
	    //setRadius(6);
	    

	    //everything is relative to the falcon ship that fired the bullet
		//setCenter(e.getPoint());

	    //set the bullet orientation to the falcon (ship) orientation
        setCenter(new Point(x, y));
        setOriginalOpsition(x, y);
        setRadius(r);

	}

	//implementing the expire functionality in the move method - added by Dmitriy
	public void move(){

		super.move();

		if ( getRadius() > Game.DIM.width / 2)
			Cc.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
		else {
			setRadius(getRadius() + 20);
        }


	}

    @Override
    public void draw(Graphics g) {
        //super.draw(g);
        g.setColor(new Color(Game.R.nextInt(256), Game.R.nextInt(256), Game.R.nextInt(256)));
        g.drawOval(getCenter().x - getRadius(), getCenter().y - getRadius(), getRadius() * 2, getRadius() * 2);
    }
}
