package finalproject.mvc.controller;

import finalproject.mvc.model.*;
import finalproject.mvc.view.GamePanel;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener, MouseMotionListener, MouseListener {

    // ===============================================
    // FIELDS
    // ===============================================

    public static final Dimension DIM = new Dimension(1100, 750); //the dimension of the game.
    public final static int ANI_DELAY = 45; // milliseconds between screen
    private static final int SPAWN_NEW_SHIP_FLOATER = 1200;
    public static Random R = new Random();
    private final int PAUSE = 80, // p key
            QUIT = 81, // q key
            LEFT = 37, // rotate left; left arrow
            RIGHT = 39, // rotate right; right arrow
            UP = 38, // thrust; up arrow
            START = 83, // s key
            FIRE = 32, // space key
            MUTE = 77, // m-key mute
            DOWN = 40, // down-key
            SUPER_B = 66, // super power

    // for possible future use
    // HYPER = 68, 					// d key
    // SHIELD = 65, 				// a key arrow
    // NUM_ENTER = 10, 				// hyp
    SPECIAL = 70;                    // fire special weapon;  F key
    private GamePanel gmpPanel;
    // updates (animation)
    private Thread thrAnim;    //main thread.
    private int nLevel = 1;
    private int nTick = 0;
    private boolean bMuted = true;
    private Clip clpThrust;
    private Clip clpMusicBackground;


    // ===============================================
    // ==CONSTRUCTOR
    // ===============================================

    public Game() {

        gmpPanel = new GamePanel(DIM);
        gmpPanel.addKeyListener(this);
        gmpPanel.addMouseMotionListener(this);
        gmpPanel.addMouseListener(this);

        clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
        clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");


    }

    // ===============================================
    // ==METHODS
    // ===============================================

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
            public void run() {
                try {
                    Game game = new Game(); // construct itself
                    game.fireUpAnimThread();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Varargs for stopping looping-music-clips
    private static void stopLoopingSounds(Clip... clpClips) {
        for (Clip clp : clpClips) {
            clp.stop();
        }
    }

    private void fireUpAnimThread() { // called initially
        if (thrAnim == null) {
            thrAnim = new Thread(this); // pass the thread a runnable object (this)
            thrAnim.start();
        }
    }

    // implements runnable - must have run method
    public void run() {

        // lower this thread's priority; let the "main" aka 'Event Dispatch'
        // thread do what it needs to do first
        thrAnim.setPriority(Thread.MIN_PRIORITY);

        // and get the current time
        long lStartTime = System.currentTimeMillis();

        // this thread animates the scene
        while (Thread.currentThread() == thrAnim) {
            tick();
            updateGameObject();
            //spawnNewShipFloater();
            gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must
            // surround the sleep() in a try/catch block
            // this simply controls delay time between
            // the frames of the animation

            //this might be a good place to check for collisions
            checkCollisions();
            //this might be a god place to check if the level is clear (no more foes)
            //if the level is clear then spawn some big asteroids -- the number of asteroids
            //should increase with the level.
            //checkNewLevel();

            try {
                // The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update)
                // between frames takes longer than ANI_DELAY, then the difference between lStartTime -
                // System.currentTimeMillis() will be negative, then zero will be the sleep time
                lStartTime += ANI_DELAY;
                Thread.sleep(Math.max(0,
                        lStartTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // just skip this frame -- no big deal
                continue;
            }
        } // end while
    } // end run

    private boolean isBossDestroied() {
        if (nTick < 450) {
            return false;
        }
        for (Movable movFoe : Cc.getInstance().getMovFoes()) {
            if (movFoe instanceof Falcon) {
                if (((Falcon) movFoe).isBoss()) {

                    return false;
                }
            }
        }
        return true;
    }

    private void updateGameObject() {

        if (nTick % 80 == 0) {
            Cc.getInstance().enemyGenerator(nLevel, 3 + nLevel * 2);
        }


        if (nTick == 400) {
            Cc.getInstance().spawnBoss(nLevel);
        }


        if (nTick > 425 && isBossDestroied()) {
            destroyAll();
            nextLevel();
        }
    }

    private void nextLevel() {
        nLevel = nLevel + 1;
        Cc.getInstance().setLevel(nLevel);
        nTick = 0;
    }

    private void destroyAll() {
        for (Movable movFoe : Cc.getInstance().getMovFoes()) {
            Cc.getInstance().getOpsList().enqueue(new Bomb(movFoe.getCenter().x, movFoe.getCenter().y, movFoe.getRadius()), CollisionOp.Operation.ADD);
            Cc.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);
        }
    }

    private void checkCollisions() {


        Point pntFriendCenter, pntFoeCenter;
        int nFriendRadiux, nFoeRadiux;

        for (Movable movFriend : Cc.getInstance().getMovFriends()) {
            for (Movable movFoe : Cc.getInstance().getMovFoes()) {

                pntFriendCenter = movFriend.getCenter();
                pntFoeCenter = movFoe.getCenter();
                nFriendRadiux = movFriend.getRadius();
                nFoeRadiux = movFoe.getRadius();

                //detect collision
                if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {

                    //falcon
                    if ((movFriend instanceof Falcon)) {
                        if (!Cc.getInstance().getFalcon().getProtected()) {
                            Cc.getInstance().getOpsList().enqueue(new Bomb(movFriend.getCenter().x, movFriend.getCenter().y, movFriend.getRadius()), CollisionOp.Operation.ADD);
                            Cc.getInstance().getOpsList().enqueue(movFriend, CollisionOp.Operation.REMOVE);
                            Cc.getInstance().spawnFalcon(false);

                            Cc.getInstance().setExp(0);
                        }
                    } else if (movFriend instanceof SuperFire) {
                        killEnemy(movFoe, true);
                    }

                    //not the falcon
                    else {
                        Cc.getInstance().getOpsList().enqueue(new Bomb(movFriend.getCenter().x, movFriend.getCenter().y, movFriend.getRadius()), CollisionOp.Operation.ADD);
                        Cc.getInstance().getOpsList().enqueue(movFriend, CollisionOp.Operation.REMOVE);
                    }//end else
                    //kill the foe and if asteroid, then spawn new asteroids
                    //killFoe(movFoe);
                    killEnemy(movFoe);
                    Sound.playSound("kapow.wav");

                }//end if
            }//end inner for
        }//end outer for


        //check for collisions between falcon and floaters
//		if (Cc.getInstance().getFalcon() != null){
//			Point pntFalCenter = Cc.getInstance().getFalcon().getCenter();
//			int nFalRadiux = Cc.getInstance().getFalcon().getRadius();
//			Point pntFloaterCenter;
//			int nFloaterRadiux;
//
//			for (Movable movFloater : Cc.getInstance().getMovFloaters()) {
//				pntFloaterCenter = movFloater.getCenter();
//				nFloaterRadiux = movFloater.getRadius();
//
//				//detect collision
//				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
//
//					Cc.getInstance().getOpsList().enqueue(movFloater, CollisionOp.Operation.REMOVE);
//					Sound.playSound("pacman_eatghost.wav");
//
//				}//end if
//			}//end inner for
//		}//end if not null


        //we are dequeuing the opsList and performing operations in serial to avoid mutating the movable arraylists while iterating them above
        while (!Cc.getInstance().getOpsList().isEmpty()) {
            CollisionOp cop = Cc.getInstance().getOpsList().dequeue();
            Movable mov = cop.getMovable();
            CollisionOp.Operation operation = cop.getOperation();

            switch (mov.getTeam()) {
                case FOE:
                    if (operation == CollisionOp.Operation.ADD) {
                        Cc.getInstance().getMovFoes().add(mov);
                    } else {
                        Cc.getInstance().getMovFoes().remove(mov);
                    }

                    break;
                case FRIEND:
                    if (operation == CollisionOp.Operation.ADD) {
                        Cc.getInstance().getMovFriends().add(mov);
                    } else {
                        Cc.getInstance().getMovFriends().remove(mov);
                    }
                    break;

                case FLOATER:
                    if (operation == CollisionOp.Operation.ADD) {
                        Cc.getInstance().getMovFloaters().add(mov);
                    } else {
                        Cc.getInstance().getMovFloaters().remove(mov);
                    }
                    break;

                case DEBRIS:
                    if (operation == CollisionOp.Operation.ADD) {
                        Cc.getInstance().getMovDebris().add(mov);
                    } else {
                        Cc.getInstance().getMovDebris().remove(mov);
                    }
                    break;


            }

        }
        //a request to the JVM is made every frame to garbage collect, however, the JVM will choose when and how to do this
        System.gc();

    }//end meth

    private void killEnemy(Movable movFoe, boolean isSuper) {
        if (isSuper == true) {
            if ((movFoe instanceof Falcon)) {
                if (((Falcon) movFoe).isBoss() == true) {
                    ((Falcon) movFoe).setBlood(((Falcon) movFoe).getBlood() - 1);
                    if (((Falcon) movFoe).getBlood() == 0) {
                        Cc.getInstance().getOpsList().enqueue(new Bomb(movFoe.getCenter().x, movFoe.getCenter().y, movFoe.getRadius()), CollisionOp.Operation.ADD);
                        Cc.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);
                    }
                } else {

                    Cc.getInstance().getOpsList().enqueue(new Bomb(movFoe.getCenter().x, movFoe.getCenter().y, movFoe.getRadius()), CollisionOp.Operation.ADD);
                    Cc.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);
                }
                Cc.getInstance().setScore(Cc.getInstance().getScore() + 10);
                int exp = Cc.getInstance().getExp();

            } else if (movFoe instanceof Bullet) {
                Cc.getInstance().getOpsList().enqueue(new Bomb(movFoe.getCenter().x, movFoe.getCenter().y, movFoe.getRadius()), CollisionOp.Operation.ADD);
                Cc.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);
            }
        }
    }

    private void killEnemy(Movable movFoe) {
        if ((movFoe instanceof Falcon)) {
            if (((Falcon) movFoe).isBoss() == true) {
                ((Falcon) movFoe).setBlood(((Falcon) movFoe).getBlood() - 1);
                if (((Falcon) movFoe).getBlood() == 0) {
                    Cc.getInstance().getOpsList().enqueue(new Bomb(movFoe.getCenter().x, movFoe.getCenter().y, movFoe.getRadius()), CollisionOp.Operation.ADD);
                    Cc.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);
                }
                Cc.getInstance().setLoadpower(Cc.getInstance().getLoadpower() + 1);
            } else {

                Cc.getInstance().getOpsList().enqueue(new Bomb(movFoe.getCenter().x, movFoe.getCenter().y, movFoe.getRadius()), CollisionOp.Operation.ADD);
                Cc.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);
                Cc.getInstance().setLoadpower(Cc.getInstance().getLoadpower() + 1);
            }
            Cc.getInstance().setScore(Cc.getInstance().getScore() + 100);
            int exp = Cc.getInstance().getExp();
            Cc.getInstance().setExp(exp + 1);
            if (Cc.getInstance().getExp() >= 10) {
                Cc.getInstance().getFalcon().setFirelevel(2);
            }
            if (Cc.getInstance().getExp() >= 100) {
                Cc.getInstance().getFalcon().setFirelevel(3);
            }

            if (Cc.getInstance().getLoadpower() >= 100) {
                Cc.getInstance().setLoadpower(0);
                Cc.getInstance().setSuperpower(Cc.getInstance().getSuperpower() + 1);
            }

        }
    }

    private void killFoe(Movable movFoe) {

        if (movFoe instanceof Asteroid) {

            //we know this is an Asteroid, so we can cast without threat of ClassCastException
            Asteroid astExploded = (Asteroid) movFoe;
            //big asteroid
            if (astExploded.getSize() == 0) {
                //spawn two medium Asteroids
                Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
                Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);

            }
            //medium size aseroid exploded
            else if (astExploded.getSize() == 1) {
                //spawn three small Asteroids
                Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
                Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
                Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);

            }

        }

        //remove the original Foe
        Cc.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);

    }

    //some methods for timing events in the game,
    //such as the appearance of UFOs, floaters (power-ups), etc.
    public void tick() {
        if (nTick == Integer.MAX_VALUE)
            nTick = 0;
        else
            nTick++;
    }

    public int getTick() {
        return nTick;
    }

    private void spawnNewShipFloater() {
        //make the appearance of power-up dependent upon ticks and levels
        //the higher the level the more frequent the appearance
        if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 20) == 0) {
            //Cc.getInstance().getMovFloaters().enqueue(new NewShipFloater());
            Cc.getInstance().getOpsList().enqueue(new NewShipFloater(), CollisionOp.Operation.ADD);
        }
    }

    // Called when user presses 's'
    private void startGame() {
        Cc.getInstance().clearAll();
        Cc.getInstance().initGame();
        Cc.getInstance().setLevel(1);
        Cc.getInstance().setPlaying(true);
        Cc.getInstance().setPaused(false);
        //if (!bMuted)
        // clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
    }

    //this method spawns new asteroids
    private void spawnAsteroids(int nNum) {
        for (int nC = 0; nC < nNum; nC++) {
            //Asteroids with size of zero are big
            Cc.getInstance().getOpsList().enqueue(new Asteroid(0), CollisionOp.Operation.ADD);

        }
    }

    private boolean isLevelClear() {
        //if there are no more Asteroids on the screen
        boolean bAsteroidFree = true;
        for (Movable movFoe : Cc.getInstance().getMovFoes()) {
            if (movFoe instanceof Asteroid) {
                bAsteroidFree = false;
                break;
            }
        }

        return bAsteroidFree;


    }

    private void checkNewLevel() {

        if (isLevelClear()) {
            if (Cc.getInstance().getFalcon() != null)
                Cc.getInstance().getFalcon().setProtected(true);

            spawnAsteroids(Cc.getInstance().getLevel() + 2);
            Cc.getInstance().setLevel(Cc.getInstance().getLevel() + 1);

        }
    }

    // ===============================================
    // KEYLISTENER METHODS
    // ===============================================

    @Override
    public void keyPressed(KeyEvent e) {
        Falcon fal = Cc.getInstance().getFalcon();
        int nKey = e.getKeyCode();
        // System.out.println(nKey);

        if (nKey == START && !Cc.getInstance().isPlaying())
            startGame();

        if (fal != null) {

            switch (nKey) {
                case PAUSE:
                    Cc.getInstance().setPaused(!Cc.getInstance().isPaused());
                    if (Cc.getInstance().isPaused())
                        stopLoopingSounds(clpMusicBackground, clpThrust);
                    else
                        clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
                    break;
                case QUIT:
                    System.exit(0);
                    break;
                case UP:
                    fal.thrustOn();
                    if (!Cc.getInstance().isPaused())
                        clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
                    break;
                case DOWN:
                    fal.dethrustOn();
                    if (!Cc.getInstance().isPaused())
                        clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
                    break;
                case LEFT:
                    fal.rotateLeft();
                    break;
                case RIGHT:
                    fal.rotateRight();
                    break;
                case SUPER_B:
                    if (Cc.getInstance().getSuperpower() < 1) {
                        break;
                    } else {
                        if (fal.getPresuperpower() == 0) {
                            fal.setPresuperpower(1);
                            Cc.getInstance().setSuperpower(Cc.getInstance().getSuperpower() - 1);
                        }
                    }
                    break;

                // possible future use
                // case KILL:
                // case SHIELD:
                // case NUM_ENTER:

                default:
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Falcon fal = Cc.getInstance().getFalcon();
        int nKey = e.getKeyCode();
        System.out.println(nKey);

        if (fal != null) {
            switch (nKey) {
                case FIRE:
                    for (Bullet b : fal.fire()) {
                        Cc.getInstance().getOpsList().enqueue(b, CollisionOp.Operation.ADD);
                    }
                    Sound.playSound("laser.wav");
                    break;

                //special is a special weapon, current it just fires the cruise missile.
                case SPECIAL:
                    Cc.getInstance().getOpsList().enqueue(new Cruise(fal), CollisionOp.Operation.ADD);
                    //Sound.playSound("laser.wav");
                    break;

                case LEFT:
                    //fal.stopRotating();
                    fal.stopLeft();
                    break;
                case RIGHT:
                    //fal.stopRotating();
                    fal.stopRight();
                    break;
                case UP:
                    //fal.thrustOff();
                    fal.stopUp();
                    clpThrust.stop();
                    break;
                case DOWN:
                    //fal.thrustOff();
                    fal.stopDown();
                    clpThrust.stop();
                    break;
                case MUTE:
                    if (!bMuted) {
                        stopLoopingSounds(clpMusicBackground);
                        bMuted = !bMuted;
                    } else {
                        clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
                        bMuted = !bMuted;
                    }
                    break;


                default:
                    break;
            }
        }
    }

    @Override
    // Just need it b/c of KeyListener implementation
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
//		Cc.getInstance().getOpsList()
//				.enqueue(new Bomb(e), CollisionOp.Operation.ADD);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
//		gmpPanel.setmMouseEvent(e);
    }
}


