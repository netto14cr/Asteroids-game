package finalproject.mvc.model;

import finalproject.mvc.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

import java.util.ArrayList;
import java.util.List;


public class Cc {

	private  int nNumFalcon;
	private  int nLevel;
	private  long lScore;
	private  Falcon falShip;
	private  boolean bPlaying;
	private  boolean bPaused;
	private int superpower;
    private int loadpower = 0;

    public int getSuperpower() {
        return superpower;
    }

    public int getLoadpower() {
        return loadpower;
    }

    public void setLoadpower(int loadpower) {
        this.loadpower = loadpower;
    }

    public void setSuperpower(int superpower) {
        this.superpower = superpower;
    }

    private int exp = 0;

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    // These ArrayLists with capacities set
	private List<Movable> movDebris = new ArrayList<Movable>(300);
	private List<Movable> movFriends = new ArrayList<Movable>(100);
	private List<Movable> movFoes = new ArrayList<Movable>(200);
	private List<Movable> movFloaters = new ArrayList<Movable>(50);

	private GameOpsList opsList = new GameOpsList();

	//added by Dmitriy
	private static Cc instance = null;

	// Constructor made private - static Utility class only
	private Cc() {}


	public static Cc getInstance(){
		if (instance == null){
			instance = new Cc();
		}
		return instance;
	}

    public void enemyGenerator(int n) {
        while (n > 0) {
            spawnEnemyFalcon(Game.R.nextInt(Game.DIM.width), 10);
            n--;
        }
    }

    public void enemyGenerator(int n, int speed) {
        while (n > 0) {
            spawnEnemyFalcon(Game.R.nextInt(Game.DIM.width), speed);
            n--;
        }
    }


	public  void initGame(){
		setLevel(1);
		setScore(0);
		setNumFalcons(10);
		spawnFalcon(true);
        setSuperpower(5);

        //enemyGenerator(10, 30);
        falShip.setFirelevel(1);

	}
	
	// The parameter is true if this is for the beginning of the game, otherwise false
	// When you spawn a new falcon, you need to decrement its number
	public  void spawnFalcon(boolean bFirst) {
		if (getNumFalcons() != 0) {
			falShip = new Falcon();
			//movFriends.enqueue(falShip);
			opsList.enqueue(falShip, CollisionOp.Operation.ADD);
			if (!bFirst)
			    setNumFalcons(getNumFalcons() - 1);
		}
		
		Sound.playSound("shipspawn.wav");

	}

	public void spawnEnemyFalcon(int x, int y) {
		Falcon enemyFalcon1 = new Falcon(x, y);
		opsList.enqueue(enemyFalcon1, CollisionOp.Operation.ADD);
	}

    public void spawnEnemyFalcon(int x, int y, int speed) {
        Falcon enemyFalcon1 = new Falcon(x, y);
        enemyFalcon1.setSpeed(speed);
        opsList.enqueue(enemyFalcon1, CollisionOp.Operation.ADD);
    }

    public void spawnBoss() {
        Falcon boss = new Falcon(true, 50);
        opsList.enqueue(boss, CollisionOp.Operation.ADD);
    }

    public void spawnBoss(int level) {
        Falcon boss = new Falcon(true, level * 20 + 30);
        boss.setFirelevel(level / 3 + 1);
        opsList.enqueue(boss, CollisionOp.Operation.ADD);
    }

	public GameOpsList getOpsList() {
		return opsList;
	}

	public void setOpsList(GameOpsList opsList) {
		this.opsList = opsList;
	}

	public  void clearAll(){
		movDebris.clear();
		movFriends.clear();
		movFoes.clear();
		movFloaters.clear();
	}

	public  boolean isPlaying() {
		return bPlaying;
	}

	public  void setPlaying(boolean bPlaying) {
		this.bPlaying = bPlaying;
	}

	public  boolean isPaused() {
		return bPaused;
	}

	public  void setPaused(boolean bPaused) {
		this.bPaused = bPaused;
	}
	
	public  boolean isGameOver() {		//if the number of falcons is zero, then game over
		if (getNumFalcons() == 0) {
			return true;
		}
		return false;
	}

	public  int getLevel() {
		return nLevel;
	}

	public   long getScore() {
		return lScore;
	}

	public  void setScore(long lParam) {
		lScore = lParam;
	}

	public  void setLevel(int n) {
		nLevel = n;
	}

	public  int getNumFalcons() {
		return nNumFalcon;
	}

	public  void setNumFalcons(int nParam) {
		nNumFalcon = nParam;
	}
	
	public  Falcon getFalcon(){
		return falShip;
	}
	
	public  void setFalcon(Falcon falParam){
		falShip = falParam;
	}

	public  List<Movable> getMovDebris() {
		return movDebris;
	}



	public  List<Movable> getMovFriends() {
		return movFriends;
	}



	public  List<Movable> getMovFoes() {
		return movFoes;
	}


	public  List<Movable> getMovFloaters() {
		return movFloaters;
	}




}
