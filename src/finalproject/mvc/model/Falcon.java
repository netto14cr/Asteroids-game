package finalproject.mvc.model;
 
import finalproject.mvc.controller.Game;

import java.awt.*;
import java.util.ArrayList;


public class Falcon extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================
	
	private final double THRUST = .65;
    private double speed = 10;
    private int score = 0;

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    private int tick = 0;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFirelevel() {

        return firelevel;
    }

    public void setFirelevel(int firelevel) {
        this.firelevel = firelevel;
    }

    private int firelevel = 1;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    final int DEGREE_STEP = 7;
	
	private boolean bShield = false;
	private boolean bFlame = false;
	private boolean bProtected; //for fade in and out
	
	private boolean bThrusting = false;
    private boolean dThrusting = false;
	private boolean bTurningRight = false;
	private boolean bTurningLeft = false;
	
	private int nShield;
			
	private final double[] FLAME = { 23 * Math.PI / 24 + Math.PI / 2,
			Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2 };

	private int[] nXFlames = new int[FLAME.length];
	private int[] nYFlames = new int[FLAME.length];

	private Point[] pntFlames = new Point[FLAME.length];
    private boolean isBoss = false;
    private int blood = 1;
    private int maxblood = 10;

    private int presuperpower = 0;

    public int getMaxblood() {
        return maxblood;
    }

    public void setMaxblood(int maxblood) {
        this.maxblood = maxblood;
    }


    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }


    public boolean isBoss() {
        return isBoss;
    }

    public void setIsBoss(boolean isBoss) {
        this.isBoss = isBoss;
    }

    // ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public Falcon() {
		super();
		setTeam(Team.FRIEND);
		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		// Robert Alef's awesome falcon design
		pntCs.add(new Point(0,9));
		pntCs.add(new Point(-1, 6));
		pntCs.add(new Point(-1,3));
		pntCs.add(new Point(-4, 1));
		pntCs.add(new Point(4,1));
		pntCs.add(new Point(-4,1));

		pntCs.add(new Point(-4, -2));
		pntCs.add(new Point(-1, -2));
		pntCs.add(new Point(-1, -9));
		pntCs.add(new Point(-1, -2));
		pntCs.add(new Point(-4, -2));

		pntCs.add(new Point(-10, -8));
		pntCs.add(new Point(-5, -9));
		pntCs.add(new Point(-7, -11));
		pntCs.add(new Point(-4, -11));
		pntCs.add(new Point(-2, -9));
		pntCs.add(new Point(-2, -10));
		pntCs.add(new Point(-1, -10));
		pntCs.add(new Point(-1, -9));
		pntCs.add(new Point(1, -9));
		pntCs.add(new Point(1, -10));
		pntCs.add(new Point(2, -10));
		pntCs.add(new Point(2, -9));
		pntCs.add(new Point(4, -11));
		pntCs.add(new Point(7, -11));
		pntCs.add(new Point(5, -9));
		pntCs.add(new Point(10, -8));
		pntCs.add(new Point(4, -2));

		pntCs.add(new Point(1, -2));
		pntCs.add(new Point(1, -9));
		pntCs.add(new Point(1, -2));
		pntCs.add(new Point(4,-2));


		pntCs.add(new Point(4, 1));
		pntCs.add(new Point(1, 3));
		pntCs.add(new Point(1,6));
		pntCs.add(new Point(0,9));

		assignPolarPoints(pntCs);

		setColor(Color.white);
		
		//put falcon in the middle.
		setCenter(new Point(Game.DIM.width / 2, Game.DIM.height * 18 / 20));
		
		//with random orientation
		setOrientation(270);
		
		//this is the size of the falcon
		setRadius(20);

		//these are falcon specific
		setProtected(true);
		setFadeValue(0);
	}

	public Falcon(int x, int y) {
		this();
		setColor(Color.RED);
        setOrientation(90);
		setTeam(Team.FOE);
        setProtected(false);
		setCenter(new Point(x, y));
	}

    public Falcon(boolean isBoss, int b) {
        this();
        if (isBoss == true) {

            setMaxblood(b);
            setBlood(getMaxblood());
            setColor(Color.YELLOW);
            setOrientation(90);
            setRadius(60);
            setTeam(Team.FOE);
            setProtected(false);
            setIsBoss(true);
            setCenter(new Point(Game.DIM.width / 2, Game.DIM.height * 2 / 20));
        } else {
            return;
        }
    }

    public ArrayList<Bullet> fire() {
        ArrayList<Bullet> result = new ArrayList<Bullet>();
        if (this.getTeam() == Team.FRIEND) {
            if (firelevel == 1) {
                result.add(new Bullet(this, 0.0, - 30.0));
            } else if (firelevel == 2) {
                result.add(new Bullet(this, 0.0, - 30.0));
                result.add(new Bullet(this, 5.40, - 29.5));
                result.add(new Bullet(this, - 5.40, - 29.5));
                //result.add(new Bullet(this, ))
            } else if (firelevel == 3) {
                result.add(new Bullet(this, 0.0, - 30.0));
                result.add(new Bullet(this, 5.40, - 29.5));
                result.add(new Bullet(this, - 5.40, - 29.5));
                result.add(new Bullet(this, 10.77, - 28));
                result.add(new Bullet(this, - 10.77, - 28));
            }
            return result;
        } else {
            if (firelevel == 1) {
                result.add(new Bullet(this, 0.0, 10.0));
            } else if (firelevel == 2) {
                result.add(new Bullet(this, 0.0, 10.0));
                result.add(new Bullet(this, 5.40, 9.5));
                result.add(new Bullet(this, - 5.40, 9.5));
                //result.add(new Bullet(this, ))
            } else if (firelevel == 3) {
                result.add(new Bullet(this, 0.0, 10.0));
                result.add(new Bullet(this, 5.40, 9.5));
                result.add(new Bullet(this, - 5.40, 9.5));
                result.add(new Bullet(this, 10.77, 8));
                result.add(new Bullet(this, - 10.77, 8));
            }
            return result;
        }
    }

    public void superFire() {
        SuperFire sf = new SuperFire(getCenter().x, getCenter().y, getRadius());
        Cc.getInstance().getOpsList().enqueue(sf, CollisionOp.Operation.ADD);
    }


	
	
	// ==============================================================
	// METHODS 
	// ==============================================================

    public int getPresuperpower() {
        return presuperpower;
    }

    public void setPresuperpower(int presuperpower) {
        this.presuperpower = presuperpower;
    }

    public void move() {
		super.move();

		if (bThrusting) {
            bFlame = true;
            //setDeltaY(0 - speed);
		}
//        if (dThrusting) {
//            setDeltaY(speed);
//        }
//		if (bTurningLeft) {
//
//			setDeltaX(0 - speed);
//		}
//		if (bTurningRight) {
//			setDeltaX(speed);
//		}
        if (presuperpower != 0) {
            if (presuperpower == 1) {
                setProtected(true);
            }
            if (presuperpower >= 20) {
                setProtected(false);
                this.presuperpower = 0;
                this.superFire();
            } else {
                presuperpower ++;
            }

        }


		//implementing the fadeInOut functionality - added by Dmitriy
		if (getProtected()) {
			setFadeValue(getFadeValue() + 3);
		}
		if (getFadeValue() == 255) {
			setProtected(false);
		}

        if (getTeam() == Team.FRIEND) {
            if (getCenter().x > Game.DIM.width - getRadius()) {
                setCenter(new Point(Game.DIM.width - getRadius(), getCenter().y));
            }
            if (getCenter().x < 0 + getRadius()) {
                setCenter(new Point(0 + getRadius(), getCenter().y));
            }
            if (getCenter().y > Game.DIM.height - getRadius() * 3) {
                setCenter(new Point(getCenter().x, Game.DIM.height - getRadius() * 3));
            }
        }

        if (getTeam() == Team.FOE) {
            tick ++ ;
            if (isBoss() && tick % (int)((double) 10 + (double) 20 * ((double) 50 / (double) maxblood)) == 0) {
                for (Bullet b : this.fire()) {
                    Cc.getInstance().getOpsList().enqueue(b, CollisionOp.Operation.ADD);
                }
            }

            if (tick % 40 == 0) {
                for (Bullet b : this.fire()) {
                    Cc.getInstance().getOpsList().enqueue(b, CollisionOp.Operation.ADD);
                }

            }
            if (isBoss()) {
                //setDeltaX(4);
                if (Math.abs(getDeltaX()) < 0.001) {
                    setDeltaX(4);
                }
                if (getCenter().x > Game.DIM.width * 3 / 4) {
                        setDeltaX(0 - 4);
                } else if (getCenter().x < Game.DIM.width * 1 / 4) {
                        setDeltaX(4);
                }
            } else {
                if (Math.abs(getDeltaX()) < 0.001) {
                    setDeltaY(1 + Game.R.nextInt((int)speed / 2));
                    setDeltaX((speed - Game.R.nextInt((int)speed * 2)) / 2);
                }
            }
        }

	} //end move

	public void rotateLeft() {
		bTurningLeft = true;
        setDeltaX(0 - speed);
	}

	public void rotateRight() {
		bTurningRight = true;
        setDeltaX(speed);
	}

	public void stopRotating() {
		bTurningRight = false;
		bTurningLeft = false;
	}

	public void thrustOn() {
		bThrusting = true;
        setDeltaY(0 - speed);
	}

    public void dethrustOn() {
        dThrusting = true;
        setDeltaY(speed);
    }

    public void stopUp() {
        bFlame = false;
        bThrusting = false;
        setDeltaY(0);
    }

    public void stopDown() {
        bFlame = false;

        dThrusting = false;

        setDeltaY(0);
    }

    public void stopLeft() {
        bFlame = false;


        bTurningLeft = false;

        setDeltaX(0);
    }

    public void stopRight() {
        bFlame = false;

        bTurningRight = false;


        setDeltaX(0);
    }

	public void thrustOff() {
		bThrusting = false;
        dThrusting = false;
		bFlame = false;
	}

	private int adjustColor(int nCol, int nAdj) {
		if (nCol - nAdj <= 0) {
			return 0;
		} else {
			return nCol - nAdj;
		}
	}

	public void draw(Graphics g) {

//        if (getTeam() == Team.FRIEND) {
//            Color c = g.getColor();
//            g.setColor(Color.white);
//            g.setFont(new Font("SansSerif", Font.BOLD, 12));
//            g.drawString("SCORE: " + this.score, 20, 20);
//            g.setColor(c);
//        }
		//does the fading at the beginning or after hyperspace
		Color colShip;

		if (getProtected() == false) {
			colShip = Color.white;
		} else {
			colShip = Color.blue;
		}

        if (getTeam() == Team.FOE) {
            colShip = Color.RED;
            if (isBoss() == true) {
                colShip = Color.YELLOW;
            }
        }


        if (getProtected() == true) {
            g.setColor(Color.BLUE);
			g.drawOval(getCenter().x - getRadius(),
					getCenter().y - getRadius() + 3, getRadius() * 2,
					getRadius() * 2);
        }

        if (isBoss() == true) {
            g.setColor(Color.PINK);
            int maxlength = getRadius() * 2 + 80;
            g.drawRect(getCenter().x - getRadius() - 40, getCenter().y - getRadius() * 1, maxlength, getRadius() / 15);
            g.fillRect(getCenter().x - getRadius() - 40, getCenter().y - getRadius() * 1, maxlength * blood / maxblood, getRadius() / 15);
            g.setColor(Color.yellow);

        }

//		//shield on
//		if (bShield && nShield > 0) {
//
//			setShield(getShield() - 1);
//
//			g.setColor(Color.cyan);
//			g.drawOval(getCenter().x - getRadius(),
//					getCenter().y - getRadius(), getRadius() * 2,
//					getRadius() * 2);
//
//		} //end if shield

		//thrusting
		if (bFlame) {
			g.setColor(colShip);
			//the flame
			for (int nC = 0; nC < FLAME.length; nC++) {
				if (nC % 2 != 0) //odd
				{
					pntFlames[nC] = new Point((int) (getCenter().x + 2
							* getRadius()
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])), (int) (getCenter().y - 2
							* getRadius()
							* Math.cos(Math.toRadians(getOrientation())
									+ FLAME[nC])));

				} else //even
				{
					pntFlames[nC] = new Point((int) (getCenter().x + getRadius()
							* 1.1
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])),
							(int) (getCenter().y - getRadius()
									* 1.1
									* Math.cos(Math.toRadians(getOrientation())
											+ FLAME[nC])));

				} //end even/odd else

			} //end for loop

			for (int nC = 0; nC < FLAME.length; nC++) {
				nXFlames[nC] = pntFlames[nC].x;
				nYFlames[nC] = pntFlames[nC].y;

			} //end assign flame points

			//g.setColor( Color.white );
			g.fillPolygon(nXFlames, nYFlames, FLAME.length);

		} //end if flame

		drawShipWithColor(g, colShip);

	} //end draw()

	public void drawShipWithColor(Graphics g, Color col) {
		super.draw(g);
		g.setColor(col);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}


	public void setProtected(boolean bParam) {
		if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

	public void setProtected(boolean bParam, int n) {
		if (bParam && n % 3 == 0) {
			setFadeValue(n);
		} else if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}	

	public boolean getProtected() {return bProtected;}
	public void setShield(int n) {nShield = n;}
	public int getShield() {return nShield;}	
	
} //end class
