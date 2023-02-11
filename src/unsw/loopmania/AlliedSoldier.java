package unsw.loopmania;

import java.util.List;
import org.javatuples.Pair;

import unsw.loopmania.Enemies.Zombie;
import unsw.loopmania.Frontend.MovingAnimatedEntity;

public class AlliedSoldier extends MovingAnimatedEntity implements EnemyCreator {

    /**
     * full health
     */
    private int health = 25;
    /**
     * DAMAGE given to enemies each attack
     */
    static final private int DAMAGE = 5;
    /**
     * set to true if turned to zombie from critcal bite
     */
    private Boolean isZombie = false;
    /**
     * set to null if it was not created from an enemy
     * during trance, else set to that enemy
     */
    private BasicEnemy prevEnemy;

    /**
     * if it's a transformed enemy, store
     * how many rounds left to turn back to an enemy
     * -1 means is not an enemy
     */
    private int isEnemy = -1;


    /**
     * Animation is inherent to the character, and these track the status
     */

    // magic numbers for animation
    static final private int SPRITE_SHEET_COLUMNS = 4;
    static final private int SPRITE_SHEET_COUNT = 6;
    static final private int SPRITE_SHEET_OFFSET_X = 0;

    public AlliedSoldier(PathPosition position) {
        super(position, "src/images/alliedsoldierrightwalk.png", SPRITE_SHEET_OFFSET_X, SPRITE_SHEET_COLUMNS, SPRITE_SHEET_COUNT);
    }

    /**
     * use this constructor when an enemy created this soldier
     * @param position soldier's path
     * @param enemy enemy that created the soldier
     */
    public AlliedSoldier(PathPosition position, BasicEnemy enemy) {
        super(position, "src/images/alliedsoldierrightwalk.png", SPRITE_SHEET_OFFSET_X, SPRITE_SHEET_COLUMNS, SPRITE_SHEET_COUNT);
        this.prevEnemy = enemy;
    }


    public void attack(BasicEnemy enemy) {
        enemy.setHealth(enemy.getHealth() - DAMAGE);
    }

    /**
     * character natrually moves down path
     */
    public void move() {
        this.moveDownPath();
    }

    /**
     * get path that can be assigned to newly created enemy
     * @param orderedPath passed form loopManiaworld, where characters can walk
     * @return path for the enemy
     */
    public PathPosition getEnemyPath(List<Pair<Integer, Integer>> orderedPath) {
        int xPos = this.getX();
        int yPos = this.getY();

        int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(xPos, yPos));
        Pair<Integer, Integer> spawnPosition = orderedPath.get(indexPosition);
        int indexInPath = orderedPath.indexOf(spawnPosition);
        
        PathPosition enemypath = new PathPosition(indexInPath, orderedPath);
        return enemypath;
    }

    /** 
     * create an enemy on same position as where allied soldir currntly is
     * @param orderedPath the path of the world characters can walk on
     * @return new enemy created
     */
    public BasicEnemy spawnEnemy(List<Pair<Integer, Integer>> orderedPath, Character c, Boolean inBattle) {
        if (isZombie) {
            // If soldier has undergone a critical bite from zombie
            // it will never transform back to a soldier :(
            PathPosition zombiePath = getEnemyPath(orderedPath);
            Zombie newZombie = new Zombie(zombiePath);
            return newZombie;
        }
        if (isEnemy == 0) {
            // if soldier used to be an enemy that was converted to solder
            // via a staff, soldier will need t be converted back
            prevEnemy.addToGrid();
            prevEnemy.setIsAlliedSolider(-1);
            if (inBattle) {
                return prevEnemy;
            }
        } else if (isEnemy != -1) {
            isEnemy -= 1;
        }
        return null;
    }

    /**
     * GETTERS AND SETTERS
     */

    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getDamage() {
        return DAMAGE;
    }

    public void setPrevEnemy(BasicEnemy prevEnemy) {
        this.prevEnemy = prevEnemy;
    }
    
    public Boolean getIsZombie() {
        return isZombie;
    }
    public void setIsZombie(Boolean isZombie) {
        this.isZombie = isZombie;
    }
    public int getIsEnemy() {
        return isEnemy;
    }
    public void setIsEnemy(int isEnemy) {
        this.isEnemy = isEnemy;
    }

    public BasicEnemy getPrevEnemy() {
        return prevEnemy;
    }

    @Override
    public void updateAnimation(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle) {
        int indexPosition = getNextIndexPositionClock(orderedPath, true);
        updateImageView(orderedPath, inActiveBattle, indexPosition, "alliedsoldier");
    }

}