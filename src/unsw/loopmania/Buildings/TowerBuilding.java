package unsw.loopmania.Buildings;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Building;

/**
 * During a battle within its shooting radius, enemies will be attacked by the tower
 */
public class TowerBuilding extends Building {
    
    static final private int SHOOTING_RADIUS = 3;

    // The damage an enemy entity will receive during each iteration
    static final private int DAMAGE = 5;

    public TowerBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/tower.png");
    }

    /**
     * for every enemy in enemies, will reduce their health by 1
     * the idea is that this is meant to represent just one iteration
     * @param enemies list of enemies that are actively in battle
     */
    public void attackEnemies(ArrayList<BasicEnemy> enemies) {
        for (BasicEnemy e: enemies) {
            // check if tower in range
            if (Math.pow((e.getX()-this.getX()), 2) +  Math.pow((e.getY()-this.getY()), 2) <= SHOOTING_RADIUS) {
                e.setHealth(e.getHealth() - DAMAGE);
            }
        }
    }

    public int getShootingRadius() {
        return SHOOTING_RADIUS;
    }
    public int getDamage() {
        return DAMAGE;
    }

}